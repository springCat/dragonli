package org.springcat.dragonli.core;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LFUCache;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ecwid.consul.v1.health.HealthServicesRequest;
import com.ecwid.consul.v1.health.model.HealthService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import org.springcat.dragonli.core.rpc.*;
import org.springcat.dragonli.core.consul.ConsulUtil;
import org.springcat.dragonli.jfinal.serialize.FastJsonSerialize;
import org.springcat.dragonli.jfinal.httpclient.HttpclientTransform;
import org.springcat.dragonli.jfinal.loadbalance.ConsistentHashRule;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class HttpInvoker {

    private static final Log log = LogFactory.get(HttpInvoker.class);

    private static TimedCache<String, List<HealthService>> serviceCache = CacheUtil.newTimedCache(1000);
    private static ILoadBalanceRule loadBalanceRule = new ConsistentHashRule();
    private static ISerialize serialize = new FastJsonSerialize();
    private static IHttpTransform httpTransform = new HttpclientTransform();
    private static LFUCache<String, CircuitBreaker> circuitBreakerCache = CacheUtil.newLFUCache(10000);
    private static LFUCache<String, Retry> retryCache = CacheUtil.newLFUCache(10000);


    public static Object invoke(RpcRequest rpcRequest, Class<?> responseType) throws RpcException {
        try {
            String serviceName = rpcRequest.getServiceName();
            Map<String, String> headers = rpcRequest.getHeader();

            List<HealthService> serviceList = serviceCache.get(serviceName, () -> {
                return ConsulUtil.use().getHealthServices(serviceName, HealthServicesRequest.newBuilder().build()).getValue();
            });

            HealthService choose = loadBalanceRule.choose(serviceList, headers.getOrDefault("client-ip", "").getBytes());
            if (choose == null) {
                throw new RpcException("can not find healthService");
            }

            String url = new StringBuilder("http://")
                    .append(choose.getService().getAddress())
                    .append(":")
                    .append(choose.getService().getPort())
                    .append("/")
                    .append(rpcRequest.getClassName())
                    .append("/")
                    .append(rpcRequest.getMethodName()).toString();
            rpcRequest.setRequestUrl(url);

            String reqStr = null;
            if (rpcRequest != null) {
                reqStr = serialize.encode(rpcRequest.getBodyObj());
            }
            rpcRequest.setBodyStr(reqStr);

            String respStr = decorateHttpTransformPost(rpcRequest);



            Object respObj = null;
            if (respStr != null) {
                respObj = serialize.decode(respStr, responseType);

            }

            return respObj;
        } catch (Exception e) {
            throw new RpcException(e.getMessage());
        }

    }

    private static String decorateHttpTransformPost(RpcRequest rpcRequest) {
        TimeInterval timeInterval = new TimeInterval();
        String url = rpcRequest.getRequestUrl();

        Retry retry = retryCache.get(url, () -> {
            return Retry.ofDefaults(url);
        });

        CircuitBreaker circuitBreaker = circuitBreakerCache.get(url, () -> {
            CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig
                    .custom()
                    .minimumNumberOfCalls(50)
                    .enableAutomaticTransitionFromOpenToHalfOpen()
                    .waitDurationInOpenState(Duration.ofSeconds(30))
                    .build();
            return CircuitBreaker.of(url, circuitBreakerConfig);
        });

        Supplier<String> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, () -> {
                    String respStr = httpTransform.post(rpcRequest);
                    long cost = timeInterval.interval();
                    log.info("RpcRequest:{},", rpcRequest);
                    return respStr;
                });

        decoratedSupplier = Retry
                .decorateSupplier(retry, decoratedSupplier);

        return decoratedSupplier.get();

    }


}
