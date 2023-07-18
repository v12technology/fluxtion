package com.fluxtion.runtime.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an exported function as non propagating.
 *
 * @author Greg Higgins
 * @see ExportFunction
 * @see ExportService
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NoPropagateFunction {
}
