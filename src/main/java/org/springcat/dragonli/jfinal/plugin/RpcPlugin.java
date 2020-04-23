package org.springcat.dragonli.jfinal.plugin;

import com.jfinal.aop.AopManager;
import com.jfinal.plugin.IPlugin;
import org.springcat.dragonli.core.rpc.RpcInvoke;
import org.springcat.dragonli.core.rpc.RpcInfo;
import java.util.Map;


public class RpcPlugin implements IPlugin {

    private RpcInfo rpcInfo;

    public RpcPlugin(RpcInfo rpcInfo) {
        this.rpcInfo = rpcInfo;
    }

    @Override
    public boolean start() {
        try {
            RpcInvoke.init(rpcInfo,(Map<Class<?>, Object> map) ->{
                for (Map.Entry<Class<?>, Object> classObjectEntry : map.entrySet()) {
                    AopManager.me().addSingletonObject(classObjectEntry.getKey(), classObjectEntry.getValue());
                }
            });
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
