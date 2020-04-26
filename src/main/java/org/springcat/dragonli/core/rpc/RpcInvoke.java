package org.springcat.dragonli.core.rpc;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.Data;
import lombok.SneakyThrows;
import org.springcat.dragonli.core.rpc.exception.RpcException;
import org.springcat.dragonli.core.rpc.exception.RpcExceptionCodes;
import org.springcat.dragonli.core.rpc.ihandle.*;
import org.springcat.dragonli.core.rpc.ihandle.impl.RegisterServiceInfo;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 整个框架的核心类
 */
@Data
public class RpcInvoke {

    private final static Log log = LogFactory.get();

    private RpcConf rpcConf;

    private ILoadBalanceRule loadBalanceRule;

    private ISerialize serialize;

    private IHttpTransform httpTransform;

    private IServiceRegister serviceRegister;

    private IValidation validation;

    private Map<Method,RpcMethodInfo> apiMap;


    /**
     *
     *  method -> buildRpcRequest -> serialize  -> loaderBalance  -> transform  -> deserialize -> return
     *                                                                 | |
     *                                                              errorHandle
     * @param rpcRequest
     * @return
     * @throws RpcException
     */
    @SneakyThrows
    public RpcResponse invoke(RpcRequest rpcRequest){
        rpcRequest.setRpcMethodInfo(apiMap.get(rpcRequest.getMethod()));
        Class returnType = rpcRequest.getRpcMethodInfo().getReturnType();

        Supplier<RpcResponse> rpcSupplier = () -> {
                RpcResponse rpcResponse = null;
                //1 校验参数,异常会中止流程
                validation.validate(rpcRequest);
                //2 serviceGetter
                List<RegisterServiceInfo> serviceList = serviceRegister.getServiceList(rpcRequest);
                //3 loaderBalance
                RegisterServiceInfo choose = loadBalanceRule.choose(serviceList, rpcRequest);
                //4 serialize encode
                String body = serialize.encode(rpcRequest.getRequestObj());
                //5 http invoke
                String resp = httpTransform.post(rpcRequest,choose, body);
                //6 serialize decode
                if (StrUtil.isNotBlank(resp)) {
                    rpcResponse = serialize.decode(resp, returnType);
                    rpcResponse.setCode(RpcExceptionCodes.SUCCESS.getCode());
                    return rpcResponse;
                }
                //for empty resp
                return RpcUtil.buildRpcResponse(RpcExceptionCodes.SUCCESS.getCode(), returnType);
        };


        IErrorHandle errorHandle = rpcRequest.getRpcMethodInfo().getIErrorHandle();

        RpcResponse execute = errorHandle.execute(rpcRequest, rpcSupplier, throwable -> {
            //重试返回
            if(rpcRequest.getRecover() != null){
                return rpcRequest.recoverResult();
            }

            if(throwable instanceof RpcException){
                RpcResponse rpcResponse = RpcUtil.buildRpcResponse(throwable.getMessage(), returnType);
                return rpcResponse;
            }

            if(throwable instanceof CallNotPermittedException){
                log.error("CallNotPermittedException error request:{}",rpcRequest);
                return RpcUtil.buildRpcResponse(RpcExceptionCodes.ERR_FUSING.getCode(), returnType);
            }

            log.error("invoke error request:{},error:{}",rpcRequest,throwable);
            return  RpcUtil.buildRpcResponse(RpcExceptionCodes.ERR_OTHER.getCode(), returnType);
        });

        return execute;
    }



}
