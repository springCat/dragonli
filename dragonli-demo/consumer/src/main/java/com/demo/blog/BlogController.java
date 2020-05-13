package com.demo.blog;

import cn.hutool.core.map.MapUtil;
import cn.hutool.system.SystemUtil;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import org.springcat.dragonli.jfinal.controller.JsonController;

public class BlogController extends JsonController {

	@Inject
	private BlogService blogService;


	@Before(BlogPara.class)
	public void testRpc(){
		BlogPara blogPara = getJsonBean(BlogPara.class);
		BlogPara resp = blogService.json(blogPara,MapUtil.of("x-uid","xxxx"),null);
		renderJson(resp);
	}


	public void env(){
		String java_home = SystemUtil.get("JAVA_HOME");
		renderText(java_home);
	}
}


