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

import java.util.Map;

/**
 * Holds all the currently registered nodes in a graph. If a node cannot be
 * found then a NodeFactory will be used to create a new instance, using the
 * provided map for configuration.
 *
 * @author Greg Higgins
 */
public interface NodeRegistry {

    /**
     *
     * @param <T> The type of the node to be created.
     * @param clazz The class of type T
     * @param config a map used by a NodeFactory to create a node.
     * @param variableName
     *
     * @return The node that is referenced by the config map.
     */
    <T> T findOrCreateNode(Class<T> clazz, Map config, String variableName);

    <T> T findOrCreatePublicNode(Class<T> clazz, Map config, String variableName);
    //TODO add a register node method for nodes that are created during another 
    //nodes findOrCreateProcess, perfect for factories to generate nodes on 
    //the fly with a simple static method and then register the newly created
    //node
    <T> T registerNode(T node, String variableName);
    
    <T> T registerPublicNode(T node, String variableName);

}
