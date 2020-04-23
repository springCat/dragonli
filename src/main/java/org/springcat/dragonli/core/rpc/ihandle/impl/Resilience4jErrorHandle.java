package org.springcat.dragonli.core.rpc.ihandle.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LFUCache;
import cn.hutool.core.util.StrUtil;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import org.springcat.dragonli.core.rpc.exception.FusingException;
import org.springcat.dragonli.core.rpc.ihandle.IErrorHandle;
import org.springcat.dragonli.core.rpc.RpcRequest;

import java.time.Duration;
import java.util.function.Supplier;

public class Resilience4jErrorHandle implements IErrorHandle {

    private LFUCache<String, CircuitBreaker> circuitBreakerCache = CacheUtil.newLFUCache(10000);

    private LFUCache<String, Retry> retryCache = CacheUtil.newLFUCache(10000);

    @Override
    public Supplier<Object> transformErrorHandle(Supplier transformSupplier, RpcRequest rpcRequest, RegisterServerInfo registerServerInfo) throws FusingException {
        try {
            String key = StrUtil.join("|",
                    registerServerInfo.getAddress(),
                    registerServerInfo.getPort(),
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
                    .decorateSupplier(circuitBreaker,
                            transformSupplier);

            decoratedSupplier = Retry
                    .decorateSupplier(retry, decoratedSupplier);

            return decoratedSupplier;

        } catch (Exception e) {
            throw new FusingException(e.getMessage());
        }
    }
}
