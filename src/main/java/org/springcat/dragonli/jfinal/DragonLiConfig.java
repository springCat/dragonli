package org.springcat.dragonli.jfinal;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.config.*;
import com.jfinal.template.Engine;
import org.springcat.dragonli.consul.ConsulUtil;
import org.springcat.dragonli.context.Context;
import org.springcat.dragonli.jfinal.plugin.ConsulRegistryPlugin;
import org.springcat.dragonli.jfinal.plugin.RpcPlugin;
import org.springcat.dragonli.registry.AppInfo;

import java.util.Arrays;

/**
 * 仅仅为了简化初始化配置
 */
public abstract class DragonLiConfig extends JFinalConfig {

    @Override
    public void configConstant(Constants me) {
        //目前返回值无用,留个hook,后续可以用于获取jf内置参数
        me = configConstantPlus(me);
        ConsulUtil.init("127.0.0.1",8500);
    }

    public abstract Constants configConstantPlus(Constants me);

    @Override
    public void configRoute(Routes me) {
        me = configRoutePlus(me);
        JFinalStatusController.init(me);
    }

    public abstract Routes configRoutePlus(Routes me);


    @Override
    public void configPlugin(Plugins me) {
        me = configPluginPlus(me);

        //RegistryP
        AppInfo appInfo = AppInfo.builder()
                .name("jfinalDemo")
                .address("10.0.75.1")
                .port(8080)
                .checkUrl("http://10.0.75.1:8080/status")
                .checkInterval("10s")
                .checkTimout("1s")
                .appTags(Arrays.asList("urlprefix-/jfinal/"))
                .build();
        ConsulRegistryPlugin consulRegistryPlugin = new ConsulRegistryPlugin(ConsulUtil.use(),appInfo);
        me.add(consulRegistryPlugin);
        //init rpc client
        RpcPlugin rpcPlugin = new RpcPlugin("com.demo.blog");
        me.add(rpcPlugin);
    }

    public abstract Plugins configPluginPlus(Plugins me);

    @Override
    public void configInterceptor(Interceptors me) {
        me = configInterceptorPlus(me);
        me.add(new Interceptor() {
            @Override
            public void intercept(Invocation inv) {
                Context.init();
                inv.invoke();
                Context.clear();
            }
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
