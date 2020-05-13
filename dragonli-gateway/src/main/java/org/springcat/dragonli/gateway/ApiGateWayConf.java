package org.springcat.dragonli.gateway;

import lombok.Data;
import org.springcat.dragonli.util.config.IConfig;

@Data
public class ApiGateWayConf implements IConfig {

    private String configPathConsul = "exposeUrl";

    private String apiExposeUrlsFilepath = "apiGateway.setting";

    //具体的请求执行实现类,可以自定义
    private String httpTransformImplClass = "org.springcat.dragonli.core.rpc.ihandle.impl.HttpclientTransform";

    //具体的负载均衡策略现类,可以自定义
    private String loadBalanceRuleImplClass = "org.springcat.dragonli.core.rpc.ihandle.impl.ConsistentHashRule";

    //具体的请求异常和熔断处理实现类,可以自定义
    private String errorHandleImplClass = "org.springcat.dragonli.core.rpc.ihandle.impl.Resilience4jErrorHandle";

    //具体的服务地址列表获取实现类,可以自定义
    private String serviceRegisterImplClass = "org.springcat.dragonli.core.rpc.ihandle.impl.ConsulServiceProvider";

    private String healthCheckUrl = "/status";
}
