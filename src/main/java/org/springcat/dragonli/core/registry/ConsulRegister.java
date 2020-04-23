package org.springcat.dragonli.core.registry;

import cn.hutool.core.net.NetUtil;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ConsulRegister {

    public void register(ConsulClient client, AppConf appConf) throws Exception{
        NewService application = new NewService();
        application.setName(appConf.getName());
        application.setAddress(appConf.getIp());
        application.setPort(appConf.getPort());
        if(appConf.getAppTags() != null && appConf.getAppTags().size() > 0) {
            application.setTags(appConf.getAppTags());
        }
        application.setId(genServiceId(appConf));
        //check
        NewService.Check serviceCheck = new NewService.Check();
        serviceCheck.setInterval(appConf.getCheckInterval());
        serviceCheck.setTimeout(appConf.getCheckTimout());
        serviceCheck.setHttp(appConf.getCheckUrl());
        application.setCheck(serviceCheck);
        client.agentServiceRegister(application);
    }

    public void unregister(ConsulClient client, AppConf appConf) throws Exception{
        String appId = genServiceId(appConf);
        client.agentCheckDeregister("service:"+appId);
        client.agentServiceDeregister(appId);
    }

    public String genServiceId(AppConf appConf){
        return appConf.getName() + NetUtil.ipv4ToLong(appConf.getIp());
    }


}
