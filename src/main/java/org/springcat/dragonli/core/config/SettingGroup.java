package org.springcat.dragonli.core.config;

import org.springcat.dragonli.core.rpc.RpcInfo;
import org.springcat.dragonli.core.registry.AppInfo;
import org.springcat.dragonli.core.ConsulInfo;

public enum SettingGroup{

    consul(ConsulInfo.class),app(AppInfo.class),rpc(RpcInfo.class);

    private Class cls;

    SettingGroup(Class cls) {
        this.cls = cls;
    }

    public Class getCls() {
        return cls;
    }

}
