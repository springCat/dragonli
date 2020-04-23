package org.springcat.dragonli.core.rpc;
import org.springcat.dragonli.core.registry.RegisterServerInfo;

import java.util.List;

public interface ILoadBalanceRule {

    RegisterServerInfo choose(List<RegisterServerInfo> serviceList, RpcRequest rpcRequest);
}
