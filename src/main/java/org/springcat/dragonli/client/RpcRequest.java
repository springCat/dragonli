package org.springcat.dragonli.client;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RpcRequest<T> {

    private Map<String,String> header;

    private T body;

    private String serviceName;

    private String className;

    private String methodName;

    private String[] label;

    public byte[] getLoadBalanceFlag(){
        return toString().getBytes();
    }
}
