package org.springcat.dragonli.util.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;

/**
 * @Description IConfig
 * @Author springCat
 * @Date 2020/4/30 11:53
 */
public interface IConfig {

    default <T> T load(){
        String className = this.getClass().getSimpleName();
        className = StrUtil.removeSuffix(className, "Conf");
        className = StrUtil.lowerFirst(className);
        return SettingConfUtil.get(className, this);
    }

    default Setting loadSetting(){
        String className = this.getClass().getSimpleName();
        className = StrUtil.removeSuffix(className, "Conf");
        className = StrUtil.lowerFirst(className);
        return SettingConfUtil.get(className);
    }
}
