package org.springcat.dragonli.core.registry;

import lombok.Data;
import java.util.List;

@Data
public class AppConf {

    private String name;

    private String ip;

    private int port;

    private String checkUrl;

    private String checkTimout;

    private String checkInterval;

    private List<String> appTags;

}
