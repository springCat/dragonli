package org.springcat.dragonli.jfinal.plugin;

import com.jfinal.aop.AopManager;
import com.jfinal.plugin.IPlugin;
import org.springcat.dragonli.core.rpc.RpcInfo;
import org.springcat.dragonli.core.rpc.RpcUtil;
import org.springcat.dragonli.core.validate.ValidationUtil;

import java.util.List;
import java.util.Map;


public class RpcPlugin implements IPlugin {

    private RpcInfo rpcInfo;

    public RpcPlugin(RpcInfo rpcInfo) {
        this.rpcInfo = rpcInfo;
    }

    @Override
    public boolean start() {
        ValidationUtil.init();
        List<Class<?>> services = RpcUtil.scanRpcService(rpcInfo.getScanPackages());
        Map<Class<?>, Object> classObjectMap = RpcUtil.convert2RpcServiceImpl(services);

        for (Map.Entry<Class<?>, Object> classObjectEntry : classObjectMap.entrySet()) {
            AopManager.me().addSingletonObject(classObjectEntry.getKey(), classObjectEntry.getValue());
        }
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
}
