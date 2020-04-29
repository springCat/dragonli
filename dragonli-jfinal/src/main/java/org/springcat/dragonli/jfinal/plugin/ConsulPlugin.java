package org.springcat.dragonli.jfinal.plugin;

import com.jfinal.plugin.IPlugin;
import org.springcat.dragonli.core.consul.ConsulConf;
import org.springcat.dragonli.core.consul.ConsulUtil;
import org.springcat.dragonli.core.registry.AppConf;
import org.springcat.dragonli.core.registry.ConsulRegister;

public class ConsulPlugin implements IPlugin {

    private ConsulConf consulConf;
    private AppConf appConf;

    public ConsulPlugin(ConsulConf consulConf, AppConf appConf) {
        this.consulConf = consulConf;
        this.appConf = appConf;
    }

    @Override
    public boolean start() {
        try {
            ConsulUtil.init(consulConf);
            ConsulRegister.register(ConsulUtil.client(), appConf);
            return true;
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean stop() {
        try {
            ConsulRegister.unregister(ConsulUtil.client(), appConf);
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }
}
