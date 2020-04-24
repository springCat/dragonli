package org.springcat.dragonli.core.rpc;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springcat.dragonli.core.rpc.exception.RpcException;
import org.springcat.dragonli.core.rpc.ihandle.impl.RegisterServiceInfo;
import org.springcat.dragonli.core.rpc.ihandle.*;
import java.lang.reflect.Method;
import java.util.HashMap;
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

    private static IServiceRegister serviceRegister;

    private static IValidation validation;

    private static Map<Method,RpcMethodInfo> apiMap = new HashMap<>();

    public static void init(RpcConf rpcConfPara, Consumer<Map<Class<?>, Object>> consumer) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        rpcConf = rpcConfPara;
        //初始化负载均衡
        loadBalanceRule = (ILoadBalanceRule) Class.forName(rpcConf.getLoadBalanceRuleImplClass()).newInstance();
        //初始化序列化
        serialize = (ISerialize) Class.forName(rpcConf.getSerializeImplClass()).newInstance();
        //初始化http请求客户端
        httpTransform = (IHttpTransform) Class.forName(rpcConf.getHttpTransformImplClass()).newInstance();

        //初始化服务列表获取
        serviceRegister = (IServiceRegister) Class.forName(rpcConf.getServiceRegisterImplClass()).newInstance();
        //初始化验证
        validation = (IValidation) Class.forName(rpcConf.getValidationImplClass()).newInstance();

        //初始化接口代理类
        List<Class<?>> services = RpcStarter.scanRpcService(rpcConf.getScanPackages());

        //初始化接口实现类
        Map<Class<?>, Object> implMap = RpcStarter.convert2RpcServiceImpl(services);
        consumer.accept(implMap);

        for (Class<?> service : services) {
            Method[] declaredMethods = service.getDeclaredMethods();

            String className = service.getSimpleName();
            className = StrUtil.strip(className,rpcConf.getRpcServiceClassNameSuffix());
            className = StrUtil.lowerFirst(className);

            for (Method method : declaredMethods) {
                RpcMethodInfo rpcMethodInfo = new RpcMethodInfo();
                Map<String, Object> annotationValueMap = AnnotationUtil.getAnnotationValueMap(service, Rpc.class);
                BeanUtil.fillBeanWithMapIgnoreCase(annotationValueMap,rpcMethodInfo,false);
                rpcMethodInfo.setControllerPath(className);
                rpcMethodInfo.setMethodName(method.getName());
                rpcMethodInfo.setReturnType(method.getReturnType());
                //初始化方法级别的错误处理
                IErrorHandle errorHandle = (IErrorHandle) Class.forName(rpcConf.getErrorHandleImplClass()).newInstance();
                errorHandle.init(method.toString());
                rpcMethodInfo.setIErrorHandle(errorHandle);

                apiMap.put(method,rpcMethodInfo);
            }
        }
    }

    /**
     *
     *  method -> buildRpcRequest -> serialize  -> loaderBalance  -> transform  -> deserialize -> return
     *                                                                 | |
     *                                                              errorHandle
     * @param rpcRequest
     * @return
     * @throws RpcException
     */
    public static RpcResponse invoke(RpcRequest rpcRequest) throws RpcException{

        RpcMethodInfo rpcMethodInfo = getApiMap().get(rpcRequest.getMethod());
        rpcRequest.setRpcMethodInfo(rpcMethodInfo);

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

        //5 decorate error handle
        String url = httpTransform.genUrl(rpcRequest,choose);
        Supplier<String> transformSupplier = () ->{
            try {
                return httpTransform.post(url, rpcRequest.getRpcHeader(), body);
            }catch(RpcException e){
                    //todo
            }
            return null;
        };

        //错误处理装饰类
        Supplier<String> supplier = rpcRequest.getRpcMethodInfo().getIErrorHandle().transformErrorHandle(transformSupplier, rpcRequest, choose);

        //6 Transform invoke
        String resp = supplier.get();

        //7 serialize decode
        if(StrUtil.isNotBlank(resp)){
            return serialize.decode(resp, rpcRequest.getRpcMethodInfo().getReturnType());
        }
        return null;
    }

    public static Map<Method, RpcMethodInfo> getApiMap() {
        return apiMap;
    }
}
