package org.springcat.dragonli.util.configcenter;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.setting.Setting;
import org.springcat.dragonli.util.consul.Consul;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Description ICenterConfig
 * @Author springCat
 * @Date 2020/5/8 16:44
 */
public class ICenterConfig {

    private Consul consul;

    private String confPath;

    private AtomicReference<Setting> localConfCache;

    private int confRefreshPeriod;

    private static ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1, ThreadUtil.newNamedThreadFactory("ConfigCenterSchedule",false));

    public void loadSetting(Setting configCenterConf,String confType){
        this.confRefreshPeriod = configCenterConf.getInt(confType + "RefreshPeriod", 0);
        this.confPath = configCenterConf.getOrDefault(confType + "Path", "")+configCenterConf.getOrDefault(  "applicationName", "");

    }

    public void init(Consul consul, Setting configCenterConf, String confType) {
        loadSetting(configCenterConf,confType);
        this.consul = consul;
        this.localConfCache = new AtomicReference<Setting>();
        if(confRefreshPeriod > 0) {
            schedule.scheduleWithFixedDelay(this::refreshConfigList, 0, confRefreshPeriod, TimeUnit.SECONDS);
        }
    }

    public Setting pullConfigList() {
        return consul.getKVValues(confPath);
    }

    public Setting refreshConfigList(){
        Setting setting = pullConfigList();
        if(setting != null) {
            localConfCache.set(setting);
        }
        return setting;
    }

    public String getValue(String key,String defaultValue) {
        return localConfCache.get().get(key,defaultValue);
    }

    public String getValue(String key) {
        return localConfCache.get().get(key);
    }

    public Setting getAll() {
        return localConfCache.get();
    }

    public void setConfig(String key,String value) {
        Setting setting = localConfCache.get();
        setting.set(key,value);
        localConfCache.set(setting);
    }

    public String pullConfig(String path) {
        return consul.getKVValue(confPath + path);
    }

    public String refreshConfig(String path) {
        String value = pullConfig(path);
        if(value != null) {
            localConfCache.get().put(path, value);
        }
        return value;
    }

}
