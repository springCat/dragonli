package org.springcat.dragonli.core.config;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.lang.Dict;
import cn.hutool.setting.Setting;
import cn.hutool.setting.SettingUtil;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import org.springcat.dragonli.core.registry.AppInfo;
import org.springcat.dragonli.core.consul.ConsulUtil;

import java.util.List;

/**
 * sysconf 用于系统启动前获取的参数,比如数据库配置等
 * userconf 用于普通的业务参数
 * 后续会把配置异步写入本地文件,遇到配置中心异常后,可以不影响业务运行
 */
public class ConfigUtil {

    private static TimedCache<String, String> userConfCache = CacheUtil.newTimedCache(1000);

    private static Dict dict;

    public static String getUserConf(String key) {
        return userConfCache.get(key, () -> {
            Response<GetValue> kvValue = ConsulUtil.client().getKVValue(key);
            if(kvValue == null || kvValue.getValue() == null){
                return null;
            }
            return kvValue.getValue().getDecodedValue();
        });
    }

    public static void fetchSysConf(AppInfo appInfo) {
        Dict tempDict = new Dict();
        Response<List<GetValue>> kvValues = ConsulUtil.client().getKVValues("/sysconf/" + appInfo.getName());
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
