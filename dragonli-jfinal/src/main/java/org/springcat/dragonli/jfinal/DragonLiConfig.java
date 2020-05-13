package org.springcat.dragonli.jfinal;

import com.jfinal.config.*;
import com.jfinal.json.JacksonFactory;
import com.jfinal.template.Engine;
import org.springcat.dragonli.rpc.Context;
import org.springcat.dragonli.rpc.RpcConf;
import org.springcat.dragonli.rpc.RpcUtil;
import org.springcat.dragonli.jfinal.health.JFinalStatusController;
import org.springcat.dragonli.jfinal.plugin.DragonLiPlugin;
import org.springcat.dragonli.registercenter.register.ApplicationConf;


/**
 * 仅仅为了简化初始化配置
 */
public abstract class DragonLiConfig extends JFinalConfig {


    private ApplicationConf appConf = new ApplicationConf().load();

    private RpcConf rpcConf = new RpcConf().load();

    @Override
    public void configConstant(Constants me) {
        configConstantPlus(me);
        me.setConfigPluginOrder(1);
        me.setInjectDependency(true);
        // 配置对超类中的属性进行注入
        me.setInjectSuperClass(true);
        me.setJsonFactory(new JacksonFactory());
    }

    public abstract void configConstantPlus(Constants me);

    @Override
    public void configRoute(Routes me) {
        configRoutePlus(me);
        me.add("config",ConfigController.class);
        //在JFinalStatusController前会被统计进status/urls接口,在这个方法后面就不会
        JFinalStatusController.init(me,appConf);
    }

    public abstract void configRoutePlus(Routes me);

    @Override
    public void configPlugin(Plugins me) {
        DragonLiPlugin dragonLiPlugin = new DragonLiPlugin();
        me.add(dragonLiPlugin);
        configPluginPlus(me);
    }

    public abstract void configPluginPlus(Plugins me);

    @Override
    public void configInterceptor(Interceptors me) {
        configInterceptorPlus(me);
        me.add(inv -> {
            Context.init();
            //传递rpc调用间的参数
            Context.setRpcParam(rpcConf.getLoadBalanceKeyName(), RpcUtil.getClientIp(inv.getController().getRequest()));
            inv.invoke();
            Context.clear();
        });
    }

    public abstract void configInterceptorPlus(Interceptors me);

    @Override
    public void configEngine(Engine me) {
        configEnginePlus(me);
    }
    /**
     * Config engine
     */
    public abstract void configEnginePlus(Engine me);

    @Override
    public void configHandler(Handlers me){
        configHandlerPlus(me);
    }
    /**
     * Config handler
     */
    public abstract void configHandlerPlus(Handlers me);

}
