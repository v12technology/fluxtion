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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.generator.compiler;

import com.fluxtion.api.generation.GenerationContext;
import com.fluxtion.api.node.DeclarativeNodeConiguration;
import com.fluxtion.api.node.NodeFactory;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
     * Map representing the root class to name mapping in the SEP, the keys is
     * the class name as a String.
     */
    private Map<String, String> rootNodeMappings;

    /**
     * Set representing the NodeFactory classes as a String in the SEP.
     */
    private List<String> factoryClassSet;

    /**
     * Map passed to each root node, contains configuration data.
     */
    private Map config;

    public Map<String, String> getRootNodeMappings() {
        return rootNodeMappings;
    }

    public void setRootNodeMappings(Map<String, String> rootNodeMappings) {
        this.rootNodeMappings = rootNodeMappings;
    }

    public List<String> getFactoryClassSet() {
        return factoryClassSet;
    }

    public void setFactoryClassSet(List<String> factoryClassSet) {
        this.factoryClassSet = factoryClassSet;
    }

    public Map getConfig() {
        return config;
    }

    public void setConfig(Map config) {
        this.config = config;
    }

    public DeclarativeNodeConiguration asDeclarativeNodeConiguration() throws ClassNotFoundException {

        // convert rootNodeMappings to classes
        HashMap<Class, String> rootClasses = null;
        if (rootNodeMappings != null) {
            rootClasses = new HashMap();
            for (Map.Entry<String, String> entrySet : rootNodeMappings.entrySet()) {
                String className = entrySet.getKey();
                String nodeName = entrySet.getValue();
                if (GenerationContext.SINGLETON != null && GenerationContext.SINGLETON.getClassLoader() != null) {
                    Class clazz = Class.forName(className, true, GenerationContext.SINGLETON.getClassLoader());
                    rootClasses.put(clazz, nodeName);
                } else {
                    Class clazz = Class.forName(className);
                    rootClasses.put(clazz, nodeName);
                }

            }
        }

        //convert factoryClassSet
        HashSet<Class<? extends NodeFactory>> nodeFactoryClasses = null;
        if (factoryClassSet != null) {
            nodeFactoryClasses = new HashSet();
            for (String factoryClassName : factoryClassSet) {
                if (GenerationContext.SINGLETON != null && GenerationContext.SINGLETON.getClassLoader() != null) {
                    Class clazz = Class.forName(factoryClassName, true, GenerationContext.SINGLETON.getClassLoader());
                    nodeFactoryClasses.add(clazz);
                } else {
                    Class clazz = Class.forName(factoryClassName);
                    nodeFactoryClasses.add(clazz);
                }
            }
        }

        DeclarativeNodeConiguration declarativeCfg
                = new DeclarativeNodeConiguration(rootClasses, nodeFactoryClasses, config);
        return declarativeCfg;
    }

}
