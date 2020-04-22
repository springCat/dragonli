package org.springcat.dragonli.core.rpc;

import lombok.Data;

@Data
public class RpcInfo {

    private String scanPackages;

    private String serializeImplClass;

    private String httpTransformImplClass;

    private String loadBalanceRuleImplClass;

}
