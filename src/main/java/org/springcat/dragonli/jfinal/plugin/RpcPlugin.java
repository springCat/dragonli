package org.springcat.dragonli.jfinal.plugin;

import com.jfinal.aop.AopManager;
import com.jfinal.plugin.IPlugin;
import org.springcat.dragonli.client.RpcUtil;
import org.springcat.dragonli.validate.ValidationUtil;

import java.util.List;
import java.util.Map;


public class RpcPlugin implements IPlugin {

    private String scanPackages = "";

    public RpcPlugin(String scanPackages) {
        this.scanPackages = scanPackages;
    }

    @Override
    public boolean start() {
        ValidationUtil.init();
        List<Class<?>> services = RpcUtil.scanRpcService(scanPackages);
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
