/* 
 * Copyright (c) 2019, V12 Technology Ltd.
 * All rights reserved.
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
package com.fluxtion.generator.graphbuilder;

import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.builder.node.NodeFactory;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads a set of NodeFactory using the {@link ServiceLoader} support provided
 * by Java platform. New factories can be added to Fluxtion using the extension
 * mechanism described in {@link ServiceLoader} documentation.
 *
 * @author greg
 */
public class NodeFactoryLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeFactoryLocator.class);

    public static final Set<Class<? extends NodeFactory>> nodeFactorySet() throws Exception {
        LOGGER.debug("NodeFactory locator");
        ServiceLoader<NodeFactory> loadServices;
        Set<Class<? extends NodeFactory>> subTypes = new HashSet<>();
        if (GenerationContext.SINGLETON != null && GenerationContext.SINGLETON.getClassLoader() != null) {
            LOGGER.debug("using custom class loader to search for factories");
            loadServices = ServiceLoader.load(NodeFactory.class, GenerationContext.SINGLETON.getClassLoader());
        } else {
            loadServices = ServiceLoader.load(NodeFactory.class);
        }
        loadServices.forEach((t) -> {
            subTypes.add(t.getClass());
        });
        LOGGER.debug("loaded NodeFactory services:{}", subTypes);
        return subTypes;
    }

}
