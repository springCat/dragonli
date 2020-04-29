package com.demo.blog;


import org.springcat.dragonli.core.rpc.Rpc;

import java.util.Map;
import java.util.function.Supplier;


@Rpc(appName = "producer")
public interface BlogService {

    /**
     * supports:
     *
     * json(BlogPara blogPara)
     *
     * json(BlogPara blogPara, Map<String,Object> header)
     *
     * json(BlogPara blogPara, Map<String,Object> header,Supplier recover)
     *
     * @param blogPara
     * @param header
     * @return
     */
    BlogPara json(BlogPara blogPara, Map<String, Object> header, Supplier recover);


}
