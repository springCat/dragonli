package org.springcat.dragonli.core.rpc.ihandle;

import org.springcat.dragonli.core.rpc.RpcResponse;
import org.springcat.dragonli.core.rpc.exception.SerializeException;

public interface ISerialize {

     RpcResponse decode(String data, Class type) throws SerializeException;

     String encode(Object object) throws SerializeException;

     default void errorHandler(SerializeException exception){}
}
