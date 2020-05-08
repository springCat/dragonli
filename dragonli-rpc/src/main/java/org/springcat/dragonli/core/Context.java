package org.springcat.dragonli.core;

import cn.hutool.core.lang.Dict;

import java.util.HashMap;
import java.util.Map;


/**
 * 用于跨参数方法传递,大部分场景都在系统框架层面,应用层面应该慎重使用
 */
public class Context {

    /**
     * 会随着调用传递到下个组件中
     */
    private static ThreadLocal<Map<String,String>> rpcParam = new InheritableThreadLocal<>();

    /**
     *  只在当前组件中起作用
     */
    private static ThreadLocal<Dict> reqParam = new InheritableThreadLocal<>();

    public static void init(){
        rpcParam.set(new HashMap<>());
        reqParam.set(new Dict());
    }

    public static void clear(){
        rpcParam.remove();
        reqParam.remove();
    }

    public static void setRpcParam(String key,String value){
        rpcParam.get().put(key,value);
    }

    public static String getRpcParam(String key){
        return rpcParam.get().get(key);
    }

    public static Map<String,String> getAllRpcParam(){
        return rpcParam.get();
    }

    public static Dict getReqParam(){
        return reqParam.get();
    }
}
