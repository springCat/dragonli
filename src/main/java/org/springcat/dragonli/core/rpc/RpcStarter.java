package org.springcat.dragonli.core.rpc;


import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ClassUtil;
import org.springcat.dragonli.core.Proxy;
import org.springcat.dragonli.core.rpc.exception.RpcException;

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
public class RpcStarter {

    public static List<Class<?>> scanRpcService(String scanPackages){
        List<Class<?>> list = new ArrayList<>();
        String[] scanPackageArray = scanPackages.split(",");
        for (String scanPackage : scanPackageArray) {
            Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(scanPackage, Rpc.class);
            list.addAll(classes);
        }
        return list;
    }

    public static Map<Class<?>,Object> convert2RpcServiceImpl(List<Class<?>> services){
        Proxy proxy = new Proxy();
        Map<Class<?>,Object> map = new HashMap();
        for (Class<?> service : services) {
            Object impl = proxy.impl(service);
            map.put(service,impl);
        }
        return map;
    }

    public static void initRpcServicesHook(List<Class<?>> services){
        for (Class<?> service : services) {
            String serviceName = AnnotationUtil.getAnnotationValue(service, Rpc.class);


        }
    }
}
