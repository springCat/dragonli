package com.demo.blog;

import com.jfinal.aop.Before;
import org.springcat.dragonli.core.jfinal.controller.JsonController;

public class BlogController extends JsonController {

	@Before(BlogPara.class)
	public void json() {
		BlogPara jsonBean = getJsonBean(BlogPara.class);
		jsonBean.setContent(getHeader("x-uid"));
		System.out.println("jsonBean invoke jsonBean:"+jsonBean);
		renderJson(jsonBean);
	}


}


