package org.springcat.dragonli.client;

import lombok.Data;

import java.util.Map;

@Data
public class RpcRequest<T> {

    private Map<String,String> header;

    private T body;

    private String serviceName;

    private String className;

    private String methodName;

    public byte[] getLoadBalanceFlag(){
        return toString().getBytes();
    }
}
