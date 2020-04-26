package org.springcat.dragonli.core.registry;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.NetUtil;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import lombok.experimental.UtilityClass;
import java.util.HashMap;

@UtilityClass
public class ConsulRegister {

    /**
     * 服务注册
     * @param client
     * @param appConf
     * @throws Exception
     */
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
        //meta
        HashMap<String, String> map = MapUtil.newHashMap();
        application.setMeta(map);
        client.agentServiceRegister(application);
    }

    /**
     * 服务注销
     * @param client
     * @param appConf
     * @throws Exception
     */
    public void unregister(ConsulClient client, AppConf appConf) throws Exception{
        String appId = genServiceId(appConf);
        client.agentCheckDeregister("service:"+appId);
        client.agentServiceDeregister(appId);
    }

    /**
     * 生成服务id,每个服务实例唯一
     * @param appConf
     * @return
     */
    public String genServiceId(AppConf appConf){
        return appConf.getName() + NetUtil.ipv4ToLong(appConf.getIp());
    }


}
