package org.springcat.dragonli.registry;

import lombok.Data;
import java.util.List;

@Data
public class AppInfo {

    private String name;

    private String ip;

    private int port;

    private String checkUrl;

    private String checkTimout;

    private String checkInterval;

    private List<String> appTags;

}
