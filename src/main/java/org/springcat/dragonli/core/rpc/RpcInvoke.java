package org.springcat.dragonli.core.rpc;


import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LFUCache;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import lombok.SneakyThrows;
import org.springcat.dragonli.core.rpc.exception.RpcException;
import org.springcat.dragonli.core.rpc.exception.TransformException;
import org.springcat.dragonli.core.rpc.ihandle.impl.RegisterServerInfo;
import org.springcat.dragonli.core.rpc.ihandle.*;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RpcInvoke {

    private static final Log log = LogFactory.get(RpcInvoke.class);

    private static ILoadBalanceRule loadBalanceRule;
    private static ISerialize serialize;
    private static IHttpTransform httpTransform;
    private static RpcConfInfo rpcConfInfo;
    private static IErrorHandle errorHandle;
    private static IServiceRegister serviceRegister;
    private static IValidation validation;

    private LFUCache<String, CircuitBreaker> circuitBreakerCache = CacheUtil.newLFUCache(10000);
    private LFUCache<String, Retry> retryCache = CacheUtil.newLFUCache(10000);


    public static void init(RpcConfInfo rpcConfInfoPara, Consumer<Map<Class<?>, Object>> consumer) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        rpcConfInfo = rpcConfInfoPara;
        //初始化负载均衡
        loadBalanceRule = (ILoadBalanceRule) Class.forName(rpcConfInfo.getLoadBalanceRuleImplClass()).newInstance();
        //初始化序列化
        serialize = (ISerialize) Class.forName(rpcConfInfo.getSerializeImplClass()).newInstance();
        //初始化http请求客户端
        httpTransform = (IHttpTransform) Class.forName(rpcConfInfo.getHttpTransformImplClass()).newInstance();
        //初始化错误处理
        errorHandle = (IErrorHandle) Class.forName(rpcConfInfo.getErrorHandleImplClass()).newInstance();
        //初始化服务列表获取
        serviceRegister = (IServiceRegister) Class.forName(rpcConfInfo.getServiceRegisterImplClass()).newInstance();
        //初始化验证
        validation = (IValidation) Class.forName(rpcConfInfo.getIValidation()).newInstance();

        //初始化接口代理类
        List<Class<?>> services = RpcUtil.scanRpcService(rpcConfInfo.getScanPackages());

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
    @SneakyThrows
    public static Object invoke(RpcRequest rpcRequest) throws RpcException{

        //1 校验参数
        validation.validate(rpcRequest.getRequestObj());

        //2 serviceGetter
        List<RegisterServerInfo> serviceList = serviceRegister.getServiceList(rpcRequest);

        //3 loaderBalance
        RegisterServerInfo choose = loadBalanceRule.choose(serviceList,rpcRequest);

        //4 serialize encode
        String body = serialize.encode(rpcRequest.getRequestObj());

        //5   decorate error handle
        String url = httpTransform.genUrl(rpcRequest,choose);
        Supplier<String> transformSupplier = () ->{
            try {
                return httpTransform.post(url, rpcRequest.getRpcHeader(), body);
            }catch(TransformException e){
                httpTransform.errorHandler(e);
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
