package org.springcat.dragonli.core.rpc.ihandle;

import org.springcat.dragonli.core.rpc.RpcRequest;
import org.springcat.dragonli.core.rpc.exception.RpcException;

/**
 * 个位数异常码先规划为系统用
 */
public interface IValidation {

     void validate(RpcRequest rpcRequest) throws RpcException;

}
