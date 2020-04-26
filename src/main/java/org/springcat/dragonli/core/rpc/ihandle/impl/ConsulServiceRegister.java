package org.springcat.dragonli.core.rpc.ihandle.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.health.HealthServicesRequest;
import com.ecwid.consul.v1.health.model.HealthService;
import org.springcat.dragonli.core.consul.ConsulUtil;
import org.springcat.dragonli.core.rpc.exception.RpcException;
import org.springcat.dragonli.core.rpc.exception.RpcExceptionCodes;
import org.springcat.dragonli.core.rpc.ihandle.IServiceRegister;
import org.springcat.dragonli.core.rpc.RpcRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认为consul服务注册中心,因为consul agent是部署在本机的,暂时不加缓存,后续压测后再看
 */
public class ConsulServiceRegister implements IServiceRegister {

    public List<RegisterServiceInfo> getServiceList(RpcRequest rpcRequest) throws RpcException {
        try {
            ConsulClient client = ConsulUtil.client();
            List<RegisterServiceInfo> list = new ArrayList<>();
            List<HealthService> value = client.getHealthServices(rpcRequest.getRpcMethodInfo().getAppName(), HealthServicesRequest.newBuilder().build()).getValue();
            for (HealthService healthService : value) {
                HealthService.Service service = healthService.getService();
                RegisterServiceInfo registerServiceInfo = new RegisterServiceInfo();
                BeanUtil.copyProperties(service, registerServiceInfo);
                list.add(registerServiceInfo);
            }
            return list;
        }catch (Exception e){
            throw new RpcException(RpcExceptionCodes.ERR_SERVICE_NOT_FIND.getCode());
        }
    }
}
