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
 * Marks a method to be called in a class when a parent node has processed an
 * event. This gives more granular notification than OnEvent, by identifying
 * which parents have updated. The marked method has a single argument, the type
 * of the parent. The type of the argument in the monitoring method is used to
 * resolve the parent to monitor<p>
 *
 * Identifying which parent has changed can be useful in applications. The
 * marked method(s) will be invoked before any {@link OnEvent} methods in this
 * node are invoked.<p>
 *
 * Optionally a {@link #value()} specifies the field name of the
 * parent to monitor. If multiple parents exist within this class of the same
 * type, type resolution is non-deterministic, specifying the value predictably
 * determines which parent is monitored.
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
     * determines whether guards are present on the marked method. Setting
     * the value to false will ensure the callback is always called regardless
     * of the dirty state of the parent node.
     */
    boolean guarded() default true;
}
