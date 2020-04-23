package org.springcat.dragonli.core.rpc.ihandle.impl;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 服务信息封装类,用于隔离consul的类注入到框架的代码中,属性和consul service保持一致
 */
@Data
public class RegisterServiceInfo {

    private String id;

    private String service;

    private List<String> tags;

    private String address;

    private Map<String, String> meta;

    private Integer port;
}
