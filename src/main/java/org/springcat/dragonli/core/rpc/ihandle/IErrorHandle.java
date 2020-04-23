package org.springcat.dragonli.core.rpc.ihandle;

import cn.hutool.core.util.StrUtil;
import org.springcat.dragonli.core.rpc.ihandle.impl.RegisterServerInfo;
import org.springcat.dragonli.core.rpc.RpcRequest;
import java.util.function.Supplier;

public interface IErrorHandle {

    default  <T> Supplier<T> transformErrorHandle(Supplier<T> transformSupplier, RpcRequest rpcRequest, RegisterServerInfo registerServerInfo){
        String key = genKey(rpcRequest,registerServerInfo);
        transformSupplier = decorateCircuitBreaker(key,transformSupplier);
        transformSupplier = decorateRetry(key,transformSupplier);
        return transformSupplier;
    }

    <T> Supplier<T> decorateCircuitBreaker(String key,Supplier<T> supplier);

    <T> Supplier<T> decorateRetry(String key,Supplier<T> supplier);

    default String genKey(RpcRequest rpcRequest, RegisterServerInfo registerServerInfo){
        String key = StrUtil.join("|",
                registerServerInfo.getAddress(),
                registerServerInfo.getPort(),
                rpcRequest.getClassName(),
                rpcRequest.getMethodName());
        return key;
    }
}
