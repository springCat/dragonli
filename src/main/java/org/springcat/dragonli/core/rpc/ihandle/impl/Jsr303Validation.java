package org.springcat.dragonli.core.rpc.ihandle.impl;

import org.hibernate.validator.HibernateValidator;
import org.springcat.dragonli.core.rpc.exception.RpcExceptionCodes;
import org.springcat.dragonli.core.rpc.ihandle.IValidation;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * 用jsr303简化参数验证
 */
public class Jsr303Validation implements IValidation {

    private Validator validator;

    public Jsr303Validation(){
        ValidatorFactory validatorFactory = Validation
                .byProvider(HibernateValidator.class).configure().failFast(false).buildValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public String validate(Object jsonBean){
        if(jsonBean == null){
            return RpcExceptionCodes.REQUEST_NONNULL.getCode();
        }
        //jsr303验证
        Set<ConstraintViolation<Object>> violations = validator.validate(jsonBean);
        if(violations.size() > 0){
            ConstraintViolation<Object> next = violations.iterator().next();
            return next.getMessage();
        }
        return null;
    }
}
