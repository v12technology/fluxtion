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
 * Marks a method to be invoked in the after event phase of event processing. A SEP 
 * processes event handling methods in two phases:
 * <ul>
 * <li>Event in phase - processes handler methods in topological order
 * <li>After event phase - processes handler methods in reverse topological order
 * </ul>
 * <p>
 * An OnEventComplete method will be called after all dependents have finished processing any
 * after event phase methods. OnEventComplete methods are only called if the following 
 * conditions are met:
 * <ul>
 * <li>An event in phase handler is present in the same instance 
 * <li>An event in phase handler is on the current execution path.
 * </ul>
 *
 * @author Greg Higgins
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnEventComplete {

}
