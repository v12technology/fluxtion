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
package com.fluxtion.test.nodegen;

import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.builder.node.DeclarativeNodeConiguration;
import com.fluxtion.builder.node.NodeFactory;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.generator.util.BaseSepInProcessTest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.hamcrest.generator.qdox.JavaDocBuilder;
import org.hamcrest.generator.qdox.model.JavaClass;
import org.hamcrest.generator.qdox.model.JavaField;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
//public class AveragingNodeFactoryTest extends BaseSepTest {
public class AveragingNodeFactoryTest extends BaseSepInProcessTest {

    @Test
    @Ignore
    public void newTest() {
        sep((c) ->{
            SampleNode sampleNode = c.addNode(new SampleNode());
            //factory config
            Set<Class<? extends NodeFactory<?>>> factoryList = new HashSet<>();
            factoryList.add(AveragingNodeFactory.class);
            Map<Object, Object> config = new HashMap<>();
            config.put(AveragingNodeFactory.DATA_SOURCE, sampleNode);
            config.put(AveragingNodeFactory.WINDOW_SIZE, 50);
            config.put(AveragingNodeFactory.DATA_SOURCE_FIELD, "sampleValue");
            //root nodes
            Map<Class<?>, String> rootNodeMappings = new HashMap<>();
            rootNodeMappings.put(AveragingNode.class, "averagingNode");
            c.declarativeConfig = new DeclarativeNodeConiguration(rootNodeMappings, factoryList, config);
        });
        
        
//        try {
//            buildAndInitSep(AvgNodeConfig.class);
//        } catch (Exception e) {
////        wont instantiate becuase the factory only generates and does not 
////        compile and add to classpath
//        }
        //load source file and validate
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(GenerationContext.SINGLETON.getPackageDirectory());
        //assert variable windowNode exists
        JavaClass genClass = builder.getClassByName(GenerationContext.SINGLETON.getPackageName() + "." + GenerationContext.SINGLETON.getSepClassName());
        JavaField rootField = genClass.getFieldByName("averagingNode");
        assertNotNull(rootField);
        //System.out.println("rootField:" + rootField);
        //load generated average class
        JavaClass genAvgClass = builder.getClassByName(rootField.getType().getFullyQualifiedName());
        assertNotNull(genAvgClass);
        String fqn = genAvgClass.getFieldByName("dataSource").getType().getFullyQualifiedName();
        assertEquals(SampleNode.class.getCanonicalName(), fqn);
        JavaField windowField = genAvgClass.getFieldByName("windowSize");
        assertEquals("50", windowField.getInitializationExpression());
    }


    public static class AvgNodeConfig extends SEPConfig {

        {
            SampleNode sampleNode = addNode(new SampleNode());
            //factory config
            Set<Class<? extends NodeFactory<?>>> factoryList = new HashSet<>();
            factoryList.add(AveragingNodeFactory.class);
            HashMap<Object, Object> config = new HashMap<>();
            config.put(AveragingNodeFactory.DATA_SOURCE, sampleNode);
            config.put(AveragingNodeFactory.WINDOW_SIZE, 50);
            config.put(AveragingNodeFactory.DATA_SOURCE_FIELD, "sampleValue");
            //root nodes
            Map<Class<?>, String> rootNodeMappings = new HashMap<>();
            rootNodeMappings.put(AveragingNode.class, "averagingNode");

            declarativeConfig = new DeclarativeNodeConiguration(rootNodeMappings, factoryList, config);

        }
    }


}
