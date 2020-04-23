package org.springcat.dragonli.core.rpc;

import lombok.Data;

@Data
public class RpcInfo {

    private String scanPackages;

    private String serializeImplClass;

    private String httpTransformImplClass;

    private String loadBalanceRuleImplClass;

    private String errorHandleImplClass = "org.springcat.dragonli.core.rpc.Resilience4jErrorHandle";

    private String serviceRegisterImplClass = "org.springcat.dragonli.core.rpc.ConsulServiceRegister";

}
