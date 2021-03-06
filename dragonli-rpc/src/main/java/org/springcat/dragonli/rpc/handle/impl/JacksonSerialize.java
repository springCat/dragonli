package org.springcat.dragonli.rpc.handle.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springcat.dragonli.rpc.RpcResponse;
import org.springcat.dragonli.rpc.handle.ISerialize;
import org.springcat.dragonli.core.exception.RpcException;
import org.springcat.dragonli.core.exception.RpcExceptionCodes;


public class JacksonSerialize implements ISerialize {

    private final static Log log = LogFactory.get();

    private ObjectMapper mapper =  new ObjectMapper();
    @Override
    public RpcResponse decode(String data, Class type) throws RpcException {
        try {
            return (RpcResponse)  mapper.readValue(data,type);
        }catch (Exception e){
            log.error("JacksonSerialize decode error data:{},type:{},error:{}" ,data,type,e.getMessage());
            throw new RpcException(RpcExceptionCodes.ERR_REQUEST_SERIALIZE.getCode());
        }

    }

    @Override
    public String encode(Object data) throws RpcException{
        try {
            return mapper.writeValueAsString(data);
        }catch (Exception e){
            log.error("JacksonSerialize encode error data:{},error:{}" ,data,e.getMessage());
            throw new RpcException(RpcExceptionCodes.ERR_RESPONSE_DESERIALIZE.getCode());
        }
    }

}
