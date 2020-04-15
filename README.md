# dragonli
---------------------------------------------

依赖consul实现一套简易的微服务,目前只适配了jfinal,理论上可以支持其他任意框架,默认采用单参数形式提供服务和调用接口
,通信方式为http+json,代码简单易懂,通过这个代码可以了解微服务的基本原理


### 设计思想及原理

*  类似spring cloud和dubbo,微服务基本套路,rpc通信方式为http+json


### dragonli目前实现的功能：
1. 注册中心
2. 客户端负载均衡 (目前只实现了一致性hash,基本生产环境的唯一选择,其他可以自己扩展)
3. 客户端动态代理实现远程服务调用(类似fegin和dubbo的方式),目前已经实现,后续会完善
4. 健康检查(利用consul的功能封装)
5. 配置中心(利用consul的功能封装),目前已经实现,后续会完善
6. 客户端和服务器端双重参数验证(利用jsr303规范,注解)
7. 通信协议目前默认方式为http+json(http客户端和json序列化性能需要更换其他高性能库来实现)
8. context机制来传递全局变量,为后续调用链,日志,用户身份鉴权等场景打基础传递

### dragonli待实现的功能：
1. 异常处理和熔断处理(准备利用resilience4j来实现)
2. 调用链日志(google drapper论文中的原来,和spring cloud)
3. 监控(准备接入promethues)
4. api gateway待实现( 利用nginx第三方插件,从consul拉取注册的服务或者自己实现)
....

### 启动前,需要先启动consul

### JFinalConfig中配置 ：
```java
	public void configConstant(Constants me) {
        ....
        //初始化consul
		ConsulUtil.init("127.0.0.1",8500);
	}

    public void configRoute(Routes me) {
        ....
		//初始化健康检查
		JFinalStatusController.init(me);
	}
    
    public void configPlugin(Plugins me) { 
             ....
    	    //RegistryP
    		AppInfo appInfo = AppInfo.builder()
    				.name("jfinalDemo")
    				.address("10.0.75.1")
    				.port(8080)
    				.checkUrl("http://10.0.75.1:8080/status")
    				.checkInterval("10s")
    				.checkTimout("1s")
    				.appTags(Arrays.asList("urlprefix-/jfinal/"))
    				.build();
    		ConsulRegistryPlugin consulRegistryPlugin = new ConsulRegistryPlugin(ConsulUtil.use(),appInfo);
    		me.add(consulRegistryPlugin);
    		//init rpc client 客户端才需要,服务端不需要
    		RpcPlugin rpcPlugin = new RpcPlugin("com.demo.blog");
    		me.add(rpcPlugin);
    }
    
	public void configInterceptor(Interceptors me) {
		me.add(new Interceptor() {
			@Override
			public void intercept(Invocation inv) {
				Context.init();
				inv.invoke();
				Context.clear();
			}
		});
 	}
```

### 具体客户端代码中使用 ：
```java
    //定义BlogService.java
    @Rpc("jfinalDemo")
    public interface BlogService {
        BlogPara json(BlogPara blogPara);
    }

    //BlogPara.java
    @Data
    public class BlogPara implements JsonBeanValidate {
    
        private String code;
    
        @Range(min=1,message="4001")
        private int id;
    
        @Length(min = 1,max = 10,message="4002")
        private String title;
    
        @NotBlank(message="4003")
        private String content;
    
    }
    //调用方式
	@Inject
	private BlogService blogService;

    public void test(){
        BlogPara blogPara = new BlogPara();
        blogPara.setId(1);
        blogPara.setContent("content");
        blogPara.setTitle("title");
        BlogPara resp = blogService.json(blogPara);
        renderJson(resp);
    }
```
### 具体服务端代码中使用 ：
```java
    public class BlogController extends JsonController {
        @Before(BlogPara.class)
        public void json() {
            BlogPara jsonBean = getJsonBean();
            jsonBean.setCode("200");
            renderJson(jsonBean);
        }
    }
```

### 调用结果 ：
1.  curl http://localhost:8080/blog/test -d '{"id":10,"title":"title","content":"content"}'
2.  {"code":"200","id":10,"title":"title","content":"content"}
