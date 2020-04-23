package org.springcat.dragonli.jfinal.plugin;

import com.jfinal.plugin.IPlugin;
import org.springcat.dragonli.core.config.ConfigUtil;
import org.springcat.dragonli.core.ConsulUtil;
import org.springcat.dragonli.core.registry.AppInfo;
import org.springcat.dragonli.core.ConsulInfo;
import org.springcat.dragonli.core.registry.ConsulRegister;

public class ConsulPlugin implements IPlugin {

    private ConsulInfo consulInfo;
    private AppInfo appInfo;

    public ConsulPlugin(ConsulInfo consulInfo, AppInfo appInfo) {
        this.consulInfo = consulInfo;
        this.appInfo = appInfo;
    }

    @Override
    public boolean start() {
        try {
            ConsulUtil.init(consulInfo);
            ConfigUtil.fetchSysConf(appInfo);
            ConsulRegister.register(ConsulUtil.client(), appInfo);
            return true;
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean stop() {
        try {
            ConsulRegister.unregister(ConsulUtil.client(), appInfo);
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }
}
