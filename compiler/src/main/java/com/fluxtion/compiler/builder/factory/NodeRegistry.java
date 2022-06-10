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
package com.fluxtion.compiler.builder.factory;

import com.fluxtion.compiler.builder.generation.NodeNameProducer;
import com.fluxtion.runtime.audit.Auditor;

import java.util.Map;

/**
 * Holds all the currently registered nodes and factories in a graph. If a node
 * cannot be found then a {@link NodeFactory} will be used to create a new
 * instance, using the provided map for configuration.<p>
 *
 * Users interact with the NodeRegistry through callbacks on the
 * {@link NodeFactory} interface.
 *
 * @see NodeFactory
 * @author Greg Higgins
 */
public interface NodeRegistry {

    /**
     * Find or create a node using a registered {@link NodeFactory}. The
     * generated node will have private scope and the name will be generated
     * from a {@link NodeNameProducer} strategy if the supplied name is null.
     *
     * @param <T> The type of the node to be created.
     * @param clazz The class of type T
     * @param config a map used by a NodeFactory to create a node.
     * @param variableName the variable name in the SEP
     *
     * @return The node that is referenced by the config map.
     */
    <T> T findOrCreateNode(Class<T> clazz, Map<?,?> config, String variableName);

    /**
     * Find or create a node using a registered {@link NodeFactory}. The
     * generated node will have public scope and the name will be generated from
     * a {@link NodeNameProducer} strategy if the supplied name is null.
     *
     * @param <T> The type of the node to be created.
     * @param clazz The class of type T
     * @param config a map used by a NodeFactory to create a node.
     * @param variableName the variable name in the SEP
     *
     * @return The node that is referenced by the config map.
     */
    <T> T findOrCreatePublicNode(Class<T> clazz, Map<?,?> config, String variableName);

    /**
     * Register a user created node with Fluxtion generator, no
     * {@link NodeFactory}'s will be used in this operation. The node will have
     * private scope and the name will be generated from a
     * {@link NodeNameProducer} strategy if the supplied name is null.
     *
     * @param <T> The type of the node to be created.
     * @param node The node to add to the SEP
     * @param variableName the variableName name of the node
     * @return
     */
    <T> T registerNode(T node, String variableName);

    /**
     * Register a user created node with Fluxtion generator, no
     * {@link NodeFactory}'s will be used in this operation. The node will have
     * public scope and the name will be generated from a
     * {@link NodeNameProducer} strategy if the supplied name is null.
     *
     * @param <T> The type of the node to be created.
     * @param node The node to add to the SEP
     * @param variableName the variableName name of the node
     * @return
     */
    <T> T registerPublicNode(T node, String variableName);

    /**
     * Register an {@link Auditor} with Fluxtion generator. The Auditor will
     * have public scope registered with the name provided.
     *
     * @param <T> The Auditor class
     * @param node The Auditor to register
     * @param auditorName the public name of the Auditor
     * @return The registered Auditor
     */
    <T extends Auditor> T registerAuditor(T node, String auditorName);
}
