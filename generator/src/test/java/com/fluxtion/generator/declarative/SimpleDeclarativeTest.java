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
package com.fluxtion.generator.declarative;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.builder.node.DeclarativeNodeConiguration;
import com.fluxtion.builder.node.NodeFactory;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.generator.Generator;
import com.fluxtion.generator.graphbuilder.NodeFactoryLocator;
import com.fluxtion.generator.model.Field;
import com.fluxtion.generator.model.SimpleEventProcessorModel;
import com.fluxtion.generator.model.TopologicallySortedDependecyGraph;
import com.fluxtion.generator.targets.SepJavaSourceModel;
import com.fluxtion.test.nodes.Calculator;
import com.fluxtion.test.nodes.CalculatorRegisteringAccumulatorFactory;
import com.fluxtion.test.nodes.DynamicallyGeneratedWindowNode;
import com.fluxtion.test.nodes.FailingWindowNodeFactory;
import com.fluxtion.test.nodes.KeyProcessorHistogram;
import com.fluxtion.test.nodes.WindowNode;
import com.fluxtion.test.nodes.WindowNodeFactory;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.Type;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class SimpleDeclarativeTest {

    @Test
    @Ignore
    public void testSimpleBuild() throws Exception {
        Set<Class<? extends NodeFactory>> class2Factory = NodeFactoryLocator.nodeFactorySet();
        Map<Class, String> rootNodeMappings = new HashMap<>();
        rootNodeMappings.put(KeyProcessorHistogram.class, "histogram");

        DeclarativeNodeConiguration config = new DeclarativeNodeConiguration(rootNodeMappings, class2Factory, new HashMap<>());

        TopologicallySortedDependecyGraph instance = new TopologicallySortedDependecyGraph(config);
        instance.generateDependencyTree();
    }

    @Test
    public void testGeneration() throws Exception {
        Set<Class<? extends NodeFactory>> class2Factory = NodeFactoryLocator.nodeFactorySet();
        Map<Class, String> rootNodeMappings = new HashMap<>();
//        rootNodeMappings.put(KeyProcessorHistogram.class, "histogram");
        rootNodeMappings.put(Calculator.class, "calculator");
        GenerationContext.setupStaticContext("com.fluxtion.test.template.java", "SimpleCalculator", new File("target/generated-test-sources/java/"), new File("target/generated-test-sources/resources/"));

        DeclarativeNodeConiguration config = new DeclarativeNodeConiguration(rootNodeMappings, class2Factory, new HashMap<>());
        SEPConfig cfg = new SEPConfig();
        cfg.templateFile = "javaTemplate.vsl";
        cfg.declarativeConfig = config;
        cfg.inlineEventHandling = true;
        cfg.generateDescription = false;

        Generator generator = new Generator();
        generator.templateSep(cfg);

    }

    @Test
    public void testFactoryOverride() throws Exception {
        //failing factory
        Set<Class<? extends NodeFactory>> class2Factory = new HashSet<>();
        class2Factory.add(FailingWindowNodeFactory.class);
        //Overrides failing factory
        Set<NodeFactory<?>> factorySet = new HashSet<>();
        factorySet.add(new WindowNodeFactory());
        Map<Class, String> rootNodeMappings = new HashMap<>();
        rootNodeMappings.put(WindowNode.class, "windowNode");

        DeclarativeNodeConiguration config = new DeclarativeNodeConiguration(rootNodeMappings, class2Factory, null, factorySet);

        TopologicallySortedDependecyGraph instance = new TopologicallySortedDependecyGraph(config);
        instance.generateDependencyTree();

        assertEquals(1, instance.getInstanceMap().size());
        assertEquals(1, instance.getSortedDependents().size());
        assertEquals(WindowNode.class, instance.getInstanceMap().keySet().toArray()[0].getClass());
    }

    @Test
    public void testFactoryInstanceInjection() throws Exception {
        //failing factory
        Set<Class<? extends NodeFactory>> class2Factory = new HashSet<>();
        //Overrides failing factory
        Set<NodeFactory<?>> factorySet = new HashSet<>();
        factorySet.add(new CalculatorRegisteringAccumulatorFactory());
        Map<Class, String> rootNodeMappings = new HashMap<>();
        rootNodeMappings.put(Calculator.class, "calcInjecting");

        DeclarativeNodeConiguration config = new DeclarativeNodeConiguration(rootNodeMappings, class2Factory, null, factorySet);

        TopologicallySortedDependecyGraph instance = new TopologicallySortedDependecyGraph(config);
        instance.generateDependencyTree();

        assertEquals(2, instance.getInstanceMap().size());
        assertEquals(2, instance.getSortedDependents().size());

        assertTrue(instance.getInstanceMap().containsValue("accumulator"));
        assertTrue(instance.getInstanceMap().containsValue("calcInjecting"));
    }

    @Test
    public void testClassOverride() throws Exception {
        //failing factory
        Set<Class<? extends NodeFactory>> class2Factory = new HashSet<>();
        class2Factory.add(FailingWindowNodeFactory.class);
        //Overrides failing factory
        Set<NodeFactory<?>> factorySet = new HashSet<>();
        factorySet.add(new WindowNodeFactory());
        Map<Class, String> rootNodeMappings = new HashMap<>();
        rootNodeMappings.put(WindowNode.class, "windowNode");

        DeclarativeNodeConiguration config = new DeclarativeNodeConiguration(rootNodeMappings, class2Factory, null, factorySet);

        TopologicallySortedDependecyGraph instance = new TopologicallySortedDependecyGraph(config);
        instance.generateDependencyTree();

        Object x = instance.getSortedDependents().get(0);
        Map classMap = new HashMap();
        classMap.put(x, "MyMadeUpClass");

        SimpleEventProcessorModel model = new SimpleEventProcessorModel(instance, null, classMap);
        model.generateMetaModel();

        Field mappedField = model.getFieldForInstance(x);
        assertEquals("MyMadeUpClass", mappedField.fqn);
    }

    @Test(expected = Exception.class)
    public void testFactoryFailsOverride() throws Exception {
        //failing factory
        Set<Class<? extends NodeFactory>> class2Factory = new HashSet<>();
        class2Factory.add(FailingWindowNodeFactory.class);
        //Overrides failing factory
        Set<NodeFactory<?>> factorySet = new HashSet<>();
        Map<Class, String> rootNodeMappings = new HashMap<>();
        rootNodeMappings.put(WindowNode.class, "windowNode");

        DeclarativeNodeConiguration config = new DeclarativeNodeConiguration(rootNodeMappings, class2Factory, null, factorySet);

        TopologicallySortedDependecyGraph instance = new TopologicallySortedDependecyGraph(config);
        instance.generateDependencyTree();

    }

    @Test
    public void testGenerateDerivedDataNode() throws Exception {
        //Build graph
        Set<NodeFactory<?>> factorySet = new HashSet<>();
        factorySet.add(new WindowNodeFactory());
        Map<Class, String> rootNodeMappings = new HashMap<>();
        rootNodeMappings.put(WindowNode.class, "windowNode");
        DeclarativeNodeConiguration config = new DeclarativeNodeConiguration(rootNodeMappings, null, null, factorySet);
        TopologicallySortedDependecyGraph instance = new TopologicallySortedDependecyGraph(config);
        instance.generateDependencyTree();
        //override the EindowNode class with DynamicallyGeneratedWindowNode
        Object x = instance.getSortedDependents().get(0);
        Map classMap = new HashMap();

        classMap.put(x, DynamicallyGeneratedWindowNode.class.getCanonicalName());
        //build model
        SimpleEventProcessorModel model = new SimpleEventProcessorModel(instance, null, classMap);
        model.generateMetaModel();
        //setup files, packages and class name for generated java
        String outputPackage = "com.fluxtion.test.derived.java";
        String className = "TestDerived";
        File outputDirectory = new File("target/generated-test-sources/java/");
        outputDirectory = new File(outputDirectory, outputPackage.replace(".", "/"));
        outputDirectory.mkdirs();
        String templateFile = "javaTemplate.vsl";
        //init velocity
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();
        //Build java source model
        SepJavaSourceModel srcModel = new SepJavaSourceModel(model, false);
        srcModel.buildSourceModel();
        //Generate java SEP 
        Template template = Velocity.getTemplate(templateFile);
        Context ctx = new VelocityContext();
        ctx.put("MODEL", srcModel);
        ctx.put("package", outputPackage);
        ctx.put("className", className);
        File outFile = new File(outputDirectory, className + ".java");
        FileWriter templateWriter = new FileWriter(outFile);
        template.merge(ctx, templateWriter);
        templateWriter.flush();
        //load source file
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(outputDirectory);
        //assert variable windowNode exists
        JavaClass genClass = builder.getClassByName(outputPackage + "." + className);
        JavaField rootField = genClass.getFieldByName("windowNode");
        assertNotNull(rootField);
        //assert the type is the replaced type
        Type actualType = rootField.getType();
        Type expecteType = new Type(DynamicallyGeneratedWindowNode.class.getName());
        assertEquals(expecteType, actualType);
    }

}
