package org.springcat.dragonli.gateway;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.Setting;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.Data;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.springcat.dragonli.core.rpc.RpcMethodInfo;
import org.springcat.dragonli.core.rpc.RpcRequest;
import org.springcat.dragonli.core.rpc.RpcResponse;
import org.springcat.dragonli.core.rpc.exception.RpcException;
import org.springcat.dragonli.core.rpc.exception.RpcExceptionCodes;
import org.springcat.dragonli.core.rpc.ihandle.IErrorHandle;
import org.springcat.dragonli.core.rpc.ihandle.IHttpTransform;
import org.springcat.dragonli.core.rpc.ihandle.ILoadBalanceRule;
import org.springcat.dragonli.core.rpc.ihandle.IServiceProvider;
import org.springcat.dragonli.core.rpc.ihandle.impl.HttpclientTransform;
import org.springcat.dragonli.util.registercenter.provider.RegisterServiceInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

@Data
public class ApiGatewayInvoke {

    private final static Log log = LogFactory.get();

    private ApiGateWayConf apiGateWayConf;

    private ILoadBalanceRule loadBalanceRule;

    private IHttpTransform httpTransform;

    private IServiceProvider serviceRegister;

    private Setting apiExposeUrls;

    private Map<String, IErrorHandle> iErrorHandleMap = new ConcurrentHashMap<>();


    public static final String ERROR_HEADER_NAME = "x_err_code";

    public boolean invokePost(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        String target = servletRequest.getRequestURI();

        String gatewayUri = StrUtil.strip(target,"/");
        String applicationName = StrUtil.subBefore(gatewayUri, "/", false);
        if (StrUtil.isEmpty(applicationName)) {
            servletResponse.setStatus(404);
            return false;
        }

        String path = gatewayUri.substring(applicationName.length());
        String labels = apiExposeUrls.get(applicationName, path);
        if (StrUtil.isEmpty(labels)) {
            servletResponse.setStatus(404);
            return false;
        }

        //build  rpcRequest
        Supplier<Boolean> rpcSupplier = () -> {
            RpcResponse rpcResponse = null;
            //2 serviceGetter
            List<RegisterServiceInfo> serviceList = serviceRegister.getServiceList(applicationName,labels.split(","));
            //3 loaderBalance, 暂时先写死,后续优化
            RegisterServiceInfo choose = loadBalanceRule.choose(serviceList, IdUtil.fastUUID());

            HttpclientTransform httpTransform = (HttpclientTransform) this.httpTransform;

            //handle request
            //直接copy body stream,减少内存消耗和解析的性能开销
            try {
                String uri = "http://" + choose.getAddress() + ":" + choose.getPort()  + path;
                HttpPost httpPost = new HttpPost(uri);

                httpPost.setEntity(new InputStreamEntity(servletRequest.getInputStream()));
                //处理请求头部
                handleRequestHeader(servletRequest,httpPost);
                //invoke
                HttpResponse httpResponse = httpTransform.getHttpClient().execute(httpPost);
                //handle response
                //处理返回头部
                handleResponseHeader(servletResponse,httpResponse);
                //直接copy body stream,减少内存消耗和解析的性能开销
                IoUtil.copy(httpResponse.getEntity().getContent(), servletResponse.getOutputStream());
            } catch (IOException e) {
                throw new RpcException(RpcExceptionCodes.ERR_OTHER.getCode());
            }
            return true;
        };


        Function<? super Throwable, ? extends Boolean> errorHandler = throwable -> {
            //系统异常码处理
            if (throwable instanceof RpcException) {
                servletResponse.addHeader(ERROR_HEADER_NAME, throwable.getMessage());
                return false;
            }

            //熔断
            if (throwable instanceof CallNotPermittedException) {
                servletResponse.addHeader(ERROR_HEADER_NAME, RpcExceptionCodes.ERR_FUSING.getCode());
                return false;
            }
            //兜底,不知道哪里出来的异常码
            log.error("invoke error uri:{},error:{}", target, throwable);
            servletResponse.addHeader(ERROR_HEADER_NAME,  RpcExceptionCodes.ERR_OTHER.getCode());
            return false;
        };

        IErrorHandle errorHandle = iErrorHandleMap.get(gatewayUri);
        return errorHandle.execute(rpcSupplier, errorHandler);

    }


    private void handleRequestHeader(HttpServletRequest servletRequest, HttpRequest request) {

    }

    private void handleResponseHeader(HttpServletResponse servletResponse,HttpResponse httpResponse) {

    }

    private RpcRequest buildApiGatewayRequest(String applicationName, String[] label, String url) {
        RpcRequest rpcRequest = new RpcRequest();
        RpcMethodInfo rpcMethodInfo = new RpcMethodInfo();
        rpcMethodInfo.setAppName(applicationName);
        rpcMethodInfo.setLabels(label);
        rpcMethodInfo.setUrl(url);
        rpcMethodInfo.setLoadBalancerKeyName("x-uid");
        rpcRequest.setRpcMethodInfo(rpcMethodInfo);
        rpcRequest.setRpcHeader(MapUtil.of("x-uid","x-uid11"));
        return rpcRequest;
    }

}
