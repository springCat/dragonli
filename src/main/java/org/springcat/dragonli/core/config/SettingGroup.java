package org.springcat.dragonli.core.config;

import org.springcat.dragonli.core.rpc.RpcConf;
import org.springcat.dragonli.core.registry.AppConf;
import org.springcat.dragonli.core.consul.ConsulConf;

/**
 * dragonli配置分类枚举
 */
public enum SettingGroup{

    consul(ConsulConf.class), application(AppConf.class), rpc(RpcConf.class),config(ConfigConf.class);

    private Class cls;

    SettingGroup(Class cls) {
        this.cls = cls;
    }

    public Class getCls() {
        return cls;
    }

}
