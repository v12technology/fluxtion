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
package com.fluxtion.runtime.annotations.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A static String based key value pair configuration item. The key/value pair
 * is added to a map which is supplied to a Nodefactory. A NodeFactory uses the
 * configuration map to build an injected instance.
 *
 * @author Greg Higgins
 * @see Inject
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(value = ConfigList.class)
public @interface Config {

    /**
     * The configuration value.
     *
     * @return The configuration value
     */
    String value();

    /**
     * The key the value will be assigned to in the configuration map.
     *
     * @return the configuration key
     */
    String key();

}
