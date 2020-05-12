package org.springcat.dragonli.gateway;


import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.Setting;
import org.springcat.dragonli.core.rpc.ihandle.IErrorHandle;
import org.springcat.dragonli.core.rpc.ihandle.IHttpTransform;
import org.springcat.dragonli.core.rpc.ihandle.ILoadBalanceRule;
import org.springcat.dragonli.core.rpc.ihandle.IServiceProvider;
import org.springcat.dragonli.util.configcenter.ConfigCenter;
import org.springcat.dragonli.util.consul.Consul;
import org.springcat.dragonli.util.registercenter.register.ApplicationConf;
import org.springcat.dragonli.util.registercenter.register.ServiceRegister;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApiGatewayInvokeStarter {

    private final static Log log = LogFactory.get();

    public static ApiGatewayInvoke init(){
        try {
            ApiGatewayInvoke invoke = new ApiGatewayInvoke();

            //init consul
            Consul consul = new Consul();

            //init service register
            ApplicationConf applicationConf = new ApplicationConf().load();
            ServiceRegister serviceRegister = new ServiceRegister(consul);
            serviceRegister.register(applicationConf);

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
            Setting routes = ConfigCenter.getRouteConf().getConfigList();
            log.info("init routes {}", routes);

            //初始化错误处理
            initRoute(invoke, apiGateWayConf,routes);

            //初始化服务列表获取
            IServiceProvider serviceProvider = (IServiceProvider) Class.forName(apiGateWayConf.getServiceRegisterImplClass()).newInstance();
            Map<String,String[]> appRouteMap = new HashMap<>();
            for (Map.Entry<String, String> stringStringEntry : routes.entrySet()) {
                String key = stringStringEntry.getKey();
                String value = stringStringEntry.getValue();
                String[] labels = StrUtil.split(value, ",");
                appRouteMap.put(key.replace("/routeConf/",""),labels);
            }
            serviceProvider.init(appRouteMap);
            invoke.setServiceRegister(serviceProvider);
            log.info("init ServiceRegister {}", apiGateWayConf.getServiceRegisterImplClass());

            return invoke;
        }catch (Exception e){
            log.info(e.getMessage());
            return null;
        }
    }

    private static void initRoute(ApiGatewayInvoke invoke,ApiGateWayConf apiGateWayConf,Setting routes){
        Map<String, IErrorHandle> iErrorHandleMap = invoke.getIErrorHandleMap();
        if(!routes.isEmpty()){
            //初始化错误处理
            List<String> groups = routes.getGroups();
            //获取appName list
            for (String group : groups) {
                Set<String> keys = routes.keySet(group);
                for (String rawUrl : keys) {
                    String key = group+rawUrl;
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
            }
        }

        invoke.setApiExposeUrls(routes);
        invoke.setIErrorHandleMap(iErrorHandleMap);
        log.info("init errorHandle {}", iErrorHandleMap);
    }

}
