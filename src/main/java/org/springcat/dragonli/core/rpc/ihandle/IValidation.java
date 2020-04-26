package org.springcat.dragonli.core.rpc.ihandle;

import cn.hutool.core.util.StrUtil;
import org.springcat.dragonli.core.rpc.RpcRequest;
import org.springcat.dragonli.core.rpc.RpcResponse;
import org.springcat.dragonli.core.rpc.RpcUtil;
import org.springcat.dragonli.core.rpc.exception.RpcException;

import java.util.Optional;

/**
 * 个位数异常码先规划为系统用
 */
public interface IValidation {

     void validate(RpcRequest rpcRequest) throws RpcException;

     default Optional<RpcResponse> validateWithRpcResponse(RpcRequest rpcRequest) throws RpcException{
          String code = null;
          try {
               validate(rpcRequest);
          }catch (RpcException exception){
               code = exception.getMessage();
          }

          if(StrUtil.isBlank(code)){
               return Optional.empty();
          }
          return RpcUtil.buildRpcResponse(code,rpcRequest.getRpcMethodInfo().getReturnType());
     }

}
