package org.springcat.dragonli.core.rpc.ihandle.impl;

import cn.hutool.core.util.HashUtil;
import org.springcat.dragonli.core.rpc.exception.RpcException;
import org.springcat.dragonli.core.rpc.ihandle.ILoadBalanceRule;
import org.springcat.dragonli.core.rpc.RpcRequest;
import java.util.List;

/**
 * 默认负载均衡采用用户份id一致性hash策略
 */
public class ConsistentHashRule implements ILoadBalanceRule {

    public final static String LOADER_BALANCE_FLAG = "client-ip";

    public RegisterServiceInfo choose(List<RegisterServiceInfo> serviceList, RpcRequest rpcRequest) throws RpcException {
        try {
            String loaderBalanceFlag = rpcRequest.getRpcHeader().getOrDefault(LOADER_BALANCE_FLAG, "");
            int i = consistentHash(HashUtil.murmur32(loaderBalanceFlag.getBytes()), serviceList.size());
            return serviceList.get(i);
        }catch (Exception e){
            throw new RpcException(e.getMessage());
        }
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
