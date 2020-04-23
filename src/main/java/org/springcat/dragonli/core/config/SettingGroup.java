package org.springcat.dragonli.core.config;

import org.springcat.dragonli.core.rpc.RpcConfInfo;
import org.springcat.dragonli.core.registry.AppInfo;
import org.springcat.dragonli.core.consul.ConsulInfo;

public enum SettingGroup{

    consul(ConsulInfo.class),app(AppInfo.class),rpc(RpcConfInfo.class);

    private Class cls;

    SettingGroup(Class cls) {
        this.cls = cls;
    }

    public Class getCls() {
        return cls;
    }

}
