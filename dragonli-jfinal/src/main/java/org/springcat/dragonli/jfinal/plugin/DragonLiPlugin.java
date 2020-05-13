package org.springcat.dragonli.jfinal.plugin;

import com.jfinal.aop.AopManager;
import com.jfinal.plugin.IPlugin;
import org.springcat.dragonli.core.RpcStarter;
import org.springcat.dragonli.core.rpc.RpcInvoke;
import org.springcat.dragonli.core.rpc.ihandle.impl.ConsulServiceProvider;
import org.springcat.dragonli.util.configcenter.ConfigCenter;
import org.springcat.dragonli.util.consul.Consul;
import org.springcat.dragonli.util.registercenter.register.ServiceRegister;

import java.util.Map;

/**
 * @Description DragonLiPlugin
 * @Author springCat
 * @Date 2020/5/6 19:39
 */
public class DragonLiPlugin implements IPlugin {

    private ServiceRegister serviceRegister;
    @Override
    public boolean start() {
        //init consul
        Consul consul = new Consul();

        //init service register
        serviceRegister = new ServiceRegister(consul);

        //init consulServiceProvider for connect ServiceProvider and Rpc
        ConsulServiceProvider consulServiceProvider = new ConsulServiceProvider();
        consulServiceProvider.init(consul);

        //init rpc
        RpcInvoke rpcInvoke = RpcStarter.init(consulServiceProvider);

        //init config
        ConfigCenter.init(consul);

        Map<Class<?>, Object> serviceImplMap = rpcInvoke.getServiceImplMap();
        for (Map.Entry<Class<?>, Object> classObjectEntry : serviceImplMap.entrySet()) {
            AopManager.me().addSingletonObject(classObjectEntry.getKey(), classObjectEntry.getValue());
        }


        return true;
    }

    @Override
    public boolean stop() {
        return serviceRegister.unRegister();
    }
}
