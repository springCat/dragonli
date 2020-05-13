package org.springcat.dragonli.rpc.ihandle.impl;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.vavr.collection.HashMap;
import io.vavr.control.Try;
import lombok.Data;
import org.springcat.dragonli.rpc.ihandle.IErrorHandle;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 用Resilience4j 来处理失败重试和熔断,待完善,后续的限流也可以用他来实现
 */
@Data
public class Resilience4jErrorHandle implements IErrorHandle {

    private CircuitBreaker circuitBreaker;

    private Retry retry;

    public void init(String key){
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig
                .custom()
                .minimumNumberOfCalls(100)
                .enableAutomaticTransitionFromOpenToHalfOpen()
                .failureRateThreshold(30.0f)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .build();

        this.circuitBreaker =  CircuitBreaker.of(key, circuitBreakerConfig);

        RetryConfig retryConfig = new RetryConfig.Builder().maxAttempts(2).build();
        this.retry = Retry.of(key,retryConfig, HashMap.empty());
    }

    @Override
    public <T> Supplier<T> decorateCircuitBreaker(Supplier<T> supplier) {
        return  CircuitBreaker.decorateSupplier(circuitBreaker, supplier);
    }


    @Override
    public <T> Supplier<T> decorateRetry(Supplier<T> supplier) {
        return Retry.decorateSupplier(retry,supplier);
    }

    @Override
    public <T> Try<T> recover(Supplier<T> supplier, Function<? super Throwable, ? extends T> errorHandler) {
        return Try.ofSupplier(supplier)
                .recover(errorHandler);
    }

}
