package org.springcat.dragonli.validate;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ValidateException extends Throwable {
    private String code;
}
