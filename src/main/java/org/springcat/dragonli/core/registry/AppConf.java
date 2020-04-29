package org.springcat.dragonli.core.registry;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.springcat.dragonli.core.config.ConfigUtil;
import org.springcat.dragonli.core.config.SettingGroup;

import java.util.Arrays;
import java.util.List;

@Data
public class AppConf {
    private String name;

    private String ip;

    private int port;

    private String rootPath;

    private String checkUrl;

    private String checkTimout;

    private String checkInterval;

    private String healthCheckPath;

    /**
     * 标识机器分组,比如现网debug点等等
     */
    private List<String> appTags = Arrays.asList("DEFAULT");

    public static AppConf getInstance(){
        AppConf appConf = ConfigUtil.getPrjConf(SettingGroup.application);
        String envConf = ConfigUtil.getEnvConf("APPLICATION_GROUP","");
        List<String> split = StrUtil.split(envConf, ',');
        if(CollectionUtil.isNotEmpty(split)) {
            appConf.setAppTags(split);
        }
        return appConf;
    }
}
