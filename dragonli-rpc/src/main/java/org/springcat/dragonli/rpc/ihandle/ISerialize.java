package org.springcat.dragonli.rpc.ihandle;

import org.springcat.dragonli.exception.RpcException;
import org.springcat.dragonli.rpc.RpcResponse;

public interface ISerialize {

     RpcResponse decode(String data, Class type) throws RpcException;

     String encode(Object object) throws RpcException;

}
