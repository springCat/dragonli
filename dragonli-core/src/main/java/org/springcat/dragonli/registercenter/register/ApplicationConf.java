package org.springcat.dragonli.registercenter.register;

import cn.hutool.core.net.NetUtil;
import lombok.Data;
import org.springcat.dragonli.config.IConfig;

import java.util.Arrays;
import java.util.List;

@Data
public class ApplicationConf implements IConfig {

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

    /**
     * 生成服务id,每个服务实例唯一
     */
    public String getServiceId(){
        return name + NetUtil.ipv4ToLong(ip) + port;
    }



}
