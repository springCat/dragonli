package org.springcat.dragonli.gateway;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.Setting;
import org.springcat.dragonli.configcenter.ConfigCenter;
import org.springcat.dragonli.consul.Consul;
import org.springcat.dragonli.handle.ILoadBalanceRule;
import org.springcat.dragonli.registercenter.provider.ConsulServiceProvider;
import org.springcat.dragonli.registercenter.register.ServiceRegister;
import org.springcat.dragonli.rpc.ihandle.IErrorHandle;
import org.springcat.dragonli.rpc.ihandle.IHttpTransform;

import java.util.HashMap;
import java.util.Map;

public class ApiGatewayInvokeStarter {

    private final static Log log = LogFactory.get();

    public static ApiGatewayInvoke init(){
        try {
            ApiGatewayInvoke invoke = new ApiGatewayInvoke();

            //init consul
            Consul consul = new Consul();

            //init service register
            ServiceRegister serviceRegister = new ServiceRegister(consul);
            serviceRegister.register();

            //init config
            ConfigCenter.init(consul);
            ConfigCenter.defaultInit();

            //注入配置
            ApiGateWayConf apiGateWayConf = new ApiGateWayConf().load();
            invoke.setApiGateWayConf(apiGateWayConf);

            //初始化负载均衡
            invoke.setLoadBalanceRule((ILoadBalanceRule) Class.forName(apiGateWayConf.getLoadBalanceRuleImplClass()).newInstance());
            log.info("init LoadBalanceRule {}", apiGateWayConf.getLoadBalanceRuleImplClass());

            //初始化http请求客户端
            invoke.setHttpTransform((IHttpTransform) Class.forName(apiGateWayConf.getHttpTransformImplClass()).newInstance());
            log.info("init httpTransform {}", apiGateWayConf.getHttpTransformImplClass());

            //从配置中心拉取所有服务
            Setting routes = ConfigCenter.getRouteConf().pullConfigList();
            log.info("init routes {}", routes);

            //初始化错误处理
            initRoute(invoke, apiGateWayConf,routes);

            //初始化服务列表获取
            ConsulServiceProvider consulServiceProvider = new ConsulServiceProvider();
            consulServiceProvider.init(consul);


            Map<String,String[]> appRouteMap = new HashMap<>();
            for (Map.Entry<String, String> stringStringEntry : routes.entrySet()) {
                String key = stringStringEntry.getKey();
                String value = stringStringEntry.getValue();
                String[] labels = StrUtil.split(value, ",");
                appRouteMap.put(key.replace("/routeConf/",""),labels);
            }
            consulServiceProvider.init(appRouteMap);
            invoke.setServiceRegister(consulServiceProvider);
            log.info("init ServiceRegister {}", apiGateWayConf.getServiceRegisterImplClass());

            return invoke;
        }catch (Exception e){
            log.info(e.getMessage());
            return null;
        }
    }

    private static void initRoute(ApiGatewayInvoke invoke,ApiGateWayConf apiGateWayConf,Setting routes){
        Map<String, IErrorHandle> iErrorHandleMap = invoke.getIErrorHandleMap();
        for (Map.Entry<String, String> entity : routes.entrySet()) {
            String key = entity.getKey();
            iErrorHandleMap.computeIfAbsent(key,k ->{
                try {
                    IErrorHandle iErrorHandle = (IErrorHandle) Class.forName(apiGateWayConf.getErrorHandleImplClass()).newInstance();
                    iErrorHandle.init(key);
                    return iErrorHandle;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            });
        }
        invoke.setIErrorHandleMap(iErrorHandleMap);
        log.info("init errorHandle {}", iErrorHandleMap);
    }

}
