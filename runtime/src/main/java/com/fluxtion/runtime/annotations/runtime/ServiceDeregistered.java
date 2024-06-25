package com.fluxtion.runtime.annotations.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotate a method that will receive a callback when a service is de-registered at runtime in the container
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ServiceDeregistered {

    /**
     * Filter a service by name as well as class
     *
     * @return the service name to filter for
     */
    String value() default "";
}
