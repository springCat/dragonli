package org.springcat.dragonli.jfinal.plugin;

import com.jfinal.plugin.IPlugin;
import org.springcat.dragonli.core.config.ConfigConf;
import org.springcat.dragonli.core.config.ConfigUtil;

public class ConfigPlugin implements IPlugin {

    private ConfigConf configConf;

    public ConfigPlugin(ConfigConf configConf) {
        this.configConf = configConf;
    }

    @Override
    public boolean start() {
        ConfigUtil.initSysConf(configConf);
        ConfigUtil.initUserConf(configConf);
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
}
