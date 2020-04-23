package org.springcat.dragonli.core.rpc.ihandle;

import org.springcat.dragonli.core.rpc.exception.ValidateException;

public interface IValidation {
     void validate(Object jsonBean) throws ValidateException;

     default void errorHandler(ValidateException exception){

     }
}
