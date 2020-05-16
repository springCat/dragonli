package com.demo.common;

import com.demo.blog.BlogController;
import com.jfinal.config.*;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;
import org.springcat.dragonli.core.jfinal.DragonLiConfig;

/**
 * 本 demo 仅表达最为粗浅的 jfinal 用法，更为有价值的实用的企业级用法
 * 详见 JFinal 俱乐部: http://jfinal.com/club
 * 
 * API 引导式配置
 */
public class ConsumerConfig extends DragonLiConfig {


	/**
	 * 配置常量
	 */
	public void configConstantPlus(Constants me) {
		me.setDevMode(false);
	}

	/**
	 * 配置路由
	 */
	public void configRoutePlus(Routes me) {
		me.add("/blog", BlogController.class);
	}
	
	public void configEnginePlus(Engine me) {
	}
	
	/**
	 * 配置插件
	 */
	public void configPluginPlus(Plugins me) {

	}

	
	/**
	 * 配置全局拦截器
	 */
	public void configInterceptorPlus(Interceptors me){
 	}
	
	/**
	 * 配置处理器
	 */
	public void configHandlerPlus(Handlers me) {

	}

	public void onStart() {

	}

	public void onStop() {

	}

	/**
	 * 启动入口，运行此 main 方法可以启动项目，此 main 方法可以放置在任意的 Class 类定义中，不一定要放于此
	 */
	public static void main(String[] args) {

		UndertowServer
				.create(ConsumerConfig.class)
				.addHotSwapClassPrefix("org.springcat.dragonli")
				.start();
	}
}
