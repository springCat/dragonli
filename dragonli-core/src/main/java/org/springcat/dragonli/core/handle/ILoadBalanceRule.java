package org.springcat.dragonli.core.handle;

import org.springcat.dragonli.core.exception.RpcException;
import org.springcat.dragonli.core.registercenter.provider.RegisterServiceInfo;

import java.util.List;

public interface ILoadBalanceRule {

    RegisterServiceInfo choose(List<RegisterServiceInfo> serviceList, Object loaderBalanceFlag) throws RpcException;

}
