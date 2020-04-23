package org.springcat.dragonli.core.rpc.ihandle.impl;

import com.jfinal.json.FastJson;
import org.springcat.dragonli.core.rpc.exception.SerializeException;
import org.springcat.dragonli.core.rpc.ihandle.ISerialize;

/**
 * 默认fastjson
 */
public class FastJsonSerialize implements ISerialize {

    @Override
    public <T> T decode(String data, Class<T> type) throws SerializeException {
        try {
            return FastJson.getJson().parse(data,type);
        }catch (Exception e){
            throw new SerializeException(e.getMessage());
        }

    }

    @Override
    public String encode(Object object) throws SerializeException{
        try {
            return FastJson.getJson().toJson(object);
        }catch (Exception e){
            throw new SerializeException(e.getMessage());
        }
    }

}
