package org.springcat.dragonli.rpc;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Rpc {

    //appName,工程注册的服务名 ,默认请求路径为  http://{ip}:{port}/{rootPath}/{className(首字母小写,去掉后缀Service)}/{methodName}
    String appName();

    //工程部署根路径,默认为空,也建议为空,但还是留个口给需要的人
    String rootPath() default "";

    //url,最高优先级,有这个属性后,请求就变为了 http://{ip}:{port}/{url}/{methodName}
    String url() default "";

    //预留,为了实现跨分组服务功能,基于这个功能可以实现优先调用本机房服务,遇到故障再调用跨机房服务的功能
    String[] labels() default {"DEFAULT"};
}
