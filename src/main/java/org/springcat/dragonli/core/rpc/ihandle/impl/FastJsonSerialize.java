package org.springcat.dragonli.core.rpc.ihandle.impl;

import com.jfinal.json.FastJson;
import org.springcat.dragonli.core.rpc.RpcResponse;
import org.springcat.dragonli.core.rpc.exception.RpcException;
import org.springcat.dragonli.core.rpc.ihandle.ISerialize;

/**
 * 默认fastjson
 */
public class FastJsonSerialize implements ISerialize {

    @Override
    public RpcResponse decode(String data, Class type) throws RpcException {
        try {
            return (RpcResponse) FastJson.getJson().parse(data,type);
        }catch (Exception e){
            throw new RpcException(e.getMessage());
        }

    }

    @Override
    public String encode(Object object) throws RpcException{
        try {
            return FastJson.getJson().toJson(object);
        }catch (Exception e){
            throw new RpcException(e.getMessage());
        }
    }

}
