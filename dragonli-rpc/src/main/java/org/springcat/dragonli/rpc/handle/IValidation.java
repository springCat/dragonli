package org.springcat.dragonli.rpc.handle;

import org.springcat.dragonli.rpc.RpcRequest;
import org.springcat.dragonli.core.exception.RpcException;

/**
 * 个位数异常码先规划为系统用
 */
public interface IValidation {

     void validate(RpcRequest rpcRequest) throws RpcException;

}
