/*
 * Copyright (C) 2018 2024 gregory higgins.
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
package com.fluxtion.runtime.annotations;

import com.fluxtion.runtime.event.Event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as an entry point to an execution path contained with the
 * execution graph. An event handler is invoked on the event in phase of
 * execution processing. A SEP processes event handling methods in two phases:
 * <ul>
 * <li>Event in phase - processes handler methods in topological order
 * <li>After event phase - processes handler methods in reverse topological
 * order
 * </ul>
 * <p>
 * <p>
 * Fluxtion reads the set
 * of entry points and constructs an execution graph at build time. A valid
 * event handler method accepts a single parameter of type
 * {@link com.fluxtion.runtime.event.Event} and optionally returns a boolean value
 * or void.
 * The boolean value indicates if a change has occurred during the processing of
 * the event.
 *
 * <h2>Conditional processing</h2>
 * If conditional
 * processing is enabled for the SEP, the following
 * strategy is employed for interpreting notification and branching
 * execution:
 * <ul>
 * <li>return = true : indicates a change has occurred processing the event
 * <li>return = false : indicates a change has NOT occurred processing the event
 * <li>return = void : assumes a change has occurred processing the event
 * </ul>
 * <p>
 * <p>
 * Conditional branching execution behaves as follows:
 * <ul>
 * <li>if a change is indicated the execution will propagate along the execution
 * path.
 * <li>No change notification will remove this node from the current execution
 * path.
 * </ul>
 *
 * <h2>Filtering</h2>
 * An EventHandler can optionally provide a filter value and match strategy to
 * specialise the events that are accepted for processing, see {@link #value()
 * }. An
 * {@link Event} can optionally specify a filter value {@link Event#filterString()
 * }. The
 * SEP will compare the filter values in the {@link Event} and the handler and
 * propagate
 * the Event conditional upon the {@link FilterType}.
 * .<p>
 * <p>
 * A node must be in the execution graph to be included in the invocation chain.
 * The Fluxtion builder api provides methods to register an instance in the
 * event processor.
 *
 * @author 2024 gregory higgins.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnEventHandler {

    /**
     * The match strategy this event handler will employ when filtering incoming
     * events. The default filtering behaviour of the EventHandler is to pass
     * through events where the filter id matches the filter id of the event
     * handler.
     * <p>
     * If no filter is supplied then the EventHandler matches against all
     * filters, and will be notified of any incoming event.
     * <p>
     * Available strategies are:
     * <ul>
     * <li> {@link FilterType#matched} Only matching filters allow event
     * propagation
     * <li> {@link FilterType#defaultCase} Invoked when no filter match is found,
     * acts as a default case.
     * </ul>
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
     * @return field providing filter override
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
     * Overrides the event type this event handler will process. Fluxtion dispatches based on concrete type. Mark a method
     * with the concrete type using this value, the handler parameter can be any super type of this value.
     *
     * @return The concrete type to dispatch to this handler
     */
    Class ofType() default void.class;

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
