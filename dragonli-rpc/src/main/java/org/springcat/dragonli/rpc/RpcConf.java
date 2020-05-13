package org.springcat.dragonli.rpc;

import lombok.Data;
import org.springcat.dragonli.config.IConfig;

@Data
public class RpcConf implements IConfig {
    //扫描该包名下rpc注解标注的类的
    private String scanPackages;

    //rpc注解标注的类的后缀
    private String rpcServiceClassNameSuffix = "Service";

    //客户端参数校验是否开启
    private int clientValidateOpen = 1;

    //具体的验证实现类名,可以自定义
    private String validationImplClass = "org.springcat.org.springcat.dragonli.core.rpc.ihandle.impl.Jsr303Validation";

    //具体的序列化实现类,可以自定义
    private String serializeImplClass = "org.springcat.org.springcat.dragonli.core.rpc.ihandle.impl.FastJsonSerialize";

    //具体的请求执行实现类,可以自定义
    private String httpTransformImplClass = "org.springcat.org.springcat.dragonli.core.rpc.ihandle.impl.HttpclientTransform";

    //具体的负载均衡策略现类,可以自定义
    private String loadBalanceRuleImplClass = "org.springcat.org.springcat.dragonli.core.rpc.ihandle.impl.ConsistentHashRule";

    //具体的请求异常和熔断处理实现类,可以自定义
    private String errorHandleImplClass = "org.springcat.org.springcat.dragonli.core.rpc.ihandle.impl.Resilience4jErrorHandle";

    //具体的服务地址列表获取实现类,可以自定义 todo
    private String serviceProviderImplClass = "org.springcat.org.springcat.dragonli.jfinal.ConsulServiceRegister";

    private String loadBalanceKeyName;

}
