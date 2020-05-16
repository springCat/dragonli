package org.springcat.dragonli.core.registercenter.register;

import cn.hutool.core.map.MapUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ecwid.consul.v1.agent.model.NewService;
import org.springcat.dragonli.core.consul.Consul;

import java.util.HashMap;

public class ServiceRegister {

    private final static Log log = LogFactory.get();

    private  ApplicationConf appConf = new ApplicationConf().load();

    private Consul consul;

    public ServiceRegister(Consul consul) {
        this.consul = consul;
        register();
    }

    /**
     * 服务注册
     * @return
     */
    public boolean register(){
        NewService application = new NewService();
        application.setName(appConf.getName());
        application.setAddress(appConf.getIp());
        application.setPort(appConf.getPort());
        if(appConf.getAppTags() != null && appConf.getAppTags().size() > 0) {
            application.setTags(appConf.getAppTags());
        }
        application.setId(appConf.getServiceId());
        //check
        NewService.Check serviceCheck = new NewService.Check();
        serviceCheck.setInterval(appConf.getCheckInterval());
        serviceCheck.setTimeout(appConf.getCheckTimout());
        serviceCheck.setHttp(appConf.getCheckUrl());
        application.setCheck(serviceCheck);
        //meta
        HashMap<String, String> map = MapUtil.newHashMap();
        application.setMeta(map);

        return consul.register(application);
    }

    /**
     * 服务注销
     * @throws Exception
     */
    public boolean unRegister(){
        return consul.unRegister(appConf.getServiceId());
    }


}
