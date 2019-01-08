/* 
 * Copyright (C) 2018 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {

    /**
     * the type of match against the filter this event handler will perform A
     * matched will filter will only process events that match the associated
     * filter Id, this is the default behaviour of the EventHandler.
     *
     * If no filter is supplied then the EventHandler matches against all
     * filters, and will be notified of any incoming event.
     *
     * If filterType is set to
     * {@link com.fluxtion.api.annotations.FilterType#unmatched unmatched} then
     * the handler will be notified if there are no filters in the system that
     * match against the event, this is the same behaviour as the default in a
     * case statement if each matching case calls break.
     *
     * @return FilterType matching strategy
     */
    FilterType value() default FilterType.matched;

    /**
     * The filter value as an int, a value of Integer.MAX_VALUE indicates no
     * filtering should be applied.
     *
     * @return the filter value of the handler to match against filterId of
     * event
     */
    int filterId() default Integer.MAX_VALUE;

    /**
     * The filter value as a String, a zero length String indicates no filtering
     * should be applied.
     *
     * @return the filter value of the handler to match against filterString of
     * event
     */
    String filterString() default "";

    /**
     * A member of this class that provides a value to override static values in
     * annotation.
     *
     * @return FIled providing filter override
     */
    String filterVariable() default "";

    /**
     * The filter value as a String derived from the supplied class. The value
     * is the fully qualified name of the class, a void.class indicates no
     * filtering should be applied. All '.' separators are replaced with '_'
     *
     * @return the filter value of the handler to match against filterString of
     * event
     */
    Class filterStringFromClass() default void.class;

    /**
     * Determines whether the SEP will invoke dependents as part of the event
     * call chain.
     *
     * @return invoke dependents on update
     */
    boolean propagate() default true;
}
