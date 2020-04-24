package org.springcat.dragonli.jfinal.plugin;

import com.jfinal.aop.AopManager;
import com.jfinal.plugin.IPlugin;
import org.springcat.dragonli.core.DragonLiStarter;
import org.springcat.dragonli.core.rpc.RpcConf;
import java.util.Map;

public class RpcPlugin implements IPlugin {

    private RpcConf rpcConf;

    public RpcPlugin(RpcConf rpcConf) {
        this.rpcConf = rpcConf;
    }

    @Override
    public boolean start() {
        try {
            Map<Class<?>, Object> map = DragonLiStarter.start(rpcConf);
            //把生成接口实现类,注入jfinal的ioc
            for (Map.Entry<Class<?>, Object> classObjectEntry : map.entrySet()) {
                AopManager.me().addSingletonObject(classObjectEntry.getKey(), classObjectEntry.getValue());
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
}
