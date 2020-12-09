package com.demo.blog;


import org.springcat.dragonli.rpc.Rpc;


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
     * @return
     */
    BlogPara json(BlogPara blogPara);


}
