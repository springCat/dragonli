//package org.springcat.dragonli.jfinal.plugin;
//
//import cn.hutool.log.Log;
//import cn.hutool.log.LogFactory;
//import com.ecwid.consul.v1.ConsulClient;
//import com.jfinal.plugin.IPlugin;
//import org.springcat.dragonli.registry.AppInfo;
//import org.springcat.dragonli.registry.ConsulRegister;
//
//public class ConsulRegistryPlugin implements IPlugin {
//
//    Log log = LogFactory.get(ConsulRegistryPlugin.class);
//
//
//    @Override
//    public boolean start() {
//        try {
//            ConsulRegister.register(client, appInfo);
//            log.info("consul registr success serviceId:"+ConsulRegister.genServiceId(appInfo));
//            return true;
//        } catch (Exception exception) {
//            log.error(exception.getMessage());
//        }
//        return false;
//    }
//
//    @Override
//    public boolean stop() {
//        try {
//            ConsulRegister.unregister(client, appInfo);
//            String appId = ConsulRegister.genServiceId(appInfo);
//            log.info("consul unregistr success serviceId:"+appId);
//            log.info("consul unregistr success servicecheck:"+"service:"+appId);
//            return true;
//        } catch (Exception exception) {
//            log.error(exception.getMessage());
//        }
//        return false;
//    }
//}
