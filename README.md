# dragonli
---------------------------------------------

依赖consul实现一套简易的微服务,目前只适配了jfinal,理论上可以支持其他任意框架,默认采用单参数形式提供服务和调用接口
,通信方式为http+json,代码简单易懂,通过这个代码可以了解微服务的基本原理,暂时不能用于生产环境


### 设计思想及原理

*  类似spring cloud和dubbo,微服务基本套路,rpc通信方式为http+json,如果不想要这种客户端负载均衡策略方式,也可以用fabio,或者nginx+三方插件这种方式从consul拉取服务,配置路由策略,来实现服务器端的负均衡和跨语言支持


### dragonli目前实现的功能：
1. 注册中心(目前调用接口前实时查询consul agent获取健康的服务,后续需要做缓存,定时刷入调用端缓存)
2. 客户端负载均衡 (目前只实现了一致性hash,基本生产环境的唯一选择,其他可以自己扩展)
3. 客户端动态代理实现远程服务调用(类似fegin和dubbo的方式)
4. 健康检查(利用consul的功能封装)
5. 配置中心(利用consul的功能封装)
6. 客户端和服务器端双重参数验证(利用jsr303规范,注解)
7. 通信协议目前默认方式为http+json(http客户端和json序列化性能需要更换其他高性能库来实现)
8. context机制来传递全局变量,为后续调用链,日志,用户身份鉴权等场景打基础传递
9. 异常处理和熔断处理(利用resilience4j来实现)
10. 灵活的框架参数配置
11. api gateway开发中(准备基于原生的servlet3实现,暂时不考虑netty实现,支持动态路由刷新)
12. 灵活的api label配置,可以在调用时指定标签,调用对应标签的api,用于实现现网,沙箱,跨机房等服务分组的需求
### dragonli待实现的功能：
1. api doc生成(因为采用单参数接口的形式,通过反射获取对象属性和jsr303的标签,作为原数据,可以生成md文档,
    或者暴露元数据以供生成各种格式的文档的)
2. 调用链日志(google drapper论文中的原来,和spring cloud)
3. 监控(准备接入promethues)
4. 单测补齐,各种场景的充分测试和性能压测
5. 部分代码实现优化
6. 原生servlet的支持
7. 内嵌容器

....

### 启动前,需要先启动consul

### JFinalConfig中配置,继承DragonLiConfig,在resources中拖入dragonli.setting修改对应配置就好：
```java
        public class DemoConfig extends DragonLiConfig {
        	...
        
        }
```

### 具体消费者代码中使用 ：
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
        BlogPara jsonBean = getJsonBean(BlogPara.class);
        BlogPara resp = blogService.json(blogPara);
        renderJson(resp);
    }
```
### 具体生产者代码中使用 ：
```java
    public class BlogController extends JsonController {
        @Before(BlogPara.class)
        public void json() {
            BlogPara jsonBean = getJsonBean(BlogPara.class);
            jsonBean.setCode("200");
            renderJson(jsonBean);
        }
    }
```

### 调用结果 ：
1.  curl http://localhost:8080/blog/test -d '{"id":10,"title":"title","content":"content"}'
2.  {"code":"200","id":10,"title":"title","content":"content"}

### 示例的dragonli-demo
apigateway 默认8080端口
consumer   默认8082端口
producer   默认8081端口

1 curl --location --request POST 'http://localhost:8080/consumer/blog/testRpc' \
--header 'x-uid: 111' \
--header 'Content-Type: text/plain' \
--data-raw '{
"id":1,
"title":"title",
"content":"content"
}' 

2 result:  {"code":"0","id":0,"title":null,"content":null}
