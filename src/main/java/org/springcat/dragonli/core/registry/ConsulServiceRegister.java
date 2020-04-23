package org.springcat.dragonli.core.registry;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.bean.BeanUtil;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.health.HealthServicesRequest;
import com.ecwid.consul.v1.health.model.HealthService;
import org.springcat.dragonli.core.consul.ConsulUtil;
import org.springcat.dragonli.core.rpc.IServiceRegister;
import org.springcat.dragonli.core.rpc.RpcRequest;

import java.util.ArrayList;
import java.util.List;

public class ConsulServiceRegister implements IServiceRegister {

    private static TimedCache<String, List<HealthService>> serviceCache = CacheUtil.newTimedCache(1000);


    public List<RegisterServerInfo> getServiceList(RpcRequest rpcRequest){
        ConsulClient client = ConsulUtil.client();
        List<RegisterServerInfo> list = new ArrayList<>();
        List<HealthService> value = client.getHealthServices(rpcRequest.getServiceName(), HealthServicesRequest.newBuilder().build()).getValue();
        for (HealthService healthService : value) {
            HealthService.Service service = healthService.getService();
            RegisterServerInfo registerServerInfo = new RegisterServerInfo();
            BeanUtil.copyProperties(service,registerServerInfo);
            list.add(registerServerInfo);
        }
        return list;
    }


}
