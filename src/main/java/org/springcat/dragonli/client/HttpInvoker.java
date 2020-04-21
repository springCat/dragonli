package org.springcat.dragonli.client;

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
import org.springcat.dragonli.loadbalance.ConsistentHashRule;
import org.springcat.dragonli.loadbalance.ILoadBalanceRule;
import org.springcat.dragonli.consul.ConsulUtil;
import org.springcat.dragonli.serialize.FastJsonSerialize;
import org.springcat.dragonli.serialize.ISerialize;
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


    public static Object invoke(RpcRequest request, Class<?> responseType) throws RpcException {
        try {
            String serviceName = request.getServiceName();
            Map<String, String> headers = request.getHeader();

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
                    .append(request.getClassName())
                    .append("/")
                    .append(request.getMethodName()).toString();

            String reqStr = null;
            if (request != null) {
                reqStr = serialize.encode(request.getBody());
            }

            String respStr = decorateHttpTransformPost(url, reqStr, headers);

            Object respObj = null;
            if (respStr != null) {
                respObj = serialize.decode(respStr, responseType);

            }

            return respObj;
        } catch (Exception e) {
            throw new RpcException(e.getMessage());
        }

    }

    private static String decorateHttpTransformPost(String url, String reqStr, Map<String, String> headers) {
        TimeInterval timeInterval = new TimeInterval();

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
                    String respStr = httpTransform.post(url, reqStr, headers);
                    long cost = timeInterval.interval();
                    log.info("uid:{},url:{},cost:{},req:{},resp:{}", headers.get("client-ip"), url, cost, reqStr, respStr);
                    return respStr;
                });

        decoratedSupplier = Retry
                .decorateSupplier(retry, decoratedSupplier);

        return decoratedSupplier.get();

    }


}
