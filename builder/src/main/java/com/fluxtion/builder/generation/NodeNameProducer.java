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
package com.fluxtion.builder.generation;

import java.util.ServiceLoader;

/**
 * Implementing this interface allow users to extend the generation of the SEP
 * with customisable variable names for nodes.
 *
 * Users implement this interface and register using the {@link ServiceLoader}
 * pattern provided in the core Java platform.
 *
 * A default naming strategy will be used if the registered NodeNameProducer
 * returns null.
 *
 * @author Greg Higgins
 */
public interface NodeNameProducer extends Comparable<NodeNameProducer> {

    String mappedNodeName(Object nodeToMap);

    default int priority() {
        return 1000;
    }

    @Override
    default public int compareTo(NodeNameProducer other) {
        return other.priority() - this.priority();
    }
}
