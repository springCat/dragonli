package org.springcat.dragonli.util.configcenter;

import cn.hutool.setting.Setting;
import lombok.Data;
import lombok.SneakyThrows;
import org.springcat.dragonli.util.consul.Consul;

import java.util.HashMap;
import java.util.Map;

@Data
public class ConfigCenter {

    private static Map<String,ICenterConfig> map = new HashMap<>();

    private static Consul consul;

    private static Setting configCenterConf = new ConfigCenterConf().loadSetting();

    private static ConfigCenter configCenter;

    public static void init(Consul consulPara){
        consul = consulPara;
    }

    public static void defaultInit(){
        registerConf("bootConf");
        registerConf("sysConf");
        registerConf("userConf");
        registerConf("routeConf");


    }

    @SneakyThrows
    public static void registerConf(String confType){
        ICenterConfig centerConfig = new ICenterConfig();
        centerConfig.init(consul,configCenterConf,confType);
        map.put(confType,centerConfig);
    }

    @SneakyThrows
    public static ICenterConfig getConf(Class<? extends ICenterConfig> cls){
        return map.get(cls);
    }
}
