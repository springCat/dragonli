package org.springcat.dragonli.core.consul;

import com.ecwid.consul.v1.ConsulClient;
import lombok.experimental.UtilityClass;
import org.springcat.dragonli.core.registry.AppInfo;

@UtilityClass
public class ConsulUtil {

    private static ConsulClient client;

    private static AppInfo innterAppInfo;

    public static void initAppInfo(AppInfo appInfo){
        innterAppInfo = appInfo;
    }

    public static void init(String ip,int port){
        client = new ConsulClient(ip,port);
    }

    public static ConsulClient use(){
        return client;
    }

    public static AppInfo getAppInfo(){
        return innterAppInfo;
    }

}
