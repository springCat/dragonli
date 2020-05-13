package org.springcat.dragonli.handle;

import org.springcat.dragonli.exception.RpcException;
import org.springcat.dragonli.registercenter.provider.RegisterServiceInfo;

import java.util.List;

public interface ILoadBalanceRule {

    RegisterServiceInfo choose(List<RegisterServiceInfo> serviceList, Object loaderBalanceFlag) throws RpcException;

}
