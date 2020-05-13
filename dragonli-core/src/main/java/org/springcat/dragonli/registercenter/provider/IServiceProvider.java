package org.springcat.dragonli.registercenter.provider;

import java.util.List;
import java.util.Map;

public interface IServiceProvider {

    void init(Map<String, String[]> appRouteMap);

    List<RegisterServiceInfo> getServiceList(String appName, String[] label);
}
