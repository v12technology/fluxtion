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
package com.fluxtion.compiler.generation.model;

import com.fluxtion.compiler.builder.factory.NodeNameProducer;
import com.fluxtion.compiler.generation.GenerationContext;
import com.fluxtion.runtime.NamedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ServiceLoader;

/**
 * @author gregp
 */
class NamingStrategy implements NodeNameProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NamingStrategy.class);
    private ArrayList<NodeNameProducer> namingStrategies;
    private int count;

    public NamingStrategy() {
        loadServices();
    }

    public final void loadServices() {
        LOGGER.debug("NamingStrategy (re)loading naming strategies");
        ServiceLoader<NodeNameProducer> loadServices;
        namingStrategies = new ArrayList<>();
        if (GenerationContext.SINGLETON != null && GenerationContext.SINGLETON.getClassLoader() != null) {
            LOGGER.debug("using custom class loader to search for NodeNameProducer");
            loadServices = ServiceLoader.load(NodeNameProducer.class, GenerationContext.SINGLETON.getClassLoader());
        } else {
            loadServices = ServiceLoader.load(NodeNameProducer.class);
        }
        loadServices.forEach(namingStrategies::add);
        Collections.sort(namingStrategies);
        LOGGER.debug("sorted naming strategies : {}", namingStrategies);
    }

    @Override
    public String mappedNodeName(Object nodeToMap) {
        String mappedName = null;
        for (NodeNameProducer namingStrategy : namingStrategies) {
            mappedName = namingStrategy.mappedNodeName(nodeToMap);
            if (mappedName != null)
                break;
        }
        if (mappedName == null && nodeToMap instanceof NamedNode) {
            mappedName = ((NamedNode) nodeToMap).getName();
        }
        if (mappedName == null) {
            mappedName = nodeToMap.getClass().getSimpleName() + "_" + count++;
            mappedName = Character.toLowerCase(mappedName.charAt(0)) + (mappedName.length() > 1 ? mappedName.substring(1) : "");
        }
        return mappedName;
    }

}
