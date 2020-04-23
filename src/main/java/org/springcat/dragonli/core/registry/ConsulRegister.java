package org.springcat.dragonli.core.registry;

import cn.hutool.core.net.NetUtil;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import lombok.experimental.UtilityClass;

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


}
