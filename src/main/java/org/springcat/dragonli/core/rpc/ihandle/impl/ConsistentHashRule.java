package org.springcat.dragonli.core.rpc.ihandle.impl;

import cn.hutool.core.util.HashUtil;
import org.springcat.dragonli.core.rpc.ihandle.ILoadBalanceRule;
import org.springcat.dragonli.core.rpc.RpcRequest;

import java.util.List;

public class ConsistentHashRule implements ILoadBalanceRule {


    public RegisterServerInfo choose(List<RegisterServerInfo> serviceList, RpcRequest rpcRequest) {
        if(serviceList == null || serviceList.size() == 0){
            return null;
        }
        String loaderBalanceFlag = rpcRequest.getRpcHeader().getOrDefault("client-ip", "");
        int i = consistentHash(HashUtil.murmur32(loaderBalanceFlag.getBytes()), serviceList.size());
        return serviceList.get(i);
    }

    public int consistentHash(long input, int buckets) {
        input = 2862933555777941757L * input + 1;
        double nextDouble = ((double) ((int) (input >>> 33) + 1)) / (0x1.0p31);
        int candidate = 0;
        int next;
        while (true) {
            next = (int) ((candidate + 1) / nextDouble);
            if (next >= 0 && next < buckets) {
                candidate = next;
            } else {
                return candidate;
            }
        }
    }
}
