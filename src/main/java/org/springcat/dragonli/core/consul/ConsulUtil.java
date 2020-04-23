package org.springcat.dragonli.core.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.health.HealthServicesRequest;
import com.ecwid.consul.v1.health.model.HealthService;
import lombok.experimental.UtilityClass;
import org.springcat.dragonli.core.registry.AppInfo;

import java.util.List;

@UtilityClass
public class ConsulUtil {

    private static ConsulClient client;

    private static AppInfo innterAppInfo;

    public static void initAppInfo(AppInfo appInfo){
        innterAppInfo = appInfo;
    }

    public static void init(ConsulInfo consulInfo){
        client = new ConsulClient(consulInfo.getIp(),consulInfo.getPort());
    }

    public static ConsulClient client(){
        return client;
    }

    public static AppInfo getAppInfo(){
        return innterAppInfo;
    }



}
