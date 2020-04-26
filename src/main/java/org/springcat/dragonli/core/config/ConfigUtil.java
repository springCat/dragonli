package org.springcat.dragonli.core.config;

import cn.hutool.setting.Setting;
import cn.hutool.setting.SettingUtil;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import lombok.SneakyThrows;
import org.springcat.dragonli.core.consul.ConsulUtil;

import java.util.List;

/**
 * prjConf           ->  dragonli.setting,打包后不再变化
 * sysConf           ->  启动前从consul加载,启动后不再变化,规划路径为 /config/sysConf/${appName}/ ,直接存于内存中
 * userConf          ->  启动后从consul加载,随时变化,规划路径为  /config/userConf/${appName}/ , 需要备份到硬盘,防止配置中心挂了
 */
public class ConfigUtil {

    private final static Setting prjConf;

    private static Setting sysConf;

    private static Setting userConf;

    static {
        prjConf = new Setting("dragonli.setting",true);
    }

    public static <T> T getPrjConf(SettingGroup settingGroup){
        try {
            T bean = (T) prjConf.getSetting(settingGroup.name()).toBean(settingGroup.getCls().newInstance());
            return bean;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    @SneakyThrows
    public static void initSysConf(ConfigConf configConf){
        sysConf = SettingUtil.get(configConf.getSysConfLocalPath());
        Response<List<GetValue>> kvValues = ConsulUtil.client().getKVValues(configConf.getSysConfPath());
        if(kvValues != null && kvValues.getValue() != null) {
            sysConf.clear();
            for (GetValue value : kvValues.getValue()) {
                sysConf.put(value.getKey(), value.getDecodedValue());
            }
        }
    }

    public static Setting getSysConf(){
        return sysConf;
    }

    @SneakyThrows
    public static void initUserConf(ConfigConf configConf){
        userConf = SettingUtil.get(configConf.getUserConfLocalPath());
        Response<List<GetValue>> kvValues = ConsulUtil.client().getKVValues(configConf.getUserConfPath());
        if(kvValues != null && kvValues.getValue() != null) {
            userConf.clear();
            for (GetValue value : kvValues.getValue()) {
                userConf.put(value.getKey(), value.getDecodedValue());
            }
            userConf.store(configConf.getUserConfLocalPath());
        }
    }

    public static Setting getUserConf(){
        return userConf;
    }


}
