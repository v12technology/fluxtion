/* 
 * Copyright (C) 2016-2017 V12 Technology Limited. All rights reserved. 
 *
 * This software is subject to the terms and conditions of its EULA, defined in the
 * file "LICENCE.txt" and distributed with this software. All information contained
 * herein is, and remains the property of V12 Technology Limited and its licensors, 
 * if any. This source code may be protected by patents and patents pending and is 
 * also protected by trade secret and copyright law. Dissemination or reproduction 
 * of this material is strictly forbidden unless prior written permission is 
 * obtained from V12 Technology Limited.  
 */
package com.fluxtion.api.node;

import com.fluxtion.api.generation.GenerationContext;
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
