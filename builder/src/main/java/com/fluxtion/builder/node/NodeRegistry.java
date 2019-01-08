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
