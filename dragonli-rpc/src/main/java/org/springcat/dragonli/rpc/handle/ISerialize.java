package org.springcat.dragonli.rpc.handle;

import org.springcat.dragonli.rpc.RpcResponse;
import org.springcat.dragonli.core.exception.RpcException;

public interface ISerialize {

     RpcResponse decode(String data, Class type) throws RpcException;

     String encode(Object object) throws RpcException;

}
