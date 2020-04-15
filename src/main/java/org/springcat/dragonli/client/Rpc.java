package org.springcat.dragonli.client;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Rpc {
    String value();
    //预留,为了实现跨分组服务功能,基于这个功能可以实现优先调用本机房服务,遇到故障再调用跨机房服务的功能
    String[] label() default {};
}
