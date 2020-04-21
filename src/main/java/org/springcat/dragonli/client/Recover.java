package org.springcat.dragonli.client;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

import java.lang.annotation.*;
import java.util.function.Supplier;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Recover {

    CircuitBreakerConfig conf = null;
    Supplier fallbackMethod = null;

}
