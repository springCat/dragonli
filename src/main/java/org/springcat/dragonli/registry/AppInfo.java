package org.springcat.dragonli.registry;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Builder;
import lombok.Data;

import java.net.InetAddress;
import java.util.List;

@Builder
@Data
public class AppInfo {

    private String name;

    private String address;

    private int port;

    private String checkUrl;

    private String checkTimout;

    private String checkInterval;

    private List<String> appTags;

}
