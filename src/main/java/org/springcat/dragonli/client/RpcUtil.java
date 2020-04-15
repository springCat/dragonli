package org.springcat.dragonli.client;

import cn.hutool.core.util.ClassUtil;
import lombok.experimental.UtilityClass;
import java.util.*;

@UtilityClass
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

    public Map<Class<?>,Object> convert2RpcServiceImpl(List<Class<?>> services){
        Map<Class<?>,Object> map = new HashMap();
        for (Class<?> service : services) {
            Object impl = Proxy.me.impl(service);
            map.put(service,impl);
        }
        return map;
    }
}
