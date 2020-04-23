package org.springcat.dragonli.core.consul;

import com.ecwid.consul.v1.ConsulClient;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ConsulUtil {

    private static ConsulClient client;


    public static void init(ConsulConf consulConf){
        client = new ConsulClient(consulConf.getIp(), consulConf.getPort());
    }

    public static ConsulClient client(){
        return client;
    }



}
