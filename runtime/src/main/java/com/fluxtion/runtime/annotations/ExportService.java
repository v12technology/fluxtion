package com.fluxtion.runtime.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
public @interface ExportService {

    /**
     * Determines whether the SEP will invoke dependents as part of the event
     * call chain. This has the effect of overriding the return value from the
     * event handler
     * method in the user class with the following effect:
     * <ul>
     * <li>true - use the boolean return value from event handler to determine
     * event propagation.
     * <li>false - permanently remove the event handler method from the
     * execution path
     * </ul>
     *
     * @return invoke dependents on update
     */
    boolean propagate() default true;
}
