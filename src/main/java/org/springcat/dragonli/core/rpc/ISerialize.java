package org.springcat.dragonli.core.rpc;

public interface ISerialize {
     <T> T decode(String data, Class<T> type) ;
     String encode(Object object) ;
}
