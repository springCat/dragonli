package org.springcat.dragonli.core.consul;

import com.ecwid.consul.v1.ConsulClient;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ConsulUtil {

    private static ConsulClient client;


    public static void init(ConsulInfo consulInfo){
        client = new ConsulClient(consulInfo.getIp(),consulInfo.getPort());
    }

    public static ConsulClient client(){
        return client;
    }



}
