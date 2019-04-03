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

/**
 * Marks a method as an entry point to an execution path. Fluxtion reads the set
 * of entry points and constructs an execution graph at build time. A valid
 * event handler method accepts a single parameter of type
 * {@link com.fluxtion.api.event.Event} and optionally returns a boolean value.
 * The boolean value indicates if a change has occurred. If conditional
 * processing is enabled for the SEP, the following
 * strategy is employed for interpreting notification and branching
 * execution:
 * <ul>
 * <li>return = true : indicates a change has occurred at this node
 * <li>return = false : indicates a change has NOT occurred at this node
 * </ul>
 * 
 * An EventHandler can optionally provide a filter value and match strategy to
 * specialise the events that are accepted for processing, see {@link #value() }
 * .<p>
 *
 * A node must be in the execution graph to be included in the invocation chain.
 * The Fluxtion builder api provides methods to register an instance in the
 * event processor.
 *
 * @author V12 Technology Ltd.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {

    /**
     * The match strategy this event handler will employ when filtering incoming
     * events. The default filtering behaviour of the EventHandler is to pass
     * events through where the filter id matched the filter id of the event
     * handler..
     *
     * If no filter is supplied then the EventHandler matches against all
     * filters, and will be notified of any incoming event.
     *
     * Available strategies are:
     * <ul>
     * <li> {@link FilterType#matched} Only matching filters allow event
     * propagation
     * <li> {@link FilterType#unmatched} Invoked when no filter match is found,
     * acts as a default case.
     * </ul>
     *
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
