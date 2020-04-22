package org.springcat.dragonli.core.rpc;

import cn.hutool.core.util.StrUtil;
import com.ecwid.consul.v1.health.model.HealthService;

public interface IHttpTransform {

      Object post(RpcRequest rpcRequest, HealthService healthService);


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
