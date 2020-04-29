package org.springcat.dragonli.core.rpc.ihandle;

import cn.hutool.core.util.StrUtil;
import org.springcat.dragonli.core.rpc.RpcMethodInfo;
import org.springcat.dragonli.core.rpc.RpcRequest;
import org.springcat.dragonli.core.rpc.exception.RpcException;
import org.springcat.dragonli.core.rpc.ihandle.impl.RegisterServiceInfo;

import java.util.Map;


public interface IHttpTransform {


      String post(String url, Map<String, String> headers, String requestBody) throws RpcException;


      default String post(RpcRequest rpcRequest, RegisterServiceInfo choose, String requestBody) throws RpcException {
            String url = genUrl(rpcRequest, choose);
            return post(url, rpcRequest.getRpcHeader(), requestBody);
      }

      default boolean isSuccess(int httpCode){
            return httpCode >= 200 && httpCode < 300;
      }

      default String genUrl(RpcRequest rpcRequest, RegisterServiceInfo registerServiceInfo) {
            RpcMethodInfo rpcMethodInfo = rpcRequest.getRpcMethodInfo();

            StringBuilder urlBuilder  = new StringBuilder("http://")
                    .append(registerServiceInfo.getAddress())
                    .append(":")
                    .append(registerServiceInfo.getPort())
                    .append("/");

            //配置了url的,url优先策略
            String url = rpcRequest.getRpcMethodInfo().getUrl();
            if(StrUtil.isNotBlank(url)){
                  return urlBuilder.append(url).toString();

            }

            //处理根路径
            if(StrUtil.isNotBlank(rpcMethodInfo.getRootPath())) {
                  urlBuilder = urlBuilder
                          .append(rpcMethodInfo.getRootPath())
                          .append("/");
            }
            return urlBuilder.append(rpcMethodInfo.getControllerPath())
                    .append("/")
                    .append(rpcMethodInfo.getMethodName()).toString();
      }

}
