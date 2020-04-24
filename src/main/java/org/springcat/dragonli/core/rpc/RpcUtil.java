package org.springcat.dragonli.core.rpc;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import org.springcat.dragonli.core.Proxy;
import org.springcat.dragonli.core.rpc.exception.RpcException;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class RpcUtil {

    /**
     * 1 先判断client-ip是否已经存在,用于消费者已经获取客户端IP,传递到生产者的场景
     * 2 不存在client-ip,就获取x-forwarded-for的值,根据http协议从反向代理服务器来的请求赋值到这个值
     * 3 不存在的话,就直接获取请求发起端的ip,用于服务没有前置的反向代理,直接面对用户的场景
     * @param request
     * @return
     */
    public static String getClientIp(HttpServletRequest request){
        String ip = request.getHeader("client-ip");
        if (StrUtil.isBlank(ip)) {
            ip = request.getHeader("x-forwarded-for");
        }
        if (StrUtil.isBlank(ip)) {
            ip =  request.getRemoteAddr();
        }
        return ip;
    }



}
