package org.springcat.dragonli.core.handle;

import org.springcat.dragonli.core.registercenter.provider.RegisterServiceInfo;

import java.util.List;
import java.util.Map;

public interface IServiceProvider {

    void init(Map<String, String[]> appRouteMap);

    List<RegisterServiceInfo> getServiceList(String appName, String[] label);
}
