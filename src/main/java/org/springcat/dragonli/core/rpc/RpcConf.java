package org.springcat.dragonli.core.rpc;

import lombok.Data;

@Data
public class RpcConf {
    //扫描该包名下rpc注解标注的类的
    private String scanPackages;
    //具体的验证实现类名,可以自定义
    private String validationImplClass = "org.springcat.dragonli.core.rpc.ihandle.impl.Jsr303Validation";
    //具体的序列化实现类,可以自定义
    private String serializeImplClass = "org.springcat.dragonli.core.rpc.ihandle.impl.FastJsonSerialize";
    //具体的请求执行实现类,可以自定义
    private String httpTransformImplClass = "org.springcat.dragonli.core.rpc.ihandle.impl.HttpclientTransform";
    //具体的负载均衡策略现类,可以自定义
    private String loadBalanceRuleImplClass = "org.springcat.dragonli.core.rpc.ihandle.impl.ConsistentHashRule";
    //具体的请求异常和熔断处理实现类,可以自定义
    private String errorHandleImplClass = "org.springcat.dragonli.core.rpc.ihandle.impl.Resilience4jErrorHandle";
    //具体的服务地址列表获取实现类,可以自定义
    private String serviceRegisterImplClass = "org.springcat.dragonli.core.rpc.ihandle.impl.ConsulServiceRegister";

}
