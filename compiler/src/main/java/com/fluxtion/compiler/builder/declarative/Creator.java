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
package com.fluxtion.compiler.builder.declarative;

import com.fluxtion.compiler.SEPConfig;
import com.fluxtion.compiler.builder.generation.GenerationContext;
import com.fluxtion.compiler.generation.compiler.classcompiler.StringCompilation;
import com.fluxtion.runtime.annotations.*;
import com.fluxtion.runtime.event.DefaultEvent;
import com.squareup.javapoet.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.ClassUtils;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A Creator processes a meta-model to produce a code solution for the Fluxtion
 * generator to consume. The Creator is intended as a bridge between CASE tools
 * and Fluxtion utility.<p>
 *
 * The creator is invoked one step before the generator and produces a code
 * input to the Fluxtion generator. Outputs will include:
 * <ul>
 * <li>Events that are not discoverable by Fluxtion
 * <li>Nodes that are not discoverable by Fluxtion
 * <li>
 * </ul>
 *
 * @author gregp
 */
public class Creator {

    private final SimpleDirectedGraph<Node, FieldEdge> graph = new SimpleDirectedGraph<>(FieldEdge.class);
    private CreatorConfig config;
    private Map<String, Node> id2NodeMap;
    private Map<String, EventDefinition> id2EventMap;
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Creator.class);

    public Class createModel(CreatorConfig config) throws Exception {
        this.config = config;
        buildGraph();
        generateAndCompileEvents();
        generateAndCompileNodes();
        return generateAndCompileSepConfig();
    }

    private void buildGraph() {
        LOG.debug("building graph");
        //store nodes
        id2NodeMap = config.getNodes().stream().collect(Collectors.toMap(Node::getId, (t) -> t));
        id2EventMap = config.getEvents().stream().collect(Collectors.toMap(EventDefinition::getId, (t) -> t));
        config.getNodes().stream().forEach(graph::addVertex);
        //build graph
        config.getNodes().stream().forEach((Node n) -> {
            n.getNodes().forEach((ReferenceDefinition ref) -> {
                FieldEdge field = new FieldEdge(ref);
                LOG.debug("adding edge {}", field);
                graph.addEdge(n, id2NodeMap.get(ref.getNode()), field);
            });
        });
    }

    private void generateAndCompileEvents() {
        config.getEvents().stream().forEach(this::createAndCompileEvent);
    }

    private void generateAndCompileNodes() {
        LOG.debug("generating nodes");
        graph.vertexSet().stream().filter(this::isNotCompiled).forEach((node) -> {
            try {
                LOG.debug("building node {}", node);
                final String className = node.getClassName();
                final String fqn = node.getType();
                final TypeSpec.Builder nodeBuilder = TypeSpec.classBuilder(className)
                        .addModifiers(Modifier.FINAL, Modifier.PUBLIC);
                //build class
                graph.outgoingEdgesOf(node).stream().forEach((FieldEdge f) -> {
                    String name = f.getFieldName();
                    ClassName type = ClassName.get(f.getPackageName(), f.getClassName());
                    FieldSpec android = FieldSpec.builder(type, name)
                            .addModifiers(Modifier.PUBLIC)
                            .build();
                    nodeBuilder.addField(android);
                    //add parent listener
                    MethodSpec handler = MethodSpec.methodBuilder("parentUpdate_" + name)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(boolean.class)
                            .addParameter(type, "parent")
                            .addStatement("return true")
                            .addAnnotation(AnnotationSpec.builder(OnParentUpdate.class)
                                    .addMember("value", "$S", name)
                                    .build())
                            .build();
                    nodeBuilder.addMethod(handler);

                });
                //add on event
                if (graph.outgoingEdgesOf(node).size() > 0) {
                    MethodSpec handler = MethodSpec.methodBuilder("onEvent")
                            .addModifiers(Modifier.PUBLIC)
                            .returns(boolean.class)
                            .addStatement("return true")
                            .addAnnotation(AnnotationSpec.builder(OnEvent.class)
                                    .build())
                            .build();
                    nodeBuilder.addMethod(handler);
                }

                //event handlers
                node.getEvents().stream().forEach((EventMethod e) -> {
                    EventDefinition t = id2EventMap.get(e.getEventId());
                    AnnotationSpec.Builder annoBuilder = AnnotationSpec.builder(EventHandler.class)
                            .addMember("propagate", e.isPropagate() + "");
                    if (e.getFilter() != null) {
                        annoBuilder.addMember("filterId", e.getFilter());
                    }

                    ClassName type = ClassName.get(t.getPackageName(), t.getClassName());
                    MethodSpec handler = MethodSpec.methodBuilder("handler" + t.getClassName())
                            .addModifiers(Modifier.PUBLIC)
                            .returns(boolean.class)
                            .addParameter(type, "event")
                            .addStatement("return true")
                            .addAnnotation(annoBuilder.build())
                            .build();
                    nodeBuilder.addMethod(handler);
                });

                //init an teardown
                MethodSpec initHandler = MethodSpec.methodBuilder("init")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(AnnotationSpec.builder(Initialise.class)
                                .build())
                        .build();
                nodeBuilder.addMethod(initHandler);
                //teardonw
                MethodSpec handler = MethodSpec.methodBuilder("teardown")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(AnnotationSpec.builder(TearDown.class)
                                .build())
                        .build();
                nodeBuilder.addMethod(handler);

                TypeSpec nodeClass = nodeBuilder.build();
                JavaFile javaFile = addLicense(
                        JavaFile.builder(GenerationContext.SINGLETON.getPackageName(), nodeClass))
                        .build();
                javaFile.writeTo(GenerationContext.SINGLETON.getSourceRootDirectory());
            } catch (IOException ex) {
                Logger.getLogger(Creator.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        compileNodes();
    }

    private JavaFile.Builder addLicense(JavaFile.Builder builder) {
        builder.
                addFileComment("Copyright (C) 2018 V12 Technology Ltd.\n"
                        + "\n"
                        + "This program is free software: you can redistribute it and/or modify\n"
                        + "it under the terms of the Server Side Public License, version 1,\n"
                        + "as published by MongoDB, Inc.\n"
                        + "\n"
                        + "This program is distributed in the hope that it will be useful,\n"
                        + "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
                        + "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
                        + "Server Side License for more details.\n"
                        + "\n"
                        + "You should have received a copy of the Server Side Public License\n"
                        + "along with this program.  If not, see \n"
                        + "<http://www.mongodb.com/licensing/server-side-public-license>.");
        return builder;
    }

    private void compileNodes() {
        LOG.debug("compiling nodes");
        ArrayList<Node> nodes = new ArrayList<>();
        TopologicalOrderIterator<Node, FieldEdge> topIter = new TopologicalOrderIterator(graph);
        while (topIter.hasNext()) {
            nodes.add(topIter.next());
        }
        Collections.reverse(nodes);
        nodes.stream().filter(this::isNotCompiled).forEach((node) -> {
            LOG.debug("compiling node id:{}", node.getId());
            compile(node.getType());
        });
    }

    private void createAndCompileEvent(EventDefinition eventDefinition) {
        final String fqn = eventDefinition.getType();
        if (!loadClass(fqn).isPresent()) {
            LOG.debug("compiling event:{}", fqn);
            try {
                final String className = eventDefinition.getClassName();
                final TypeSpec.Builder nodeBuilder = TypeSpec.classBuilder(className)
                        .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                        .superclass(ClassName.get(DefaultEvent.class));
                TypeSpec nodeClass = nodeBuilder.build();
//                JavaFile javaFile = 
//                        JavaFile.builder(GenerationContext.SINGLETON.getPackageName(), nodeClass)
//                        .build();
                JavaFile javaFile = addLicense(
                        JavaFile.builder(GenerationContext.SINGLETON.getPackageName(), nodeClass))
                        .build();
                javaFile.writeTo(GenerationContext.SINGLETON.getSourceRootDirectory());
                compile(fqn);
            } catch (IOException ex) {
                Logger.getLogger(Creator.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            LOG.debug("re-using event:{}", fqn);
        }
    }

    private boolean isNotCompiled(Node node) {
        return !isCompiled(node);
    }

    private boolean isCompiled(Node node) {
        final boolean compiled = node.isFactoryCreated() || loadClass(node.getType()).isPresent();
        if (!compiled) {
            LOG.debug("un-compiled node id:{}", node.getId());
        }
        return compiled;
    }

    private Optional<Class> loadClass(String fqn) {
        Optional<Class> clazz = Optional.empty();
        try {
            clazz = Optional.ofNullable(GenerationContext.SINGLETON.getClassLoader().loadClass(fqn));
        } catch (ClassNotFoundException ex) {
            //Logger.getLogger(Creator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return clazz;
    }

    private Class compile(String fqn) {
        try {
            File file = new File(GenerationContext.SINGLETON.getPackageDirectory(), ClassUtils.getShortClassName(fqn) + ".java");
            String javaCode = GenerationContext.readText(file.getCanonicalPath());
            return StringCompilation.compile(fqn, javaCode);
        } catch (IOException | ClassNotFoundException | URISyntaxException ex) {
            Logger.getLogger(Creator.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("cannot generate creator class", ex);
        }
    }

    private Class generateAndCompileSepConfig() throws Exception {
        LOG.debug("generate and compile SEPConfig");
        final String className = config.getSepCfgShortClassName();
        final String packageName = config.getSepCfgPackageName();
        final TypeSpec.Builder nodeBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .superclass(ClassName.get(SEPConfig.class));

        //override a method
        //init an teardown "buildConfig"
        MethodSpec.Builder initHandler = MethodSpec.methodBuilder("buildConfig")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Override.class)
                        .build());
        initHandler.addComment("creating node instance");
        //add definitions
        graph.vertexSet().stream().forEach((node) -> {
            try {
                if (node.isFactoryCreated()) {
                    try {
                        SepConfigGenerator gen = (SepConfigGenerator) Class.forName(node.getFactoryType()).newInstance();
                        initHandler.addStatement("$1T $2L", node.getNodeClass(), node.getId());
                        String code = gen.sepConfigStatement(node.getConfigBean(), node.getId(), null);
                        initHandler.addCode(code);
                        if (node.isPublicAccess()) {
                            initHandler.addStatement("addPublicNode($1L, $1S)",
                                    node.getId());
                        } else {
                            initHandler.addStatement("addNode($1L)",
                                    node.getId());
                        }
                    } catch (InstantiationException | IllegalAccessException ex) {
                        throw new RuntimeException("problem generating SEP config", ex);
                    }
                } else if (node.isPublicAccess()) {
                    initHandler.addStatement("$1T $2L = addPublicNode(new $1T(), $2S)",
                            node.getNodeClass(), node.getId());

                } else {

                    initHandler.addStatement("$1T $2L = addNode(new $1T())",
                            node.getNodeClass(), node.getId());
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Creator.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        //refs
        initHandler.addComment("setting node reference");
        graph.edgeSet().stream().forEach((FieldEdge field) -> {
            String src = graph.getEdgeSource(field).getId();
            String target = graph.getEdgeTarget(field).getId();
            String id = field.getFieldName();
            initHandler.addStatement("$1L.$2L = $3L", src, id, target);
        });
        //auditor
        if (config.getAuditorClass() != null) {
            initHandler.addComment("adding auditor");
            Class clazz = Class.forName(config.getAuditorClass());
            initHandler.addStatement("addAuditor(new $T(), \"auditor\") ", clazz);
        }
        nodeBuilder.addMethod(initHandler.build());

        TypeSpec nodeClass = nodeBuilder.build();
        JavaFile javaFile = addLicense(JavaFile.builder(packageName, nodeClass))
                //                .
                .build();
        javaFile.writeTo(GenerationContext.SINGLETON.getSourceRootDirectory());
        return compile(config.getOutputSepConfigClass());
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public class FieldEdge extends DefaultEdge {

        public ReferenceDefinition ref;
        public Node refNode;

        public FieldEdge(ReferenceDefinition ref) {
            this.ref = ref;
            this.refNode = id2NodeMap.get(ref.getNode());
        }

        public String getFieldName() {
            return ref.getName();
        }

        public String getFqn() {
            return refNode.getType();
        }

        public String getClassName() {
            return refNode.getClassName();
        }

        public String getPackageName() {
            return refNode.getPackageName();
        }
    }

}
