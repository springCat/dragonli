package org.springcat.dragonli.core.rpc.ihandle;
import org.springcat.dragonli.core.rpc.exception.LoadBalanceException;
import org.springcat.dragonli.core.rpc.ihandle.impl.RegisterServerInfo;
import org.springcat.dragonli.core.rpc.RpcRequest;

import java.util.List;

public interface ILoadBalanceRule {

    RegisterServerInfo choose(List<RegisterServerInfo> serviceList, RpcRequest rpcRequest) throws LoadBalanceException;

    default void errorHandler(LoadBalanceException exception){

    }
}
