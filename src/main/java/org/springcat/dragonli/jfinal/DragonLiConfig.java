package org.springcat.dragonli.jfinal;

import cn.hutool.core.util.StrUtil;
import com.jfinal.config.*;
import com.jfinal.kit.Prop;
import com.jfinal.template.Engine;
import org.springcat.dragonli.context.Context;
import org.springcat.dragonli.jfinal.plugin.ConsulPlugin;
import org.springcat.dragonli.jfinal.plugin.RpcPlugin;
import org.springcat.dragonli.registry.AppInfo;

/**
 * 仅仅为了简化初始化配置
 */
public abstract class DragonLiConfig extends JFinalConfig {

    private static Prop p;

    @Override
    public void configConstant(Constants me) {
        p = configConstantPlus(me);
        me.setConfigPluginOrder(1);
    }

    public abstract Prop configConstantPlus(Constants me);

    @Override
    public void configRoute(Routes me) {
        me = configRoutePlus(me);
        JFinalStatusController.init(me);
    }

    public abstract Routes configRoutePlus(Routes me);


    @Override
    public void configPlugin(Plugins me) {
        String ip = p.get("app.consul.ip");
        Integer port = p.getInt("app.consul.port");
        //为了先从配置中心拉取配置
        me.add( new ConsulPlugin(ip,port,initAppInfo()));

        me = configPluginPlus(me);

        //init rpc client
        RpcPlugin rpcPlugin = new RpcPlugin(p.get("app.scanPackages",""));
        me.add(rpcPlugin);



    }

    private AppInfo initAppInfo(){

        String appName = p.get("app.name");
        String appIp = p.get("app.ip");
        int appPort = p.getInt("app.port",8080);
        String appHealthCheckUrl = p.get("app.health.checkUrl",StrUtil.format("http://{}:{}/status", appIp, appPort));
        String appHealthInterval = p.get("app.health.interval","3s");
        String appHealthTimout = p.get("app.health.timout","1s");
        String appLabel = p.get("app.label","");

        AppInfo appInfo = AppInfo.builder()
                .name(appName)
                .address(appIp)
                .port(appPort)
                .checkUrl(appHealthCheckUrl)
                .checkInterval(appHealthInterval)
                .checkTimout(appHealthTimout)
                .appTags(StrUtil.split(appLabel, ','))
                .build();
        return appInfo;
    }

    public abstract Plugins configPluginPlus(Plugins me);

    @Override
    public void configInterceptor(Interceptors me) {
        me = configInterceptorPlus(me);
        me.add(inv -> {
            Context.init();
            inv.invoke();
            Context.clear();
        });
    }

    public abstract Interceptors configInterceptorPlus(Interceptors me);



    @Override
    public void configEngine(Engine me) {
        me = configEnginePlus(me);
    }
    /**
     * Config engine
     */
    public abstract Engine configEnginePlus(Engine me);


    @Override
    public void configHandler(Handlers me){
        me = configHandlerPlus(me);
    }
    /**
     * Config handler
     */
    public abstract Handlers configHandlerPlus(Handlers me);

}
