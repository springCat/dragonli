package org.springcat.dragonli.core.rpc.ihandle;

import org.springcat.dragonli.core.rpc.exception.SerializeException;

public interface ISerialize {
     <T> T decode(String data, Class<T> type) throws SerializeException;
     String encode(Object object) throws SerializeException;

     default void errorHandler(SerializeException exception){

     }
}
