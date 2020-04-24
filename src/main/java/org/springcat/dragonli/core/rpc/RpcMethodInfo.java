package org.springcat.dragonli.core.rpc;

import lombok.Data;
import org.springcat.dragonli.core.rpc.ihandle.IErrorHandle;

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
