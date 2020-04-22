package org.springcat.dragonli.core.rpc;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.springcat.dragonli.core.Context;
import org.springcat.dragonli.core.validate.ValidateException;
import org.springcat.dragonli.core.validate.ValidationUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Data
public class RpcRequest{

    public RpcRequest(Method method, Object[] args) throws ValidateException {
        requestObj = ArrayUtil.get(args, 0);
        Map<String,String> reqHeader = ArrayUtil.get(args, 1);
        supplier = ArrayUtil.get(args, 2);

        rpcHeader = Context.getAllRpcParam();
        if(reqHeader != null){
            rpcHeader.putAll(reqHeader);
        }

        Class<?> declaringClass = method.getDeclaringClass();
        Map<String, Object> map = AnnotationUtil.getAnnotationValueMap(declaringClass, Rpc.class);
        serviceName = (String) map.get("value");
        labels = (String[]) map.get("labels");

        className = declaringClass.getSimpleName();
        returnType = method.getReturnType();
        methodName = method.getName();
    }

    private Method method;

    private Map<String,String> rpcHeader;

    private Object requestObj;

    private Supplier supplier;

    private String serviceName;

    private String[] labels;

    private String methodName;

    private Class returnType;

    private String className;

    private ISerialize serialize;

}
