package org.springcat.dragonli.rpc.ihandle;

import org.springcat.dragonli.exception.RpcException;
import org.springcat.dragonli.rpc.RpcRequest;

/**
 * 个位数异常码先规划为系统用
 */
public interface IValidation {

     void validate(RpcRequest rpcRequest) throws RpcException;

}
