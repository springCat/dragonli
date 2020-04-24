package org.springcat.dragonli.core.rpc.ihandle;

import org.springcat.dragonli.core.rpc.ihandle.impl.RegisterServiceInfo;
import org.springcat.dragonli.core.rpc.RpcRequest;
import java.util.function.Supplier;

public interface IErrorHandle {

    default  <T> Supplier<T> transformErrorHandle(Supplier<T> transformSupplier, RpcRequest rpcRequest, RegisterServiceInfo registerServiceInfo){
        transformSupplier = decorateCircuitBreaker(rpcRequest,transformSupplier);
        transformSupplier = decorateRetry(rpcRequest,transformSupplier);
        return transformSupplier;
    }

    void init(String key);
    /**
     * 熔断实现类
     * @param rpcRequest
     * @param supplier
     * @param <T>
     * @return
     */
    <T> Supplier<T> decorateCircuitBreaker(RpcRequest rpcRequest,Supplier<T> supplier);

    /**
     * 失败重试实现类
     * @param rpcRequest
     * @param supplier
     * @param <T>
     * @return
     */
    <T> Supplier<T> decorateRetry(RpcRequest rpcRequest,Supplier<T> supplier);

}
