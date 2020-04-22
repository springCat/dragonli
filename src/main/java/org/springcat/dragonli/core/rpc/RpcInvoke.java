package org.springcat.dragonli.core.rpc;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LFUCache;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.HashUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ecwid.consul.v1.health.HealthServicesRequest;
import com.ecwid.consul.v1.health.model.HealthService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import org.springcat.dragonli.core.consul.ConsulUtil;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RpcInvoke {

    private static final Log log = LogFactory.get(RpcInvoke.class);

    private static TimedCache<String, List<HealthService>> serviceCache = CacheUtil.newTimedCache(1000);
    private static LFUCache<String, CircuitBreaker> circuitBreakerCache = CacheUtil.newLFUCache(10000);
    private static LFUCache<String, Retry> retryCache = CacheUtil.newLFUCache(10000);

    private static ILoadBalanceRule loadBalanceRule;
    private static ISerialize serialize;
    private static IHttpTransform httpTransform;
    private static RpcInvoke invoke;
    private static RpcInfo rpcInfo;

    public static void init(RpcInfo rpcInfo1,Consumer<Map<Class<?>, Object>> consumer) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        rpcInfo = rpcInfo1;

        //初始化负载均衡
        loadBalanceRule = (ILoadBalanceRule) Class.forName(rpcInfo.getLoadBalanceRuleImplClass()).newInstance();
        //初始化序列化
        serialize = (ISerialize) Class.forName(rpcInfo.getSerializeImplClass()).newInstance();
        //初始化http请求客户端
        httpTransform = (IHttpTransform) Class.forName(rpcInfo.getHttpTransformImplClass()).newInstance();

        //初始化接口代理类
        List<Class<?>> services = RpcUtil.scanRpcService(rpcInfo.getScanPackages());
        Map<Class<?>, Object> implMap = RpcUtil.convert2RpcServiceImpl(services);
        consumer.accept(implMap);
    }

    private static Supplier<Object> decorateHttpTransformPost (RpcRequest rpcRequest,HealthService healthService){
        TimeInterval timeInterval = new TimeInterval();

        String key = StrUtil.join("|",
                healthService.getService().getAddress(),
                healthService.getService().getPort(),
                rpcRequest.getClassName(),
                rpcRequest.getMethodName());

        Retry retry = retryCache.get(key, () -> {
            return Retry.ofDefaults(key);
        });

        CircuitBreaker circuitBreaker = circuitBreakerCache.get(key, () -> {
            CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig
                    .custom()
                    .minimumNumberOfCalls(50)
                    .enableAutomaticTransitionFromOpenToHalfOpen()
                    .waitDurationInOpenState(Duration.ofSeconds(30))
                    .build();
            return CircuitBreaker.of(key, circuitBreakerConfig);
        });

        Supplier<Object> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, () -> {
                    Object respStr = httpTransform.post(rpcRequest,healthService);
                    long cost = timeInterval.interval();
                    log.info("RpcRequest:{},", rpcRequest);
                    return respStr;
                });

        decoratedSupplier = Retry
                .decorateSupplier(retry, decoratedSupplier);

        return decoratedSupplier;

    };


    /**
     *
     *
     *  method -> buildRpcRequest -> serialize  -> loaderBalance  -> transform  -> deserialize -> return
     *                                              |                   |                           |
     *                                              ------------->   errorHandle   ---------------->
     *
     * @param rpcRequest
     * @return
     * @throws RpcException
     */
    public static Object invoke(RpcRequest rpcRequest) throws RpcException {
        //loaderBalance
        List<HealthService> serviceList = ConsulUtil.getServiceList(rpcRequest.getServiceName());
        HealthService choose = loadBalanceRule.choose(serviceList,rpcRequest);
        if (choose == null) {
            log.error("can not find healthService");
            return null;
        }

        rpcRequest.setSerialize(serialize);

        //transform
        try {
            Supplier<Object> supplier = decorateHttpTransformPost(rpcRequest, choose);
            return supplier.get();
        } catch (Exception e) {
            throw new RpcException(e.getMessage());
        }
    }


}
