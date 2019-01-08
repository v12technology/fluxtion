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

import com.fluxtion.builder.generation.GenerationContext;
import java.util.Map;
import java.util.Set;

/**
 * A factory for creating instances. The SEP will call this method to create
 * nodes.
 *
 * Instance re-use. To ensure that node instances are re-used there are two
 * approaches:
 *
 * 1. The factory caches the node, and returns the same instance for the same
 * configuration. 2. The factory creates new nodes, but the node overrides
 * hashcode and equals so interchangeable node instances will return true when
 * equality is tested. The framework will only use one of the instances and
 * ignore the subsequently created instances.
 *
 * @author Greg Higgins
 * @param <T>
 */
public interface NodeFactory<T> {

    /**
     *
     * @param config
     * @param registry
     * @return The newly created node instance
     */
    T createNode(Map config, NodeRegistry registry);
    
    /**
     * 
     * @param config
     * @param registry
     * @param instance 
     */
    default void postInstanceRegistration(Map config, NodeRegistry registry, T instance) { }

    /**
     * 
     * If the node generates a class for this SEP, this callback will indicate
     * the desired target.
     * 
     * @param targetLanguage 
     */
    default public void setTargetLanguage(String targetLanguage) { }

    /**
     * 
     * If the node generates a class for this SEP, this callback gives 
     * the node access to the GenerationContext. 
     * 
     * @param context 
     */
    default public void preSepGeneration(GenerationContext context) { }

}
