package org.springcat.dragonli.core.rpc.ihandle.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.hibernate.validator.HibernateValidator;
import org.springcat.dragonli.core.rpc.RpcRequest;
import org.springcat.dragonli.core.rpc.exception.RpcException;
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

    private final static Log log = LogFactory.get();

    private Validator validator;

    public Jsr303Validation(){
        ValidatorFactory validatorFactory = Validation
                .byProvider(HibernateValidator.class).configure().failFast(false).buildValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public void validate(RpcRequest rpcRequest) throws RpcException {
        Object requestObj = rpcRequest.getRequestObj();
        if(requestObj == null){
            throw new RpcException(RpcExceptionCodes.ERR_REQUEST_NULL.getCode());
        }

        //jsr303验证
        Set<ConstraintViolation<Object>> violations = validator.validate(requestObj);
        if(violations.size() > 0){
            ConstraintViolation<Object> next = violations.iterator().next();
            log.error("validate error request{},code:{}",rpcRequest,next.getMessage());
            throw new RpcException(next.getMessage());
        }
    }
}
