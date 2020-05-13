package org.springcat.dragonli.rpc.ihandle;

import io.vavr.control.Try;

import java.util.function.Function;
import java.util.function.Supplier;

public interface IErrorHandle {

    default <T> T execute(Supplier<T> rpcSupplier, Function<? super Throwable, ? extends T> errorHandler){
        rpcSupplier= decorateCircuitBreaker(rpcSupplier);
        rpcSupplier = decorateRetry(rpcSupplier);
        Try<T> retry = recover(rpcSupplier, errorHandler);
        return retry.get();
    }

    void init(String key);
    /**
     * 熔断实现类
     * @param supplier
     * @param <T>
     * @return
     */
    <T> Supplier<T> decorateCircuitBreaker(Supplier<T> supplier);


    /**
     * 失败重试实现类
     * @param supplier
     * @param <T>
     * @return
     */
    <T> Supplier<T> decorateRetry(Supplier<T> supplier);

    /**
     * 从异常中恢复
     * @param supplier
     * @param errorHandler
     * @param <T>
     * @return
     */
    <T> Try<T> recover(Supplier<T> supplier, Function<? super Throwable, ? extends T> errorHandler);



}
