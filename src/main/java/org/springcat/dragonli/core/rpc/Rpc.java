package org.springcat.dragonli.core.rpc;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Rpc {

    //appName,工程注册的服务名 ,默认请求路径为  http://{ip}:{port}/{className(首字母小写,去掉后缀Service)}/{methodName}
    String appName();

    //url,最高优先级,有这个属性后,请求就变为了 http://{ip}:{port}/{url}
    String url() default "";

    //预留,为了实现跨分组服务功能,基于这个功能可以实现优先调用本机房服务,遇到故障再调用跨机房服务的功能
   String[] labels() default {};
}
