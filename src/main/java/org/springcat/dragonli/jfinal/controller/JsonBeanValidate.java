package org.springcat.dragonli.jfinal.controller;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import org.springcat.dragonli.core.validate.ValidateException;
import org.springcat.dragonli.core.validate.ValidationUtil;


public interface JsonBeanValidate extends Interceptor {


    default void otherValidate(Object obj) throws ValidateException {}

    default void intercept(Invocation invocation) {
        Controller controller = invocation.getController();
        String rawData = controller.getRawData();
        try {
            Object jsonBean = JsonKit.parse(rawData, getClass());

            //自定义验证
            try {
                //jsr303验证
                ValidationUtil.validate(jsonBean);
                //用户自定义验证
                otherValidate(jsonBean);
            } catch (ValidateException e) {
                Ret result = Ret.create().set("code", e.getCode());
                controller.renderJson(result);
                return;
            }

            //验证通过,把json对象缓存起来,减少序列化次数
            controller.setAttr("$jsonbean",jsonBean);
            invocation.invoke();
        } catch (Exception exception){
            System.out.println(exception);
        }
    }
}
