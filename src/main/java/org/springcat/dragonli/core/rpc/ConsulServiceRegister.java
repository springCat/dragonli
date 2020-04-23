package org.springcat.dragonli.core.rpc;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.ecwid.consul.v1.health.model.HealthService;
import org.springcat.dragonli.core.consul.ConsulUtil;

import java.util.List;

public class ConsulServiceRegister implements IServiceRegister {

    private static TimedCache<String, List<HealthService>> serviceCache = CacheUtil.newTimedCache(1000);

    public List<HealthService> getServiceList(RpcRequest rpcRequest){
        List<HealthService> serviceList = ConsulUtil.getServiceList(rpcRequest.getServiceName());
        return serviceList;
    }

}
