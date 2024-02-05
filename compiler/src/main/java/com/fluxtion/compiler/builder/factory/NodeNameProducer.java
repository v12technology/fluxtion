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
package com.fluxtion.compiler.builder.factory;

import java.util.ServiceLoader;

/**
 * Implementing this interface allow users to extend the generation of the SEP
 * with customisable variable names for nodes. A default naming strategy will be
 * used if the registered NodeNameProducer returns null.<p>
 * <p>
 * An optional {@link #priority() } can be returned to Fluxtion generator,
 * allowing control of naming strategy resolution.
 *
 *
 * <h2>Registering factories</h2>
 * Fluxtion employs the {@link ServiceLoader} pattern to register user
 * implemented NodeNameProducer's. Please read the java documentation describing
 * the meta-data a factory implementor must provide to register a factory using
 * the {@link ServiceLoader} pattern.
 *
 * @author Greg Higgins
 */
public interface NodeNameProducer extends Comparable<NodeNameProducer> {

    /**
     * The mapping strategy for naming this node. The name returned will be used
     * in the generated source code as the variable name of the node.
     *
     * @param nodeToMap incoming node
     * @return the name of the node
     */
    String mappedNodeName(Object nodeToMap);

    /**
     * The priority of this naming strategy. Default value = 1000.
     *
     * @return naming strategy priority.
     */
    default int priority() {
        return 1000;
    }

    @Override
    default int compareTo(NodeNameProducer other) {
        return other.priority() - this.priority();
    }
}
