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
package com.fluxtion.builder.node;

import com.fluxtion.builder.annotation.Config;
import com.fluxtion.builder.annotation.Inject;
import com.fluxtion.builder.generation.GenerationContext;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * A factory for creating instances. The SEP will call this method to create
 * nodes.<p>
 *
 * <h2>Instance re-use</h2>
 * To ensure that node instances are re-used there are two approaches:
 * <ul>
 * <li>The factory caches the node, and returns the same instance for the same
 * configuration.
 * <li>The factory creates new nodes and the node overrides hashcode and equals.
 * The {@link NodeRegistry} will consult a map for an equivalent node. If an
 * equivalent node is found then the newly created node will be discarded and
 * the existing instance used.
 * </ul>
 * <p>
 * <h2>Registering factories</h2>
 * Fluxtion employs the {@link ServiceLoader} pattern to register user
 * implemented NodeFactories. Please read the java documentation describing the
 * meta-data a factory implementor must provide to register a factory using the
 * {@link ServiceLoader} pattern.
 *
 * @author Greg Higgins
 * @param <T>
 */
public interface NodeFactory<T> {

    /**
     * NodeFactory writer must implement this method to generate instances of
     * nodes. The Fluxtion compiler will call this method when an {@link Inject}
     * instance is created. {@link Config} variables are used to populate the
     * config map.
     *
     * @param config map configuration
     * @param registry The node registry of the current generation context
     * @return The newly created node instance
     */
    T createNode(Map<?,?> config, NodeRegistry registry);

    /**
     * Callback invoked by Fluxtion generator after the generated SEP has been
     * registered in the{@link GenerationContext}
     *
     * @param config map configuration
     * @param registry The node registry of the current generation context
     * @param instance the newly created instance
     */
    default void postInstanceRegistration(Map<? super Object, ? super Object> config, NodeRegistry registry, T instance) {
    }

    /**
     *
     * If the node generates a class for this SEP, this callback will indicate
     * the desired target.
     *
     * @param targetLanguage target language for generation
     */
    default void setTargetLanguage(String targetLanguage) {
    }

    /**
     *
     * If the node generates a class for this SEP, this callback gives the node
     * access to the GenerationContext before generation.
     *
     * @param context The context the Fluxtion SEC compiler uses
     */
    default void preSepGeneration(GenerationContext context) {
    }

}
