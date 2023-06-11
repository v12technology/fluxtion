package com.fluxtion.runtime.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExportFunction {

    /**
     * The name of the exported function in the generated {@link com.fluxtion.runtime.StaticEventProcessor}
     *
     * @return method name exported
     */
    String value() default "";

    /**
     * The filter value as a String, a zero length String indicates no filtering
     * should be applied.
     *
     * @return the filter value of the handler to match against filterString of
     * event
     */
//    String filterString() default "";

    /**
     * A member of this class that provides a value to override static values in
     * annotation.
     *
     * @return field providing filter override
     */
//    String filterVariable() default "";
}
