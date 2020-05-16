package org.springcat.dragonli.core.consul;

import lombok.Data;
import org.springcat.dragonli.core.config.IConfig;


@Data
public class ConsulConf implements IConfig {

    private String ip = "127.0.0.1";

    private int port = 8500;

}
