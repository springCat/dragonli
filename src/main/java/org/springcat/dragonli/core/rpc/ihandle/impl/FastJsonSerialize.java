package org.springcat.dragonli.core.rpc.ihandle.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.jfinal.json.FastJson;
import org.springcat.dragonli.core.rpc.RpcResponse;
import org.springcat.dragonli.core.rpc.exception.RpcException;
import org.springcat.dragonli.core.rpc.exception.RpcExceptionCodes;
import org.springcat.dragonli.core.rpc.ihandle.ISerialize;

/**
 * 默认fastjson
 */
public class FastJsonSerialize implements ISerialize {

    private final static Log log = LogFactory.get();

    @Override
    public RpcResponse decode(String data, Class type) throws RpcException {
        try {
            return (RpcResponse) FastJson.getJson().parse(data,type);
        }catch (Exception e){
            log.error("FastJsonSerialize decode error data:{},type:{},error:{}" ,data,type,e.getMessage());
            throw new RpcException(RpcExceptionCodes.ERR_REQUEST_SERIALIZE.getCode());
        }

    }

    @Override
    public String encode(Object data) throws RpcException{
        try {
            return FastJson.getJson().toJson(data);
        }catch (Exception e){
            log.error("FastJsonSerialize encode error data:{},error:{}" ,data,e.getMessage());
            throw new RpcException(RpcExceptionCodes.ERR_RESPONSE_DESERIALIZE.getCode());
        }
    }

}
