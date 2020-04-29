package org.springcat.dragonli.core.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import cn.hutool.system.SystemUtil;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import org.springcat.dragonli.core.consul.ConsulUtil;

import java.util.List;
import java.util.Optional;

public class ConfigUtil {

    //prjConf    ->  dragonli.setting,打包后不再变化
    private final static Setting prjConf;
    //sysConf    ->  启动前从consul加载,启动后不再变化,规划路径为 /config/sysConf/${appName}/ ,直接存于内存中
    private static Setting sysConf = new Setting();
    //userConf   ->   启动后从consul加载,随时变化,规划路径为 /config/userConf/${appName}/ ,支持单个刷新和直接设置
    private static Setting userConf = new Setting();

    private final static ConfigConf configConf;

    static {
        prjConf = new Setting("dragonli.setting",true);
        configConf = getPrjConf(SettingGroup.config);
    }

    //-------------------------------------EnvConf----------------------------------
    public static String getEnvConf(String name,String defaultValue){
        return SystemUtil.get(name,defaultValue);
    }

    //-------------------------------------PrjConf----------------------------------
    public static <T> T getPrjConf(String settingGroupName,Class cls){
        try {
            T bean = (T) prjConf.getSetting(settingGroupName).toBean(cls.newInstance());
            return bean;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T getPrjConf(SettingGroup settingGroup){
        return getPrjConf(settingGroup.name(), settingGroup.getCls());
    }

    //-------------------------------------SysConf----------------------------------
    public static Setting getSysConf(){
        return sysConf;
    }

    public static void refreshSysConf(){
        Response<List<GetValue>> resp = ConsulUtil.client().getKVValues(configConf.getSysConfPath());
        List<GetValue> values = resp.getValue();
        Optional.ofNullable(values).ifPresent(list -> {
                    for (GetValue value : list) {
                        put(sysConf,value,configConf.getSysConfPath());
                    }
                }
        );
    }
    //---------------------------------------UserConf--------------------------------

    public static Setting getUserConf(){
        return userConf;
    }

    public static void refreshUserConf(){
        Response<List<GetValue>> resp = ConsulUtil.client().getKVValues(configConf.getUserConfPath());
        List<GetValue> values = resp.getValue();
        Optional.ofNullable(values).ifPresent(list -> {
                    for (GetValue value : list) {
                        put(userConf,value,configConf.getUserConfPath());
                    }
                }
        );
    }

    public static void pullUserConf(String name){
        Response<GetValue> resp = ConsulUtil.client().getKVValue(configConf.getUserConfPath()+"/"+name);
        Optional.ofNullable(resp.getValue()).ifPresent( value -> put(userConf,value,configConf.getUserConfPath()));
    }

    public static void setUserConf(String name,String value){
        userConf.put(name,value);
    }
    //-----------------------------------------------------------------------

    private static void put(Setting setting, GetValue value, String path){
        int len = path.length();
        //去掉前缀
        String key = value.getKey().substring(len-1);
        if(StrUtil.isNotBlank(key)) {
            setting.put(key,value.getDecodedValue());
        }
    }
}