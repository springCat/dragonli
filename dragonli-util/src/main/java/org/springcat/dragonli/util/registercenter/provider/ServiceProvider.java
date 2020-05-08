package org.springcat.dragonli.util.registercenter.provider;

import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.LRUCache;
import cn.hutool.cache.impl.NoCache;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ecwid.consul.v1.health.model.HealthService;
import org.springcat.dragonli.util.consul.Consul;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description ServiceConsumer
 * @Author springCat
 * @Date 2020/5/6 15:16
 */
public class ServiceProvider {

    private final static Log log = LogFactory.get();

    private Consul consul;

    private ScheduledExecutorService schedule;


    public ServiceProvider(Consul consul, ServiceProviderConf serviceProviderConf, Map<String,String[]> appRouteMap) {
        this.consul = consul;
        initServiceWatcher(serviceProviderConf,appRouteMap);

    }

    private Cache<String,List<RegisterServiceInfo>> serviceCache = new NoCache<String,List<RegisterServiceInfo>>();

    private void initServiceWatcher(ServiceProviderConf serviceConsumerConf, Map<String,String[]> appRouteMap){

        Integer scanServicePeriod = serviceConsumerConf.getServiceScanPeriod();

        if(scanServicePeriod == null || scanServicePeriod < 0 || CollectionUtil.isEmpty(appRouteMap) ) {
            return;
        }


        schedule = Executors.newScheduledThreadPool(serviceConsumerConf.getServiceFetcherNum(), ThreadUtil.newNamedThreadFactory("ServiceConsumer",false));

        serviceCache = new LRUCache<String,List<RegisterServiceInfo>>(4096);

        for (Map.Entry<String, String[]> entry : appRouteMap.entrySet()) {
            schedule.scheduleAtFixedRate(() -> {
                List<RegisterServiceInfo> serviceList = getServiceListNoCache(entry.getKey(), entry.getValue());
                if(serviceList != null){
                    serviceCache.put(entry.getKey(),serviceList);
                }
            }, 0, scanServicePeriod, TimeUnit.SECONDS);
        }
    }

    public List<RegisterServiceInfo> getServiceListFormCache(String appName, String[] labels){
        return serviceCache.get(appName,() -> getServiceListNoCache(appName, labels));
    }

    public List<RegisterServiceInfo> getServiceListNoCache(String appName, String[] labels){
        List<RegisterServiceInfo> list = new ArrayList<>();

        List<HealthService> healthServiceList = consul.getHealthServices(appName, labels);
        //异常情况返回null
        if(healthServiceList == null){
            log.error("RegisterCenter getServiceList no service find appName:{},labels:{}" ,appName,labels);
            return null;
        }

        for (HealthService healthService : healthServiceList) {
            HealthService.Service service = healthService.getService();
            RegisterServiceInfo registerServiceInfo = new RegisterServiceInfo();
            BeanUtil.copyProperties(service, registerServiceInfo);
            list.add(registerServiceInfo);
        }
        return list;
    }
}
