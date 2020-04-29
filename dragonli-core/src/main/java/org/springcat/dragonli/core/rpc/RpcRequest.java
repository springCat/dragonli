package org.springcat.dragonli.core.rpc;

import cn.hutool.core.util.ArrayUtil;
import lombok.Data;
import org.springcat.dragonli.core.Context;
import org.springcat.dragonli.core.rpc.exception.RpcExceptionCodes;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 整个rpc请求的参数封装类
 */
@Data
public class RpcRequest{

    public RpcRequest(){

    }

    public RpcRequest(Method method, Object[] args){

        requestObj = ArrayUtil.get(args, 0);
        Map<String,String> reqHeader = ArrayUtil.get(args, 1);
        recover = ArrayUtil.get(args, 2);
        rpcHeader = Context.getAllRpcParam();
        if(reqHeader != null){
            rpcHeader.putAll(reqHeader);
        }
        this.method = method;
    }

    private Method method;

    private Map<String,String> rpcHeader;

    private Object requestObj;

    private Supplier recover;

    private RpcMethodInfo rpcMethodInfo;

    public RpcResponse recoverResult(){
        if(getRecover() != null){
            RpcResponse rpcResponse = (RpcResponse)getRecover().get();
            if(rpcResponse != null) {
                rpcResponse.setCode(RpcExceptionCodes.ERR_RECOVER.getCode());
            }
            return rpcResponse;
        }
        return null;
    }

}
