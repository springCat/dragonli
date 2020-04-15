package org.springcat.dragonli.client;

import cn.hutool.aop.ProxyUtil;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.StrUtil;
import org.springcat.dragonli.context.Context;
import org.springcat.dragonli.validate.ValidateException;
import org.springcat.dragonli.validate.ValidationUtil;
import javax.validation.ConstraintViolation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Proxy {

    public static Proxy me = new Proxy();

	public Object impl(Class cls){
        Object impl = ProxyUtil.newProxyInstance(new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws RpcException, ValidateException {
                //拒绝处理基本方法
                if(StrUtil.equalsAny(method.getName(), "toString","clone","equal")){
                    return null;
                }
                RpcRequest request = new RpcRequest();
                if(args != null){
                    if(args.length > 0){
                        //客户端参数验证不通过,直接拒绝,不进行调用
                        ValidationUtil.validate(args[0]);
                        request.setBody(args[0]);
                    } else if(args.length > 1 && args[1] instanceof Map){

                        request.setHeader((Map<String, String>) args[1]);
                    }
                }
                request.setServiceName(AnnotationUtil.getAnnotationValue(method.getDeclaringClass(), Rpc.class));
                request.setClassName(StrUtil.strip(method.getDeclaringClass().getSimpleName(),"Service").toLowerCase());
                request.setMethodName(method.getName());

                if(request.getHeader() == null){
                    request.setHeader(new HashMap<String, String>());
                }
                //为框架 调用链日志,负载均衡等保留
                Map<String, String> sysHeader = Context.getAll();
                if(sysHeader != null) {
                    request.getHeader().putAll(sysHeader);
                }

                Class<?> returnType = method.getReturnType();
                return HttpInvoker.invoke(request,returnType);
            }
        }, cls);
        return impl;
    }
}
