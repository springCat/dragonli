package org.springcat.dragonli.gateway;


import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.Setting;
import org.springcat.dragonli.core.rpc.ihandle.IErrorHandle;
import org.springcat.dragonli.core.rpc.ihandle.IHttpTransform;
import org.springcat.dragonli.core.rpc.ihandle.ILoadBalanceRule;
import org.springcat.dragonli.core.rpc.ihandle.IServiceProvider;
import org.springcat.dragonli.core.rpc.ihandle.impl.ConsulServiceProvider;
import org.springcat.dragonli.util.configcenter.ConfigCenter;
import org.springcat.dragonli.util.configcenter.ConfigCenterConf;
import org.springcat.dragonli.util.consul.Consul;
import org.springcat.dragonli.util.consul.ConsulConf;
import org.springcat.dragonli.util.registercenter.register.ApplicationConf;
import org.springcat.dragonli.util.registercenter.register.ServiceRegister;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApiGatewayInvokeStarter {

    private final static Log log = LogFactory.get();

    public static ApiGatewayInvoke init(){
        try {
            ApiGatewayInvoke invoke = new ApiGatewayInvoke();

            //init consul
            ConsulConf consulConf = new ConsulConf().load();
            Consul consul = new Consul().connect(consulConf);

            //init service register
            ApplicationConf applicationConf = new ApplicationConf().load();
            ServiceRegister serviceRegister = new ServiceRegister(consul);
            serviceRegister.register(applicationConf);

            //init consulServiceProvider for connect ServiceProvider and Rpc
            ConsulServiceProvider consulServiceProvider = new ConsulServiceProvider(consul);

            //init config
            ConfigCenterConf configCenterConf = new ConfigCenterConf().load();
            ConfigCenter.init(configCenterConf, consul);

            //注入配置
            ApiGateWayConf apiGateWayConf = new ApiGateWayConf().load();
            invoke.setApiGateWayConf(apiGateWayConf);

            //初始化负载均衡
            invoke.setLoadBalanceRule((ILoadBalanceRule) Class.forName(apiGateWayConf.getLoadBalanceRuleImplClass()).newInstance());
            log.info("init LoadBalanceRule {}", apiGateWayConf.getLoadBalanceRuleImplClass());

            //初始化http请求客户端
            invoke.setHttpTransform((IHttpTransform) Class.forName(apiGateWayConf.getHttpTransformImplClass()).newInstance());
            log.info("init httpTransform {}", apiGateWayConf.getHttpTransformImplClass());


            //初始化路由
            initRoute(invoke, apiGateWayConf);

            Setting apiExposeUrls = invoke.getApiExposeUrls();
            //初始化服务列表获取
            IServiceProvider serviceProvider = (IServiceProvider) Class.forName(apiGateWayConf.getServiceRegisterImplClass()).newInstance();
            serviceProvider.init();
            invoke.setServiceRegister(serviceProvider);
            log.info("init ServiceRegister {}", apiGateWayConf.getServiceRegisterImplClass());

            return invoke;
        }catch (Exception e){
            log.info(e.getMessage());
            return null;
        }
    }

    private static void initRoute(ApiGatewayInvoke invoke,ApiGateWayConf apiGateWayConf){
        Map<String, IErrorHandle> iErrorHandleMap = invoke.getIErrorHandleMap();
        //apiExposeUrls
        Setting apiExposeUrls = ApiUrlFetcher.refreshApiExposeUrls(apiGateWayConf);
        if(!apiExposeUrls.isEmpty()){
            //初始化错误处理
            List<String> groups = apiExposeUrls.getGroups();
            //获取appName list
            for (String group : groups) {
                Set<String> keys = apiExposeUrls.keySet(group);
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

        invoke.setApiExposeUrls(apiExposeUrls);
        invoke.setIErrorHandleMap(iErrorHandleMap);
    }

}
