package org.springcat.dragonli.core.rpc.validate;
import lombok.experimental.UtilityClass;
import org.hibernate.validator.HibernateValidator;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

@UtilityClass
public class ValidationUtil {

    private  Validator validator;

    public void init(){
        ValidatorFactory validatorFactory = Validation
                .byProvider(HibernateValidator.class).configure().failFast(false).buildValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public Validator getValidator(){
        return validator;
    }

    public void validate(Object jsonBean) throws ValidateException{
        //jsr303验证
        Set<ConstraintViolation<Object>> violations = ValidationUtil.getValidator().validate(jsonBean);
        if(violations.size() > 0){
            ConstraintViolation<Object> next = violations.iterator().next();
            throw new ValidateException(next.getMessage());
        }
    }

}
