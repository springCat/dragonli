package org.springcat.dragonli.jfinal.controller;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public interface JsonBeanValidate extends Interceptor {

     Validator validator = Validation.byProvider(HibernateValidator.class).configure().failFast(false).buildValidatorFactory().getValidator();

     default void validate(Object jsonBean) throws Throwable {
        //jsr303验证
        Set<ConstraintViolation<Object>> violations = validator.validate(jsonBean);
        if(violations.size() > 0){
            ConstraintViolation<Object> next = violations.iterator().next();
            throw new Throwable(next.getMessage());
        }
    }

    default void otherValidate(Object obj) throws Throwable {}

    default void intercept(Invocation invocation) {
        Controller controller = invocation.getController();
        String rawData = controller.getRawData();
        try {
            Object jsonBean = JsonKit.parse(rawData, getClass());
            //自定义验证
            try {
                //jsr303验证
                validate(jsonBean);
                //用户自定义验证
                otherValidate(jsonBean);
            } catch (Throwable e) {
                Ret result = Ret.create().set("code", e.getMessage());
                controller.renderJson(result);
                return;
            }
            //验证通过,把json对象缓存起来,减少序列化次数
            controller.setAttr("$jsonbean$",jsonBean);
            invocation.invoke();
        } catch (Exception exception){
            System.out.println(exception);
        }
    }
}
