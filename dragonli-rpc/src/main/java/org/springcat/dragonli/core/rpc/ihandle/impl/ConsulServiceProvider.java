package org.springcat.dragonli.core.rpc.ihandle.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springcat.dragonli.core.rpc.exception.RpcException;
import org.springcat.dragonli.core.rpc.ihandle.IServiceProvider;
import org.springcat.dragonli.util.consul.Consul;
import org.springcat.dragonli.util.registercenter.provider.RegisterServiceInfo;
import org.springcat.dragonli.util.registercenter.provider.ServiceProvider;
import org.springcat.dragonli.util.registercenter.provider.ServiceProviderConf;

import java.util.List;
import java.util.Map;

/**
 * 默认为consul服务注册中心,因为consul agent是部署在本机的,暂时不加缓存,后续压测后再看
 */
public class ConsulServiceProvider implements IServiceProvider {

    private final static Log log = LogFactory.get();

    private ServiceProvider serviceConsumer;

    private ServiceProviderConf serviceProviderConf;

    private Consul consul;

    public void init(Consul consul) {
        serviceProviderConf = new ServiceProviderConf().load();
        this.consul = consul;
    }

    @Override
    public void init( Map<String,String[]> appRouteMap) {

        serviceConsumer = new ServiceProvider(consul,serviceProviderConf,appRouteMap);
    }

    /**
     *
     *
     * @return
     * @throws RpcException
     */
    public List<RegisterServiceInfo> getServiceList(String appName, String[] labels) throws RpcException {
        return serviceConsumer.getServiceListFormCache(appName, labels);
    }
}
