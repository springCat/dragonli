package org.springcat.dragonli.core;


import cn.hutool.aop.ProxyUtil;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ClassUtil;
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
import java.util.*;

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
public class DragonLiStarter {
    private final static Log log = LogFactory.get();

    @SneakyThrows
    public static Map<Class<?>, Object> start(RpcConf rpcConf){

        RpcInvoke invoke = new RpcInvoke();

        //初始化负载均衡
        invoke.setLoadBalanceRule((ILoadBalanceRule) Class.forName(rpcConf.getLoadBalanceRuleImplClass()).newInstance());
        log.info("init LoadBalanceRule {}",rpcConf.getLoadBalanceRuleImplClass());
        //初始化序列化
        invoke.setSerialize((ISerialize)Class.forName(rpcConf.getSerializeImplClass()).newInstance());
        log.info("init Serialize {}",rpcConf.getSerializeImplClass());

        //初始化http请求客户端
        invoke.setHttpTransform((IHttpTransform) Class.forName(rpcConf.getHttpTransformImplClass()).newInstance());
        log.info("init httpTransform {}",rpcConf.getHttpTransformImplClass());

        //初始化服务列表获取
        invoke.setServiceRegister((IServiceRegister) Class.forName(rpcConf.getServiceRegisterImplClass()).newInstance());
        log.info("init ServiceRegister {}",rpcConf.getServiceRegisterImplClass());

        //初始化验证
        invoke.setValidation((IValidation) Class.forName(rpcConf.getValidationImplClass()).newInstance());
        log.info("init Validation {}",rpcConf.getValidationImplClass());

        //初始化接口代理类
        List<Class<?>> services = DragonLiStarter.scanRpcService(rpcConf.getScanPackages());
        log.info("find services for rpc{}",services);

        //初始化接口实现类
        Map<Class<?>, Object> serviceImplMap = buildServiceImpl(services,invoke);
        log.info("build serviceImpl for rpc success");

        //初始化rpcMethod配置
        Map<Method, RpcMethodInfo> methodRpcMethodInfoMap = DragonLiStarter.initRpcMehod(rpcConf, services);
        invoke.setApiMap(methodRpcMethodInfoMap);
        log.info("init remote Service success");
        return serviceImplMap;
    }

    public static List<Class<?>> scanRpcService(String scanPackages){
        List<Class<?>> list = new ArrayList<>();
        String[] scanPackageArray = scanPackages.split(",");
        for (String scanPackage : scanPackageArray) {
            Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(scanPackage, Rpc.class);
            list.addAll(classes);
        }
        return list;
    }

    public static Map<Class<?>,Object> buildServiceImpl(List<Class<?>> services,RpcInvoke rpcInvoke){
        Map<Class<?>,Object> map = new HashMap();
        for (Class<?> service : services) {
            Object impl =ProxyUtil.newProxyInstance(new InvocationHandler() {
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

            String className = service.getSimpleName();
            className = StrUtil.strip(className,rpcConf.getRpcServiceClassNameSuffix());
            className = StrUtil.lowerFirst(className);

            for (Method method : declaredMethods) {
                RpcMethodInfo rpcMethodInfo = new RpcMethodInfo();
                Map<String, Object> annotationValueMap = AnnotationUtil.getAnnotationValueMap(service, Rpc.class);
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
                map.put(method,rpcMethodInfo);
            }
        }
        return map;
    }
}
