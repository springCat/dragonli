package org.springcat.dragonli.jfinal.plugin;

import com.jfinal.plugin.IPlugin;
import org.springcat.dragonli.core.RpcStarter;
import org.springcat.dragonli.core.rpc.ihandle.impl.ConsulServiceProvider;
import org.springcat.dragonli.util.configcenter.ConfigCenter;
import org.springcat.dragonli.util.consul.Consul;
import org.springcat.dragonli.util.registercenter.register.ServiceRegister;

/**
 * @Description DragonLiPlugin
 * @Author springCat
 * @Date 2020/5/6 19:39
 */
public class DragonLiPlugin implements IPlugin {
    @Override
    public boolean start() {
        //init consul
        Consul consul = new Consul();

        //init service register
        ServiceRegister serviceRegister = new ServiceRegister(consul);

        //init consulServiceProvider for connect ServiceProvider and Rpc
        ConsulServiceProvider consulServiceProvider = new ConsulServiceProvider(consul);

        //init rpc
        RpcStarter.init(consulServiceProvider);

        //init config
        ConfigCenter.init(consul);


        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
}
