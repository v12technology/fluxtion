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
package com.fluxtion.compiler.builder.node;

import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Registered {@link NodeFactory} available to support {@link com.fluxtion.runtime.annotations.builder.Inject}
 * annotation
 *
 */
@EqualsAndHashCode
public final class NodeFactoryRegistration {

    /**
     * The set of factory classes used for node creation, each factory must
     * have default constructor so the SEP can instantiate the factory. 
     * The classes in factoryClassSet are instantiated and merged into the 
     * factorySet instances.
     */
    public final Set<Class<? extends NodeFactory<?>>> factoryClassSet;

    /**
     * The factory instances registered that can create new instances of
     * nodes.
     */
    public final Set<NodeFactory<?>> factorySet;

    public NodeFactoryRegistration(Set<Class<? extends NodeFactory<?>>> factoryList) {
        this(factoryList, null);
    }

    public NodeFactoryRegistration(Set<Class<? extends NodeFactory<?>>> factoryList, Set<NodeFactory<?>> factorySet) {
        this.factoryClassSet = factoryList == null ? new HashSet<>() : factoryList;
        this.factorySet = factorySet == null ? new HashSet<>() : factorySet;
    }
}
