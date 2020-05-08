package org.springcat.dragonli.util.config;

import cn.hutool.setting.Setting;
import lombok.experimental.UtilityClass;

/**
 *
 * @Description dragonli.setting,打包后不再变化
 * @Author springCat
 * @Date 2020/4/30 11:35
 */

@UtilityClass
public class SettingConfUtil {

    private Setting setting = new Setting("dragonli.setting",true);

    public <T> T get(String settingGroupName, Class cls){
        return (T) setting.getSetting(settingGroupName).toBean(cls);
    }

    public <T> T get(String settingGroupName, Object obj){
        return (T) setting.getSetting(settingGroupName).toBean(obj);
    }

    public Setting get(String settingGroupName){
        return setting.getSetting(settingGroupName);
    }

}
