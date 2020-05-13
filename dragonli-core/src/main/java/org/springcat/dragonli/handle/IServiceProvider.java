package org.springcat.dragonli.handle;

import org.springcat.dragonli.registercenter.provider.RegisterServiceInfo;

import java.util.List;
import java.util.Map;

public interface IServiceProvider {

    void init(Map<String, String[]> appRouteMap);

    List<RegisterServiceInfo> getServiceList(String appName, String[] label);
}
