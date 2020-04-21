package org.springcat.dragonli.jfinal;

import cn.hutool.core.util.StrUtil;
import com.jfinal.config.*;
import com.jfinal.core.Controller;
import com.jfinal.json.FastJsonFactory;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.kit.Prop;
import com.jfinal.template.Engine;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springcat.dragonli.context.Context;
import org.springcat.dragonli.jfinal.plugin.ConsulPlugin;
import org.springcat.dragonli.jfinal.plugin.RpcPlugin;
import org.springcat.dragonli.registry.AppInfo;

/**
 * 仅仅为了简化初始化配置
 */
@NoArgsConstructor
@Data
public abstract class DragonLiConfig extends JFinalConfig {

    private String consulIp;

    private int consulPort;

    private AppInfo appInfo;

    private String scanPackages;

    public Prop loadDragonLiProp(){
        return null;
    }

    private boolean initByUser(){
        return false;
    }

    private boolean initByProp(){
        Prop p = loadDragonLiProp();
        if(p != null) {
            String appName = p.get("app.name");
            String appIp = p.get("app.ip");
            int appPort = p.getInt("app.port", 8080);
            String appHealthCheckUrl = p.get("app.health.checkUrl", StrUtil.format("http://{}:{}/status", appIp, appPort));
            String appHealthInterval = p.get("app.health.interval", "3s");
            String appHealthTimout = p.get("app.health.timout", "1s");
            String appLabel = p.get("app.label", "");

            AppInfo appInfo = AppInfo.builder()
                    .name(appName)
                    .address(appIp)
                    .port(appPort)
                    .checkUrl(appHealthCheckUrl)
                    .checkInterval(appHealthInterval)
                    .checkTimout(appHealthTimout)
                    .appTags(StrUtil.split(appLabel, ','))
                    .build();

            this.appInfo = appInfo;
            this.consulIp = p.get("app.consul.ip","127.0.0.1");
            this.consulPort = p.getInt("app.consul.port",8500);
            this.scanPackages = p.get("app.scanPackages", "");
            return true;
        }
        return false;
    }

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
        if(!initByUser()){
            if(!initByProp()){
                return;
            }
        }
        //为了先从配置中心拉取配置
        me.add( new ConsulPlugin(consulIp,consulPort,appInfo));

        configPluginPlus(me);

        //init rpc client
        RpcPlugin rpcPlugin = new RpcPlugin(scanPackages);
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
