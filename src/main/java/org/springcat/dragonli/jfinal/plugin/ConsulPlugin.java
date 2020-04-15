package org.springcat.dragonli.jfinal.plugin;

import com.jfinal.plugin.IPlugin;
import org.springcat.dragonli.config.ConfigUtil;
import org.springcat.dragonli.consul.ConsulUtil;
import org.springcat.dragonli.registry.AppInfo;
import org.springcat.dragonli.registry.ConsulRegister;

public class ConsulPlugin implements IPlugin {

    private String ip;
    private int port;
    private AppInfo appInfo;

    public ConsulPlugin(String ip, int port, AppInfo appInfo) {
        this.ip = ip;
        this.port = port;
        this.appInfo = appInfo;
    }

    @Override
    public boolean start() {
        try {
            ConsulUtil.init(ip,port);
            ConfigUtil.fetchSysConf(appInfo);
            ConsulRegister.register(ConsulUtil.use(), appInfo);
            return true;
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean stop() {
        try {
            ConsulRegister.unregister(ConsulUtil.use(), appInfo);
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }
}
