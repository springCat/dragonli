package org.springcat.dragonli.core.rpc;

import com.ecwid.consul.v1.health.model.HealthService;
import java.util.function.Supplier;

public interface IErrorHandle {
    Supplier<Object> transformErrorHandle(IHttpTransform httpTransform, RpcRequest rpcRequest, HealthService healthService);
}
