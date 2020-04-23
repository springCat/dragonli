package org.springcat.dragonli.core.rpc;


import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springcat.dragonli.core.rpc.exception.RpcException;
import org.springcat.dragonli.core.rpc.ihandle.impl.RegisterServerInfo;
import org.springcat.dragonli.core.rpc.ihandle.*;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class RpcInvoke {

    private static final Log log = LogFactory.get(RpcInvoke.class);

    private static ILoadBalanceRule loadBalanceRule;
    private static ISerialize serialize;
    private static IHttpTransform httpTransform;
    private static RpcInfo rpcInfo;
    private static IErrorHandle errorHandle;
    private static IServiceRegister serviceRegister;
    private static IValidation validation;

    public static void init(RpcInfo rpcInfoPara,Consumer<Map<Class<?>, Object>> consumer) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        rpcInfo = rpcInfoPara;
        //初始化负载均衡
        loadBalanceRule = (ILoadBalanceRule) Class.forName(rpcInfo.getLoadBalanceRuleImplClass()).newInstance();
        //初始化序列化
        serialize = (ISerialize) Class.forName(rpcInfo.getSerializeImplClass()).newInstance();
        //初始化http请求客户端
        httpTransform = (IHttpTransform) Class.forName(rpcInfo.getHttpTransformImplClass()).newInstance();
        //初始化错误处理
        errorHandle = (IErrorHandle) Class.forName(rpcInfo.getErrorHandleImplClass()).newInstance();
        //初始化服务列表获取
        serviceRegister = (IServiceRegister) Class.forName(rpcInfo.getServiceRegisterImplClass()).newInstance();
        //初始化验证
        validation = (IValidation) Class.forName(rpcInfo.getIValidation()).newInstance();

        //初始化接口代理类
        List<Class<?>> services = RpcUtil.scanRpcService(rpcInfo.getScanPackages());

        //初始化
        Map<Class<?>, Object> implMap = RpcUtil.convert2RpcServiceImpl(services);
        consumer.accept(implMap);
    }

    /**
     *
     *
     *  method -> buildRpcRequest -> serialize  -> loaderBalance  -> transform  -> deserialize -> return
     *                                                                 |
     *                                                              errorHandle
     *
     * @param rpcRequest
     * @return
     * @throws RpcException
     */
    public static Object invoke(RpcRequest rpcRequest) throws RpcException{

        //1 校验参数
        validation.validate(rpcRequest.getRequestObj());

        //2 serviceGetter
        List<RegisterServerInfo> serviceList = serviceRegister.getServiceList(rpcRequest);

        //3 loaderBalance
        RegisterServerInfo choose = loadBalanceRule.choose(serviceList,rpcRequest);

        //4 serialize encode
        String body = serialize.encode(rpcRequest.getRequestObj());

        //5 Transform invoke
        String url = httpTransform.genUrl(rpcRequest,choose);
        String resp = httpTransform.post(url, rpcRequest.getRpcHeader(), body);

        //6 serialize decode
        if(StrUtil.isNotBlank(resp)){
            return serialize.decode(resp, rpcRequest.getReturnType());
        }

//        //4 errorHandle and httpTransform
//        try {
//            Supplier<Object> supplier = errorHandle.transformErrorHandle(transform,rpcRequest, choose);
//            return supplier.get();
//        } catch (Exception e) {
//            throw new RpcException(e.getMessage());
//        }
        return null;
    }


}
