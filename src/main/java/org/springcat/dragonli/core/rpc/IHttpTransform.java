package org.springcat.dragonli.core.rpc;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import com.ecwid.consul.v1.health.model.HealthService;

import java.util.Map;


public interface IHttpTransform {


      String post(String url, Map<String, String> headers, String request);

      default Object post(RpcRequest rpcRequest, HealthService healthService){
            String url = genUrl(rpcRequest, healthService);
            Map<String, String> headers = rpcRequest.getRpcHeader();
            ISerialize serialize = rpcRequest.getSerialize();
            String request = serialize.encode(rpcRequest.getRequestObj());
            String response = post(url, headers, request);
            try {
                  if(StrUtil.isNotBlank(response)){
                        return serialize.decode(response, rpcRequest.getReturnType());
                  }
            }catch (Exception exception){
                  Log.get().error(exception);
            }
            return null;
      }


      default String genUrl(RpcRequest rpcRequest, HealthService healthService) {
            String url = new StringBuilder("http://")
                    .append(healthService.getService().getAddress())
                    .append(":")
                    .append(healthService.getService().getPort())
                    .append("/")
                    .append(StrUtil.lowerFirst(StrUtil.strip(rpcRequest.getClassName(),"Service")))
                    .append("/")
                    .append(rpcRequest.getMethodName()).toString();
            return url;
      }

}
