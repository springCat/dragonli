package org.springcat.dragonli.core;

import cn.hutool.aop.ProxyUtil;
import cn.hutool.core.util.StrUtil;
import org.springcat.dragonli.core.rpc.RpcMethodInfo;
import org.springcat.dragonli.core.rpc.RpcResponse;
import org.springcat.dragonli.core.rpc.exception.RpcException;
import org.springcat.dragonli.core.rpc.RpcInvoke;
import org.springcat.dragonli.core.rpc.RpcRequest;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * rpc服务入口,用于包装服务接口生成实现类的实列,把方法调用转换为远程调用
 */
public class Proxy {

    private static RpcInvoke invoke = new RpcInvoke();

	public Object impl(Class cls){
        Object serviceImpl = ProxyUtil.newProxyInstance(new InvocationHandler() {
            @Override
            public RpcResponse invoke(Object proxy, Method method, Object[] args) throws RpcException {
                //拒绝处理基本方法
                if(StrUtil.equalsAny(method.getName(),
                        "toString","clone","equal")){
                    return null;
                }
                RpcRequest rpcRequest = new RpcRequest(method,args);
                return invoke.invoke(rpcRequest);
            }
        }, cls);
        return serviceImpl;
    }
}
