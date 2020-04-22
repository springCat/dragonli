package org.springcat.dragonli.jfinal;

import cn.hutool.core.util.StrUtil;
import com.jfinal.config.*;
import com.jfinal.core.Controller;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.template.Engine;
import org.springcat.dragonli.core.rpc.RpcInfo;
import org.springcat.dragonli.core.Context;
import org.springcat.dragonli.jfinal.plugin.ConsulPlugin;
import org.springcat.dragonli.jfinal.plugin.RpcPlugin;
import org.springcat.dragonli.core.registry.AppInfo;
import org.springcat.dragonli.core.registry.ConsulInfo;
import org.springcat.dragonli.core.config.SettingGroup;
import org.springcat.dragonli.core.config.SettingUtil;


/**
 * 仅仅为了简化初始化配置
 */
public abstract class DragonLiConfig extends JFinalConfig {

    @Override
    public void configConstant(Constants me) {
        configConstantPlus(me);
        me.setConfigPluginOrder(1);
        me.setInjectDependency(true);
        // 配置对超类中的属性进行注入
        me.setInjectSuperClass(true);
        me.setJsonFactory(new MixedJsonFactory());
    }

    public abstract void configConstantPlus(Constants me);

    @Override
    public void configRoute(Routes me) {
        configRoutePlus(me);
        JFinalStatusController.init(me);
    }

    public abstract void configRoutePlus(Routes me);

    @Override
    public void configPlugin(Plugins me) {

        ConsulInfo consulInfo = SettingUtil.getConfBean(SettingGroup.consul);
        AppInfo appInfo = SettingUtil.getConfBean(SettingGroup.app);
        RpcInfo rpcInfo = SettingUtil.getConfBean(SettingGroup.rpc);

        //为了先从配置中心拉取配置
        me.add(new ConsulPlugin(consulInfo,appInfo));

        configPluginPlus(me);

        //init rpc client
        RpcPlugin rpcPlugin = new RpcPlugin(rpcInfo);
        me.add(rpcPlugin);
}

    public abstract void configPluginPlus(Plugins me);

    @Override
    public void configInterceptor(Interceptors me) {
        configInterceptorPlus(me);
        me.add(inv -> {
            Context.init();
            Context.setRpcParam("client-ip",getClientIp(inv.getController()));
            inv.invoke();
            Context.clear();
        });
    }

    /**
     * 1 先判断client-ip是否已经存在,用于消费者已经获取客户端IP,传递到生产者的场景
     * 2 不存在client-ip,就获取x-forwarded-for的值,根据http协议从反向代理服务器来的请求赋值到这个值
     * 3 不存在的话,就直接获取请求发起端的ip,用于服务没有前置的反向代理,直接面对用户的场景
     * @param controller
     * @return
     */
    private String getClientIp(Controller controller){
        String ip = controller.getHeader("client-ip");
        if (StrUtil.isBlank(ip)) {
             ip = controller.getHeader("x-forwarded-for");
        }
        if (StrUtil.isBlank(ip)) {
            ip =  controller.getRequest().getRemoteAddr();
        }
        return ip;
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
