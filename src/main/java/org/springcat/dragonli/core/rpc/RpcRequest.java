package org.springcat.dragonli.core.rpc;

import lombok.Data;

import java.util.Map;

@Data
public class RpcRequest<T> {

    private String requestUrl;

    private Map<String,String> header;

    private T bodyObj;

    private String bodyStr;

    private String serviceName;

    private String className;

    private String methodName;

    private String[] label;

    public byte[] getLoadBalanceFlag(){
        return toString().getBytes();
    }
}
