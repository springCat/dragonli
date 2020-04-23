package org.springcat.dragonli.core;

import cn.hutool.aop.ProxyUtil;
import cn.hutool.core.util.StrUtil;
import org.springcat.dragonli.core.rpc.exception.RpcException;
import org.springcat.dragonli.core.rpc.RpcInvoke;
import org.springcat.dragonli.core.rpc.RpcRequest;
import org.springcat.dragonli.core.rpc.exception.ValidateException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class Proxy {

    private static RpcInvoke invoke = new RpcInvoke();

	public Object impl(Class cls){
        Object impl = ProxyUtil.newProxyInstance(new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws RpcException, ValidateException {
                //拒绝处理基本方法
                if(StrUtil.equalsAny(method.getName(),
                        "toString","clone","equal")){
                    return null;
                }
                RpcRequest rpcRequest = new RpcRequest(method,args);
                return invoke.invoke(rpcRequest);
            }
        }, cls);
        return impl;
    }
}
