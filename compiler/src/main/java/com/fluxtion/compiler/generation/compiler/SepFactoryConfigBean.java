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
package com.fluxtion.compiler.generation.compiler;

import com.fluxtion.compiler.builder.generation.GenerationContext;
import com.fluxtion.compiler.builder.node.NodeFactoryRegistration;
import com.fluxtion.compiler.builder.node.NodeFactory;

import java.util.HashSet;
import java.util.List;

/**
 * A javabean holding configuration properties that are passed into the SEP
 * generator. The bean pattern helps with marshallers that understand the
 * pattern and can read/write to non-java formats to a javabean, for example
 * yaml.
 *
 * @author Greg Higgins
 */
public class SepFactoryConfigBean {

    /**
     * Set representing the NodeFactory classes as a String in the SEP.
     */
    private List<String> factoryClassSet;

    public List<String> getFactoryClassSet() {
        return factoryClassSet;
    }

    public void setFactoryClassSet(List<String> factoryClassSet) {
        this.factoryClassSet = factoryClassSet;
    }

    @SuppressWarnings("unchecked")
    public NodeFactoryRegistration asDeclarativeNodeConfiguration() throws ClassNotFoundException {
        //convert factoryClassSet
        HashSet<Class<? extends NodeFactory<?>>> nodeFactoryClasses = null;
        if (factoryClassSet != null) {
            nodeFactoryClasses = new HashSet<>();
            for (String factoryClassName : factoryClassSet) {
                if (GenerationContext.SINGLETON != null && GenerationContext.SINGLETON.getClassLoader() != null) {
                    Class<? extends NodeFactory<?>> clazz = (Class<? extends NodeFactory<?>>) Class.forName(factoryClassName, true, GenerationContext.SINGLETON.getClassLoader());
                    nodeFactoryClasses.add(clazz);
                } else {
                    Class<? extends NodeFactory<?>> clazz = (Class<? extends NodeFactory<?>>) Class.forName(factoryClassName);
                    nodeFactoryClasses.add(clazz);
                }
            }
        }
        return new NodeFactoryRegistration(nodeFactoryClasses);
    }

}
