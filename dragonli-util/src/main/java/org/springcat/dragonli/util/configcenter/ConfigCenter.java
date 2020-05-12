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

    public static final String BOOT_CONF = "bootConf";
    public static final String SYS_CONF = "sysConf";
    public static final String USER_CONF = "userConf";
    public static final String ROUTE_CONF = "routeConf";

    public static void init(Consul consulPara){
        consul = consulPara;
    }

    public static void defaultInit(){
        registerConf(BOOT_CONF);
        registerConf(SYS_CONF);
        registerConf(USER_CONF);
        registerConf(ROUTE_CONF);
    }

    @SneakyThrows
    public static void registerConf(String confType){
        ICenterConfig centerConfig = new ICenterConfig();
        centerConfig.init(consul,configCenterConf,confType);
        map.put(confType,centerConfig);
    }

    public static ICenterConfig getBootConf(){
        return getConf(BOOT_CONF);
    }

    public static ICenterConfig getSysConf(){
        return getConf(SYS_CONF);
    }

    public static ICenterConfig getUserConf(){
        return getConf(USER_CONF);
    }

    public static ICenterConfig getRouteConf(){
        return getConf(ROUTE_CONF);
    }

    @SneakyThrows
    public static ICenterConfig getConf(String confType){
        return map.get(confType);
    }
}
