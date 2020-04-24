package org.springcat.dragonli.core.rpc.ihandle.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LFUCache;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import org.springcat.dragonli.core.rpc.RpcRequest;
import org.springcat.dragonli.core.rpc.ihandle.IErrorHandle;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.function.Supplier;

/**
 * 用Resilience4j 来处理失败重试和熔断,待完善,后续的限流也可以用他来实现
 */
public class Resilience4jErrorHandle implements IErrorHandle {

    private LFUCache<Method, CircuitBreaker> circuitBreakerCache = CacheUtil.newLFUCache(10000);

    private LFUCache<Method, Retry> retryCache = CacheUtil.newLFUCache(10000);

    @Override
    public <T> Supplier<T> decorateCircuitBreaker(RpcRequest rpcRequest,Supplier<T> supplier) {
        CircuitBreaker circuitBreaker = circuitBreakerCache.get(rpcRequest.getMethod(), () -> {
            CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig
                    .custom()
                    .minimumNumberOfCalls(50)
                    .enableAutomaticTransitionFromOpenToHalfOpen()
                    .waitDurationInOpenState(Duration.ofSeconds(30))
                    .build();
            return CircuitBreaker.of(rpcRequest.getMethod().toString(), circuitBreakerConfig);
        });
        return  CircuitBreaker.decorateSupplier(circuitBreaker, supplier);
    }

    @Override
    public <T> Supplier<T> decorateRetry(RpcRequest rpcRequest, Supplier<T> supplier) {
        Retry retry = retryCache.get(rpcRequest.getMethod(), () -> {
            return Retry.ofDefaults(rpcRequest.getMethod().toString());
        });
        return Retry.decorateSupplier(retry, supplier);
    }

}
