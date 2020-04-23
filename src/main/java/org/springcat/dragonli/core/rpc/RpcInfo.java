package org.springcat.dragonli.core.rpc;

import lombok.Data;

@Data
public class RpcInfo {

    private String scanPackages;

    private String serializeImplClass = "org.springcat.dragonli.core.rpc.ihandle.impl.FastJsonSerialize";

    private String httpTransformImplClass = "org.springcat.dragonli.core.rpc.ihandle.impl.HttpclientTransform";

    private String loadBalanceRuleImplClass = "org.springcat.dragonli.core.rpc.ihandle.impl.ConsistentHashRule";

    private String errorHandleImplClass = "org.springcat.dragonli.core.rpc.ihandle.impl.Resilience4jErrorHandle";

    private String serviceRegisterImplClass = "org.springcat.dragonli.core.rpc.ihandle.impl.ConsulServiceRegister";

}
