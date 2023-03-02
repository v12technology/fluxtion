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
package com.fluxtion.runtime.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a handler method as a member of an execution path on the event in phase.
 * This method will be invoked invoked in topological order during the event in phase, i.e. after all its
 * dependencies on the execution path have processed the event.<p>
 * <p>
 * A SEP processes event handling methods in two phases:
 * <ul>
 * <li>Event in phase - processes handler methods in topological order
 * <li>After event phase - processes handler methods in reverse topological
 * order
 * </ul>
 *
 * <h2>Condtional processing</h2>
 * A valid OnEvent accepts no arguments and optionally returns a boolean
 * indicating a change
 * has occurred. If conditional processing is enabled for the SEP, the following
 * strategy is employed for interpreting notification and branching execution:
 * <ul>
 * <li>return = true : indicates a change has occurred at this node
 * <li>return = false : indicates a change has NOT occurred at this node
 * </ul>
 *
 * <h2>Dirty strategy</h2>
 * The {@link #dirty() } method controls the propagation strategy for
 * conditional branching execution.
 * <ul>
 * <li>dirty = true : invoked if any dependent returns true from their event
 * handling method
 * <li>dirty = false : invoked if any dependent returns false from their
 * event handling method
 * </ul>
 * <p>
 * <p>
 * A node must be in the execution graph to be included in the invocation chain.
 * The Fluxtion builder api provides methods to register an instance in the
 * event processor.
 *
 * @author Greg Higgins
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnTrigger {

    /**
     * Controls the event calling strategy for this node. If conditional
     * execution is enabled in the SEP, the notification status of the dependent
     * determines if this method is invoked.
     *
     * <ul>
     * <li>dirty = true : invoked if any dependent returns true from their event
     * handling method
     * <li>dirty = false : invoked if any dependent returns false from their
     * event handling method
     * </ul>
     *
     * @return dirty monitoring strategy of dependencies
     */
    boolean dirty() default true;

    /**
     * Validates at generation time the method has guards applied. If the check fails a {@link RuntimeException} will
     * be thrown.
     * <p>
     * This is a generation time only
     *
     * @return
     */
    boolean failBuildIfNotGuarded() default true;
}
