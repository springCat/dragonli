package org.springcat.dragonli.core.rpc;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springcat.dragonli.core.rpc.exception.RpcException;
import org.springcat.dragonli.core.rpc.ihandle.impl.RegisterServiceInfo;
import org.springcat.dragonli.core.rpc.ihandle.*;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 整个框架的核心类
 */
public class RpcInvoke {

    private static final Log log = LogFactory.get(RpcInvoke.class);

    private static RpcConf rpcConf;

    private static ILoadBalanceRule loadBalanceRule;

    private static ISerialize serialize;

    private static IHttpTransform httpTransform;

    private static IErrorHandle errorHandle;

    private static IServiceRegister serviceRegister;

    private static IValidation validation;

    public static void init(RpcConf rpcConfPara, Consumer<Map<Class<?>, Object>> consumer) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        rpcConf = rpcConfPara;
        //初始化负载均衡
        loadBalanceRule = (ILoadBalanceRule) Class.forName(rpcConf.getLoadBalanceRuleImplClass()).newInstance();
        //初始化序列化
        serialize = (ISerialize) Class.forName(rpcConf.getSerializeImplClass()).newInstance();
        //初始化http请求客户端
        httpTransform = (IHttpTransform) Class.forName(rpcConf.getHttpTransformImplClass()).newInstance();
        //初始化错误处理
        errorHandle = (IErrorHandle) Class.forName(rpcConf.getErrorHandleImplClass()).newInstance();
        //初始化服务列表获取
        serviceRegister = (IServiceRegister) Class.forName(rpcConf.getServiceRegisterImplClass()).newInstance();
        //初始化验证
        validation = (IValidation) Class.forName(rpcConf.getValidationImplClass()).newInstance();

        //初始化接口代理类
        List<Class<?>> services = RpcUtil.scanRpcService(rpcConf.getScanPackages());

        //初始化接口实现类
        Map<Class<?>, Object> implMap = RpcUtil.convert2RpcServiceImpl(services);
        consumer.accept(implMap);

        RpcUtil.initRpcServicesHook(services);
    }

    /**
     *
     *
     *  method -> buildRpcRequest -> serialize  -> loaderBalance  -> transform  -> deserialize -> return
     *                                                                 | |
     *                                                              errorHandle
     *
     * @param rpcRequest
     * @return
     * @throws RpcException
     */
    public static RpcResponse invoke(RpcRequest rpcRequest) throws RpcException{

        //1 校验参数,异常会中止流程
        String code = validation.validate(rpcRequest.getRequestObj());
        if(StrUtil.isNotBlank(code)){
            return new RpcResponse(code);
        }

        //2 serviceGetter
        List<RegisterServiceInfo> serviceList = serviceRegister.getServiceList(rpcRequest);

        //3 loaderBalance
        RegisterServiceInfo choose = loadBalanceRule.choose(serviceList,rpcRequest);

        //4 serialize encode
        String body = serialize.encode(rpcRequest.getRequestObj());

        //5   decorate error handle
        String url = httpTransform.genUrl(rpcRequest,choose);
        Supplier<String> transformSupplier = () ->{
            try {
                return httpTransform.post(url, rpcRequest.getRpcHeader(), body);
            }catch(RpcException e){
                    //todo
            }
            return null;
        };
        Supplier<String> supplier = errorHandle.transformErrorHandle(transformSupplier, rpcRequest, choose);

        //6 Transform invoke
        String resp = supplier.get();

        //7 serialize decode
        if(StrUtil.isNotBlank(resp)){
            return serialize.decode(resp, rpcRequest.getReturnType());
        }
        return null;
    }

}
