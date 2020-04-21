package org.springcat.dragonli.setting;

import org.springcat.dragonli.client.RpcInfo;
import org.springcat.dragonli.registry.AppInfo;
import org.springcat.dragonli.registry.ConsulInfo;

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
