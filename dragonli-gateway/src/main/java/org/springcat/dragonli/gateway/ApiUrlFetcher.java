package org.springcat.dragonli.gateway;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.Setting;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import org.springcat.dragonli.consul.ConsulUtil;
import org.springcat.dragonli.util.configcenter.ConfigCenter;
import org.springcat.dragonli.util.consul.Consul;
import org.springcat.dragonli.util.registercenter.provider.ServiceProvider;

import java.util.List;
import java.util.Optional;

/**
 * 代用后端的服务采用配置的方式,动态刷新,因为apigateway作为前置要保持独立,频繁重启会丢失请求,影响体验
 */
public class ApiUrlFetcher {

    private final static Log log = LogFactory.get();


    public static Setting refreshApiExposeUrls(ApiGateWayConf apiGateWayConf){
        Setting setting = new Setting();
        if(StrUtil.isNotBlank(apiGateWayConf.getConfigPathConsul())){
            setting = refreshApiExposeUrlsFormConsul(apiGateWayConf);
        }
        if(setting.isEmpty()){
            setting = refreshApiExposeUrlsFormFile(apiGateWayConf);
        }
        return setting;
    }

    public static Setting refreshApiExposeUrlsFormConsul(ConfigCenter configCenter, ApiGateWayConf apiGateWayConf){

        Response<List<GetValue>> resp = ConsulUtil.client().getKVValues(apiGateWayConf.getConfigPathConsul());
        List<GetValue> values = resp.getValue();
        Optional.ofNullable(values).ifPresent(list -> {
                    //刷新期间不影响业务运行
                    for (GetValue value : list) {
                        handleValue(apiGateWayConf,value,setting);
                    }
                }
        );
        log.info("refreshApiExposeUrlsFormConsul apiGateWayConf:{}",apiGateWayConf);
        log.info("refreshApiExposeUrlsFormConsul apiExposeUrls:{}",setting);
        return setting;
    }

    private static void handleValue(ApiGateWayConf apiGateWayConf,GetValue value,Setting setting){
        int len = apiGateWayConf.getConfigPathConsul().length();
        //去掉前缀
        String key = value.getKey().substring(len+1);
        if(StrUtil.isNotBlank(key)) {
            String applicationName = StrUtil.subBefore(key, "/", false);
            String url = key.substring(applicationName.length());
            setting.put(applicationName, url,value.getDecodedValue());
        }
    }

    public static Setting refreshApiExposeUrlsFormFile(ApiGateWayConf apiGateWayConf){
        Setting setting = new Setting(apiGateWayConf.getApiExposeUrlsFilepath());
        log.info("refreshApiExposeUrlsFormFile apiGateWayConf:{}",apiGateWayConf);
        log.info("refreshApiExposeUrlsFormFile apiExposeUrls:{}",setting);
        return setting;

    }

}
