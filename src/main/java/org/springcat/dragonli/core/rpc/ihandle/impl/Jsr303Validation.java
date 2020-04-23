package org.springcat.dragonli.core.rpc.ihandle.impl;

import org.hibernate.validator.HibernateValidator;
import org.springcat.dragonli.core.rpc.exception.ValidateException;
import org.springcat.dragonli.core.rpc.ihandle.IValidation;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class Jsr303Validation implements IValidation {

    private Validator validator;

    public Jsr303Validation(){
        ValidatorFactory validatorFactory = Validation
                .byProvider(HibernateValidator.class).configure().failFast(false).buildValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public void validate(Object jsonBean) throws ValidateException {
        //jsr303验证
        Set<ConstraintViolation<Object>> violations = validator.validate(jsonBean);
        if(violations.size() > 0){
            ConstraintViolation<Object> next = violations.iterator().next();
            throw new ValidateException(next.getMessage());
        }
    }
}
