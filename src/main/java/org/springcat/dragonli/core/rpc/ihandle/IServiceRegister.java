package org.springcat.dragonli.core.rpc.ihandle;

import org.springcat.dragonli.core.rpc.exception.RpcException;
import org.springcat.dragonli.core.rpc.ihandle.impl.RegisterServiceInfo;
import org.springcat.dragonli.core.rpc.RpcRequest;

import java.util.List;

public interface IServiceRegister {

    List<RegisterServiceInfo> getServiceList(RpcRequest rpcRequest) throws RpcException;


}
