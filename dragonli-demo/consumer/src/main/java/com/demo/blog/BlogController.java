package com.demo.blog;

import cn.hutool.system.SystemUtil;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import org.springcat.dragonli.core.jfinal.controller.JsonController;
import org.springcat.dragonli.rpc.Context;

public class BlogController extends JsonController {

	@Inject
	private BlogService blogService;


	@Before(BlogPara.class)
	public void testRpc(){
		BlogPara blogPara = getJsonBean(BlogPara.class);

		Context.setRpcParam("x-uid",getHeader("x-uid"));
		BlogPara resp = blogService.json(blogPara);
		renderJson(resp);
	}


	public void env(){
		String java_home = SystemUtil.get("JAVA_HOME");
		renderText(java_home);
	}
}


