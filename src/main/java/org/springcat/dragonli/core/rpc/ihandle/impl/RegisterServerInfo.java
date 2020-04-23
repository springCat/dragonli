package org.springcat.dragonli.core.rpc.ihandle.impl;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class RegisterServerInfo {

    private String id;


    private String service;


    private List<String> tags;


    private String address;


    private Map<String, String> meta;


    private Integer port;
}
