package org.springcat.dragonli.core.rpc.ihandle;
import org.springcat.dragonli.core.rpc.exception.RpcException;
import org.springcat.dragonli.core.rpc.ihandle.impl.RegisterServiceInfo;
import org.springcat.dragonli.core.rpc.RpcRequest;

import java.util.List;

public interface ILoadBalanceRule {

    RegisterServiceInfo choose(List<RegisterServiceInfo> serviceList, RpcRequest rpcRequest) throws RpcException;

}
