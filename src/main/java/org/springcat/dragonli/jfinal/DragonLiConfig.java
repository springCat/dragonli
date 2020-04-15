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

public abstract class DragonLiConfig extends JFinalConfig {

    @Override
    public void configConstant(Constants me) {
        ConsulUtil.init("127.0.0.1",8500);
    }

    @Override
    public void configRoute(Routes me) {
        JFinalStatusController.init(me);
    }

    @Override
    public void configPlugin(Plugins me) {

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

    @Override
    public void configInterceptor(Interceptors me) {
		me.add(new Interceptor() {
			@Override
			public void intercept(Invocation inv) {
				Context.init();
				inv.invoke();
				Context.clear();
			}
		});
    }


}
