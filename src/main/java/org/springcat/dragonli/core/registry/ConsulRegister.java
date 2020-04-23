package org.springcat.dragonli.core.registry;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.net.NetUtil;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.health.HealthServicesRequest;
import com.ecwid.consul.v1.health.model.HealthService;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ConsulRegister {

    public void register(ConsulClient client,AppInfo appInfo) throws Exception{
        NewService application = new NewService();
        application.setName(appInfo.getName());
        application.setAddress(appInfo.getIp());
        application.setPort(appInfo.getPort());
        if(appInfo.getAppTags() != null && appInfo.getAppTags().size() > 0) {
            application.setTags(appInfo.getAppTags());
        }
        application.setId(genServiceId(appInfo));
        //check
        NewService.Check serviceCheck = new NewService.Check();
        serviceCheck.setInterval(appInfo.getCheckInterval());
        serviceCheck.setTimeout(appInfo.getCheckTimout());
        serviceCheck.setHttp(appInfo.getCheckUrl());
        application.setCheck(serviceCheck);
        client.agentServiceRegister(application);
    }

    public void unregister(ConsulClient client,AppInfo appInfo) throws Exception{
        String appId = genServiceId(appInfo);
        client.agentCheckDeregister("service:"+appId);
        client.agentServiceDeregister(appId);
    }

    public String genServiceId(AppInfo appInfo){
        return appInfo.getName() + NetUtil.ipv4ToLong(appInfo.getIp());
    }


    public  List<RegisterServerInfo> getServiceList(ConsulClient client,String serviceName){
        List<RegisterServerInfo> list = new ArrayList<>();
        List<HealthService> value = client.getHealthServices(serviceName, HealthServicesRequest.newBuilder().build()).getValue();
        for (HealthService healthService : value) {
            HealthService.Service service = healthService.getService();
            RegisterServerInfo registerServerInfo = new RegisterServerInfo();
            BeanUtil.copyProperties(service,registerServerInfo);
            list.add(registerServerInfo);
        }
        return list;
    }

}
