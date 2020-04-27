package org.springcat.dragonli.jfinal.plugin;

import com.jfinal.plugin.IPlugin;
import org.springcat.dragonli.core.config.ConfigUtil;

public class ConfigPlugin implements IPlugin {

    @Override
    public boolean start() {
        ConfigUtil.refreshSysConf();
        ConfigUtil.refreshUserConf();
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
}
