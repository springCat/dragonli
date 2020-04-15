package org.springcat.dragonli.client;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.IdUtil;
import com.ecwid.consul.v1.health.HealthServicesRequest;
import com.ecwid.consul.v1.health.model.HealthService;
import org.springcat.dragonli.jfinal.JFinalHttpTransform;
import org.springcat.dragonli.loadbalance.ConsistentHashRule;
import org.springcat.dragonli.loadbalance.ILoadBalanceRule;
import org.springcat.dragonli.consul.ConsulUtil;
import org.springcat.dragonli.serialize.ISerialize;
import org.springcat.dragonli.jfinal.JFinalJsonSerialize;
import java.util.List;
import java.util.Map;

public class HttpInvoker {

    private static TimedCache<String, List<HealthService>> cache = CacheUtil.newTimedCache(1000);
    private static ILoadBalanceRule loadBalanceRule = new ConsistentHashRule();
    private static ISerialize serialize = new JFinalJsonSerialize();
    private static IHttpTransform httpTransform = new JFinalHttpTransform();

    public static Object invoke(RpcRequest request, Class<?> responseType) throws RpcException{
        try {
                String serviceName = request.getServiceName();
                Map<String,String> header = request.getHeader();

                List<HealthService> serviceList = cache.get(serviceName, () -> {
                    return ConsulUtil.use().getHealthServices(serviceName, HealthServicesRequest.newBuilder().build()).getValue();
                });

                HealthService choose = loadBalanceRule.choose(serviceList,header.getOrDefault("x-uid", IdUtil.fastSimpleUUID()).getBytes());
                if(choose == null){
                    throw new RpcException("can not find healthService");
                }

                String url = new StringBuilder("http://")
                        .append(choose.getService().getAddress())
                        .append(":")
                        .append(choose.getService().getPort())
                        .append("/")
                        .append(request.getClassName())
                        .append("/")
                        .append(request.getMethodName()).toString();

                String reqStr = null;

                if(request != null){
                    reqStr = serialize.encode(request.getBody());
                }

                String respStr = httpTransform.post(url, reqStr, header);

                if(respStr != null){
                    return serialize.decode(respStr,responseType);
                }
        }catch (Exception e){
            throw new RpcException(e.getMessage());
        }
        return null;
    }


}
