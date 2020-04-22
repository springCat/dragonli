package org.springcat.dragonli.core.config;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.lang.Dict;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import org.springcat.dragonli.core.registry.AppInfo;
import org.springcat.dragonli.core.consul.ConsulUtil;

import java.util.List;

public class ConfigUtil {

    private static TimedCache<String, String> userConfCache = CacheUtil.newTimedCache(1000);

    private static Dict dict;

    public static String getUserConf(String key) {
        return userConfCache.get(key, () -> {
            Response<GetValue> kvValue = ConsulUtil.use().getKVValue(key);
            if(kvValue == null || kvValue.getValue() == null){
                return null;
            }
            return kvValue.getValue().getDecodedValue();
        });
    }

    public static void fetchSysConf(AppInfo appInfo) {
        Dict tempDict = new Dict();
        Response<List<GetValue>> kvValues = ConsulUtil.use().getKVValues("/sysconf/" + appInfo.getName());
        if(kvValues != null && kvValues.getValue() != null) {
            for (GetValue value : kvValues.getValue()) {
                tempDict.put(value.getKey(), value.getDecodedValue());
            }
        }
        dict = tempDict;
    }

    public static Dict getSysConf() {
        if(dict == null){
            throw new RuntimeException("InitConf should fetch from Consul first");
        }
      return dict;
    }
}
