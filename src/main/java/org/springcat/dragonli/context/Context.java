package org.springcat.dragonli.context;

import java.util.HashMap;
import java.util.Map;

public class Context {

    private static ThreadLocal<Map<String,String>> item = new InheritableThreadLocal<>();

    public static void init(){
        item.set(new HashMap<>());
    }

    public static void clear(){
        item.remove();
    }

    public static void set(String key,String value){
        item.get().put(key,value);
    }

    public static String get(String key){
        return item.get().get(key);
    }

    public static Map<String,String> getAll(){
        return item.get();
    }
}
