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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a reference as an injection point for an instance. An injected instance
 * is created using a NodeFactory, see Fluxtion builder module.
 * <p>
 * <p>
 * If no NodeFactory is implemented for this type then a zero argument
 * constructor will be used to inject the reference. Properties of the injected
 * instance will be set using the configuration map, where keys are the member
 * properties of the class.<p>
 * <p>
 * Once an injected instance has been created it will be added to the SEP
 * execution graph as if a user had added the node manually. The injected node
 * will be inspected for annotations and processed by the Fluxtion Static Event
 * Compiler. New injected instances are recursively inspected until no more
 * injected instances are added to the execution graph. The recursive addition
 * of injected instances allows arbitrarily complex execution graphs be created
 * with a single {@literal @}Inject annotation.<p>
 * <p>
 * Injected references can be configured using {@link ConfigVariable} and
 * {@link Config} annotations.
 *
 * @author Greg Higgins
 * @see Config
 * @see ConfigVariable
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Inject {

    /**
     * Create and inject only a single instance in the generated SEP if true.
     *
     * @return singleton flag
     */
    boolean singleton() default false;

    /**
     * The name of the singleton if the singleton = true
     *
     * @return singleton name
     */
    String singletonName() default "";

    /**
     * The name of the NodeFactory.
     *
     * @return the NodeFactory name that will inject the instance into the graph
     */
    String factoryName() default "";

    /**
     * The name of the NodeFactory read from a variable in the annotated class.
     *
     * @return the NodeFactory name that will inject the instance into the graph
     */
    String factoryVariableName() default "";

    /**
     * Specify the qualifier of the instance to inject if using runtime instance injection with:
     * {@link com.fluxtion.runtime.StaticEventProcessor#injectNamedInstance(Object, String)}
     *
     * @return the runtime instance qualifer
     */
    String instanceName() default "";

    /**
     * Specify the qualifier of the instance to inject if using runtime instance injection with,
     * read from a variable in the annotated class.
     * <p>
     * {@link com.fluxtion.runtime.StaticEventProcessor#injectNamedInstance(Object, String)}
     *
     * @return the runtime instance qualifer
     */
    String instanceVariableName() default "";
}
