package org.springcat.dragonli.core.rpc.ihandle;

import org.springcat.dragonli.core.rpc.exception.FusingException;
import org.springcat.dragonli.core.rpc.ihandle.impl.RegisterServerInfo;
import org.springcat.dragonli.core.rpc.RpcRequest;

import java.util.function.Supplier;

public interface IErrorHandle{
    Supplier<Object> transformErrorHandle(Supplier transformSupplier, RpcRequest rpcRequest, RegisterServerInfo registerServerInfo) throws FusingException;
}
