package org.springcat.dragonli.core.rpc;

import org.springcat.dragonli.core.registry.RegisterServerInfo;
import java.util.function.Supplier;

public interface IErrorHandle {
    Supplier<Object> transformErrorHandle(IHttpTransform httpTransform, RpcRequest rpcRequest, RegisterServerInfo registerServerInfo);
}
