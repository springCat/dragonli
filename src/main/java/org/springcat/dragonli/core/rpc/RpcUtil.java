package org.springcat.dragonli.core.rpc;

import cn.hutool.core.util.ClassUtil;
import lombok.experimental.UtilityClass;
import org.springcat.dragonli.core.Proxy;

import java.util.*;


public class RpcUtil {

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


}
