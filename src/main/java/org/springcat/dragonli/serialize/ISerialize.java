package org.springcat.dragonli.serialize;

public interface ISerialize {
     <T> T decode(String data, Class<T> type) ;
     String encode(Object object) ;
}
