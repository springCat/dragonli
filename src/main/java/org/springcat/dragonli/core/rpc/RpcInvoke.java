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
import java.util.function.Function;
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
        IErrorHandle errorHandle = rpcRequest.getRpcMethodInfo().getIErrorHandle();

        //1 客户端校验参数,从rpcSupplier中分离出,因为客户端参数校验必须在开发阶段就处理掉,不需要重试,熔断和错误处理
        if(rpcConf.getClientValidateOpen() == 1) {
            validation.validate(rpcRequest);
        }

        Supplier<RpcResponse> rpcSupplier = () -> {
            RpcResponse rpcResponse = null;
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

        Function<? super Throwable, ? extends RpcResponse> errorHandler = throwable -> {
            //重试返回
            if (rpcRequest.getRecover() != null) {
                return rpcRequest.recoverResult();
            }
            //系统异常码处理
            if (throwable instanceof RpcException) {
                return RpcUtil.buildRpcResponse(throwable.getMessage(), returnType);
            }
            //熔断
            if (throwable instanceof CallNotPermittedException) {
                log.error("CallNotPermittedException error class:{},method:{}", rpcRequest.getClass(), rpcRequest.getMethod());
                return RpcUtil.buildRpcResponse(RpcExceptionCodes.ERR_FUSING.getCode(), returnType);
            }
            //兜底,不知道哪里出来的异常码
            log.error("invoke error request:{},error:{}", rpcRequest, throwable);
            return RpcUtil.buildRpcResponse(RpcExceptionCodes.ERR_OTHER.getCode(), returnType);
        };

        return errorHandle.execute(rpcRequest, rpcSupplier, errorHandler);
    }



}
