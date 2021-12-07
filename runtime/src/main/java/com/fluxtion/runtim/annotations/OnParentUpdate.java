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
package com.fluxtion.runtim.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method to be called in a node when a dependency has processed an
 * event. Identifying which parent has changed can be useful when a node has
 * multiple dependencies on differing execution paths.
 * OnParentUpdate gives more granular notification than OnEvent, by identifying
 * which parents have updated. The marked method has a single argument, the type
 * of the parent.<p>
 *
 * The marked method(s) will be invoked during the event in phase before any
 * {@link OnEvent} methods in this node are invoked.
 *
 * A SEP processes event handling methods in two phases:
 * <ul>
 * <li>Event in phase - processes handler methods in topological order
 * <li>After event phase - processes handler methods in reverse topological
 * order
 * </ul>
 *
 * <h2>Parent resolution</h2>
 * The type of the argument in the OnParentUpdate method is used to
 * resolve the parent to monitor. Optionally a {@link #value()} specifies the
 * field name of the parent to monitor. If multiple parents exist within this
 * class of the same type, type resolution is non-deterministic, specifying the
 * value predictably determines which parent is monitored.
 *
 * <h2>collections</h2>
 * If the OnParentUpdate method points to a collection, the instance inside the
 * collection that is updated will be passed to the method as an argument. As
 * multiple instances inside a collection maybe updated in an execution cycle,
 * the same OnParentUpdate method may be called multiple times in that cycle.
 *
 * @author Greg Higgins
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnParentUpdate {

    /**
     * The variable name of the parent reference to bind listen notifications
     * to.
     *
     * @return The variable name of the parent to monitor
     */
    String value() default "";

    /**
     * determines whether guards are present on the marked method.Setting
     * the value to false will ensure the callback is always called regardless
     * of the dirty state of the parent node.
     *
     * @return guarding of OnParentUpdate callback
     */
    boolean guarded() default true;
}
