package org.springcat.dragonli.core.rpc.ihandle;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import org.springcat.dragonli.core.rpc.exception.TransformException;
import org.springcat.dragonli.core.rpc.ihandle.impl.RegisterServerInfo;
import org.springcat.dragonli.core.rpc.RpcRequest;

import java.util.Map;

public interface IHttpTransform {


      String post(String url, Map<String, String> headers, String request) throws TransformException;


      default String genUrl(RpcRequest rpcRequest, RegisterServerInfo registerServerInfo) {
            String url = new StringBuilder("http://")
                    .append(registerServerInfo.getAddress())
                    .append(":")
                    .append(registerServerInfo.getPort())
                    .append("/")
                    .append(StrUtil.lowerFirst(StrUtil.strip(rpcRequest.getClassName(),"Service")))
                    .append("/")
                    .append(rpcRequest.getMethodName()).toString();
            return url;
      }

}
