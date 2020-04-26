package org.springcat.dragonli.core.rpc.ihandle.impl;

import cn.hutool.core.util.HashUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springcat.dragonli.core.rpc.exception.RpcException;
import org.springcat.dragonli.core.rpc.exception.RpcExceptionCodes;
import org.springcat.dragonli.core.rpc.ihandle.ILoadBalanceRule;
import org.springcat.dragonli.core.rpc.RpcRequest;
import java.util.List;

/**
 * 默认负载均衡采用用户份id一致性hash策略
 */
public class ConsistentHashRule implements ILoadBalanceRule {

    private final static Log log = LogFactory.get();

    public final static String LOADER_BALANCE_FLAG = "client-ip";

    public RegisterServiceInfo choose(List<RegisterServiceInfo> serviceList, RpcRequest rpcRequest) throws RpcException {
        RegisterServiceInfo registerServiceInfo = null;
        try {
            String loaderBalanceFlag = rpcRequest.getRpcHeader().getOrDefault(LOADER_BALANCE_FLAG, "");
            int i = consistentHash(HashUtil.murmur32(loaderBalanceFlag.getBytes()), serviceList.size());
            registerServiceInfo = serviceList.get(i);
            return registerServiceInfo;
        }catch (Exception e){
            throw new RpcException(RpcExceptionCodes.ERR_LOAD_BALANCE.getCode());
        }finally {
            log.debug("choose RegisterServiceInfo:{}",registerServiceInfo);
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
