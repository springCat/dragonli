package org.springcat.dragonli.core;


import cn.hutool.aop.ProxyUtil;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import lombok.SneakyThrows;
import org.springcat.dragonli.core.rpc.*;
import org.springcat.dragonli.core.rpc.exception.RpcException;
import org.springcat.dragonli.core.rpc.ihandle.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *         register
 *      /           \
 *    client -> server
 *
 *    client 需要appName,获取实例列表哇,选出ip和port和rootPath
 *    然后拼接工程部署的根路径,controller name,method name
 *
 *    所以一个完整的url为
 *    http://{ip}:{port}/{rootPath}/{ControllerPath}/{method}
 *
 *    从配置中需要读取
 *    服务提供方的
 *    appName,ControllerPath(可以默认从ClassName中获取),method(可以默认从methodName中获取)
 *
 *
 */
public class RpcStarter {
    private final static Log log = LogFactory.get();

    @SneakyThrows
    public static RpcInvoke init(IServiceProvider serviceProvider){
        RpcConf rpcConf = new RpcConf().load();

        RpcInvoke invoke = new RpcInvoke();

        //注入配置
        invoke.setRpcConf(rpcConf);

        //初始化负载均衡
        invoke.setLoadBalanceRule((ILoadBalanceRule) Class.forName(rpcConf.getLoadBalanceRuleImplClass()).newInstance());
        log.info("init LoadBalanceRule {}",rpcConf.getLoadBalanceRuleImplClass());

        //初始化序列化
        invoke.setSerialize((ISerialize)Class.forName(rpcConf.getSerializeImplClass()).newInstance());
        log.info("init Serialize {}",rpcConf.getSerializeImplClass());

        //初始化http请求客户端
        invoke.setHttpTransform((IHttpTransform) Class.forName(rpcConf.getHttpTransformImplClass()).newInstance());
        log.info("init httpTransform {}",rpcConf.getHttpTransformImplClass());

        //初始化验证
        invoke.setValidation((IValidation) Class.forName(rpcConf.getValidationImplClass()).newInstance());
        log.info("init validation {}",rpcConf.getValidationImplClass());

        //初始化接口代理类
        List<Class<?>> services = RpcUtil.scanRpcService(rpcConf.getScanPackages());
        log.info("find services for rpc{}",services);

        //初始化接口实现类
        Map<Class<?>, Object> serviceImplMap = buildServiceImpl(services,invoke);
        invoke.setServiceImplMap(serviceImplMap);
        log.info("build serviceImpl for rpc success");

        //初始化rpcMethod配置
        Map<Method, RpcMethodInfo> methodRpcMethodInfoMap = initRpcMehod(rpcConf, services);
        invoke.setApiMap(methodRpcMethodInfoMap);
        log.info("init remote Service success");

        //初始化服务列表获取
        Map<String, String[]> appRouteMap = buildAppRouteMap(services);
        serviceProvider.init(appRouteMap);
        invoke.setServiceRegister(serviceProvider);
        log.info("init ServiceRegister {}",serviceProvider);

        return invoke;
    }

    private static Map<String,String[]> buildAppRouteMap(List<Class<?>> services ){
        Map<String,String[]> servicesMap = MapUtil.newHashMap();
        for (Class<?> service : services) {
            String serviceName = RpcUtil.getRpcAppName(service);
            String[] labels = RpcUtil.getRpcLabels(service);
            servicesMap.put(serviceName,labels);
        }
        return servicesMap;
    }

    public static Map<Class<?>,Object> buildServiceImpl(List<Class<?>> services,RpcInvoke rpcInvoke){
        Map<Class<?>,Object> map = new HashMap();
        for (Class<?> service : services) {
            Object impl = ProxyUtil.newProxyInstance(new InvocationHandler() {
                @Override
                public RpcResponse invoke(Object proxy, Method method, Object[] args) throws RpcException {
                    //拒绝处理基本方法
                    if(StrUtil.equalsAny(method.getName(),
                            "toString","clone","equal")){
                        return null;
                    }
                    RpcRequest rpcRequest = new RpcRequest(method,args);
                    return rpcInvoke.invoke(rpcRequest);
                }
            }, service);
            map.put(service,impl);
        }
        return map;
    }


    @SneakyThrows
    public static Map<Method,RpcMethodInfo> initRpcMehod(RpcConf rpcConf,List<Class<?>> services){
        Map<Method, RpcMethodInfo> map = new HashMap<>();
        for (Class<?> service : services) {
            Method[] declaredMethods = service.getDeclaredMethods();

            String className = RpcUtil.geControllerName(service,rpcConf.getRpcServiceClassNameSuffix());

            Map<String, Object> annotationValueMap = AnnotationUtil.getAnnotationValueMap(service, Rpc.class);

            for (Method method : declaredMethods) {
                RpcMethodInfo rpcMethodInfo = new RpcMethodInfo();
                BeanUtil.fillBeanWithMapIgnoreCase(annotationValueMap,rpcMethodInfo,false);
                // 处理前后路径中的/
                rpcMethodInfo.setUrl(StrUtil.strip(rpcMethodInfo.getUrl(),"/"));
                rpcMethodInfo.setRootPath(StrUtil.strip(rpcMethodInfo.getRootPath(),"/"));
                rpcMethodInfo.setControllerPath(className);
                rpcMethodInfo.setMethodName(method.getName());
                rpcMethodInfo.setReturnType(method.getReturnType());
                //初始化方法级别的错误处理
                IErrorHandle errorHandle = (IErrorHandle) Class.forName(rpcConf.getErrorHandleImplClass()).newInstance();
                errorHandle.init(SecureUtil.md5(method.toString()));
                rpcMethodInfo.setIErrorHandle(errorHandle);
                rpcMethodInfo.setLoadBalancerKeyName(rpcConf.getLoadBalanceKeyName());
                map.put(method,rpcMethodInfo);
            }
        }
        return map;
    }
}
