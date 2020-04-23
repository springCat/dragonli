package org.springcat.dragonli.core.rpc.ihandle;

import cn.hutool.core.util.StrUtil;
import org.springcat.dragonli.core.rpc.exception.TransformException;
import org.springcat.dragonli.core.rpc.ihandle.impl.RegisterServiceInfo;
import org.springcat.dragonli.core.rpc.RpcRequest;
import java.util.Map;


public interface IHttpTransform {

      String post(String url, Map<String, String> headers, String request) throws TransformException;

      default String genUrl(RpcRequest rpcRequest, RegisterServiceInfo registerServiceInfo) {
            String url = new StringBuilder("http://")
                    .append(registerServiceInfo.getAddress())
                    .append(":")
                    .append(registerServiceInfo.getPort())
                    .append("/")
                    .append(StrUtil.lowerFirst(StrUtil.strip(rpcRequest.getClassName(),"Service")))
                    .append("/")
                    .append(rpcRequest.getMethodName()).toString();
            return url;
      }

      default void errorHandler(TransformException TransformException){}

}
