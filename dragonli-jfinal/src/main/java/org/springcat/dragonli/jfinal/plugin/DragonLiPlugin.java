package org.springcat.dragonli.jfinal.plugin;

import com.jfinal.plugin.IPlugin;
import org.springcat.dragonli.core.RpcStarter;
import org.springcat.dragonli.core.rpc.RpcConf;
import org.springcat.dragonli.core.rpc.ihandle.impl.ConsulServiceProvider;
import org.springcat.dragonli.util.configcenter.ConfigCenter;
import org.springcat.dragonli.util.configcenter.ConfigCenterConf;
import org.springcat.dragonli.util.consul.Consul;
import org.springcat.dragonli.util.consul.ConsulConf;
import org.springcat.dragonli.util.registercenter.register.ApplicationConf;
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
        ConsulConf consulConf = new ConsulConf().load();
        Consul consul = new Consul().connect(consulConf);

        //init service register
        ApplicationConf applicationConf = new ApplicationConf().load();
        ServiceRegister serviceRegister = new ServiceRegister(consul);
        serviceRegister.register(applicationConf);

        //init consulServiceProvider for connect ServiceProvider and Rpc
        ConsulServiceProvider consulServiceProvider = new ConsulServiceProvider(consul);

        //init rpc
        RpcConf rpcConf = new RpcConf().load();
        RpcStarter.init(rpcConf,consulServiceProvider);

        //init config
        ConfigCenterConf configCenterConf = new ConfigCenterConf().load();
        ConfigCenter.init(configCenterConf, consul);


        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
}
