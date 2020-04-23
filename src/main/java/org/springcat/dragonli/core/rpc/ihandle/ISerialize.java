package org.springcat.dragonli.core.rpc.ihandle;

import org.springcat.dragonli.core.rpc.RpcResponse;
import org.springcat.dragonli.core.rpc.exception.RpcException;

public interface ISerialize {

     RpcResponse decode(String data, Class type) throws RpcException;

     String encode(Object object) throws RpcException;

}
