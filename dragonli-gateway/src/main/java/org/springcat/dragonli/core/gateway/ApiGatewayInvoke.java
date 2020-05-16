package org.springcat.dragonli.core.gateway;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.Data;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.springcat.dragonli.core.configcenter.ConfigCenter;
import org.springcat.dragonli.core.handle.IErrorHandle;
import org.springcat.dragonli.core.handle.ILoadBalanceRule;
import org.springcat.dragonli.core.exception.RpcException;
import org.springcat.dragonli.core.exception.RpcExceptionCodes;
import org.springcat.dragonli.core.registercenter.provider.IServiceProvider;
import org.springcat.dragonli.core.registercenter.provider.RegisterServiceInfo;

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

    private IServiceProvider serviceRegister;

    private ApiGatewayHttpClient httpClient;

    private Map<String, IErrorHandle> iErrorHandleMap = new ConcurrentHashMap<>();


    public static final String ERROR_HEADER_NAME = "x_err_code";

    public boolean invoke(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {

        String target = servletRequest.getRequestURI();
        String gatewayUri = StrUtil.strip(target,"/");
        String applicationName = StrUtil.subBefore(gatewayUri, "/", false);

        if (StrUtil.isEmpty(applicationName)) {
            servletResponse.setStatus(404);
            return false;
        }

        String labels = ConfigCenter.getRouteConf().getValue(target);
        if (StrUtil.isEmpty(labels)) {
            servletResponse.setStatus(404);
            return false;
        }

        //build  rpcRequest
        Supplier<Boolean> rpcSupplier = () -> {
            //2 serviceGetter
            List<RegisterServiceInfo> serviceList = serviceRegister.getServiceList(applicationName,labels.split(","));

            //3 loaderBalance,  todo loaderBalanceFlag暂时先写死,后续优化
            RegisterServiceInfo choose = loadBalanceRule.choose(serviceList, IdUtil.fastUUID());

            ApiGatewayHttpClient httpClient = new ApiGatewayHttpClient();

            //构造gateway 向后端的请求
            String path = gatewayUri.substring(applicationName.length());
            String url = "http://" + choose.getAddress() + ":" + choose.getPort()  + path;
            httpClient.proxyPost(url,servletRequest,servletResponse);

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
        //todo
        IErrorHandle errorHandle = iErrorHandleMap.get(target);
        return errorHandle.execute(rpcSupplier, errorHandler);

    }


    private void handleRequestHeader(HttpServletRequest servletRequest, HttpRequest request) {

    }

    private void handleResponseHeader(HttpServletResponse servletResponse,HttpResponse httpResponse) {

    }

}
