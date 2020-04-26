package org.springcat.dragonli.core.rpc;

import lombok.Data;
import org.springcat.dragonli.core.rpc.ihandle.IErrorHandle;

/**
 * 用于缓存方法基本的配置,需要包含rpc注解中的所有属性,以及熔断,错误处理,限流等等的设置
 */
@Data
public class RpcMethodInfo {

    private String appName;

    private String rootPath;

    private String controllerPath;

    private String methodName;

    private Class returnType;

    private String url;

    private String[] labels;

    private IErrorHandle iErrorHandle;
}
