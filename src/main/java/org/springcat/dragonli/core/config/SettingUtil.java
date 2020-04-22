package org.springcat.dragonli.core.config;

import cn.hutool.setting.Setting;

public class SettingUtil {

    private final static Setting setting;

    static {
        setting = new Setting("dragonli.setting",true);
        setting.autoLoad(true);
    }

    public static <T> T getConfBean(SettingGroup settingGroup){
        try {
            T bean = (T) setting.getSetting(settingGroup.name()).toBean(settingGroup.getCls().newInstance());
            return bean;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
