package org.springcat.dragonli.util.consul;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.Setting;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.health.HealthServicesRequest;
import com.ecwid.consul.v1.health.model.HealthService;
import com.ecwid.consul.v1.kv.model.GetValue;
import lombok.Data;
import org.springcat.dragonli.util.registercenter.register.ApplicationConf;

import java.util.List;


@Data
public class Consul {

    private final static Log log = LogFactory.get();

    private final static String EMPTY = "";

    private ConsulClient client;

    public Consul connect(ConsulConf consulConf){
        client = new ConsulClient(consulConf.getIp(), consulConf.getPort());
        return this;
    }

    /**
     * consul中无值返回空的Setting
     * 异常情况返回null
     *
     * @param path
     * @return
     */
    public Setting getKVValues(String path){
        Setting setting = null;
        try {
            Response<List<GetValue>> resp = client.getKVValues(path);
            setting = new Setting();
            List<GetValue> values = resp.getValue();
            if(values == null){
                return setting;
            }
            for (GetValue value : values) {
                setting.put(value.getKey(),value.getDecodedValue());
            }
            return setting;
        }catch (Exception e){
            log.error("Consul getKVValues error:"+e.getMessage());
            return null;
        }
    }

    public String getKVValue(String path){
        try {
            Response<GetValue> resp = client.getKVValue(path);
            GetValue values = resp.getValue();
            if(values == null){
                return EMPTY;
            }
            return values.getDecodedValue();
        }catch (Exception e){
            log.error("Consul getKVValue error:{}",e.getMessage());
            return null;
        }
    }

    public boolean register(NewService service){
        try {
            client.agentServiceRegister(service);
            log.info("Consul register service:{}", service);
            return true;
        }catch (Exception e){
            log.error("Consul register error:{}",e.getMessage());
        }
        return false;
    }


    public boolean unRegister(ApplicationConf appConf){
        try {
            String serviceId = appConf.getServiceId();
            client.agentCheckDeregister("service:" + serviceId);
            client.agentServiceDeregister(serviceId);
            log.info("Consul unregister service id:{}", serviceId);
            return true;
        }catch (Exception e){
            log.error("Consul unRegister error:{}",e.getMessage());
        }
        return false;
    }

    public List<HealthService> getHealthServices(String serviceName,String[] labels){
        try {
            List<HealthService> value = client.getHealthServices(serviceName,
                    HealthServicesRequest.newBuilder().setTags(labels).build()).getValue();
            return value;
        }catch (Exception e){
            log.error("Consul getHealthServices name:{},label:{},error:{}",serviceName,labels,e.getMessage());
        }
        return null;
    }

}
