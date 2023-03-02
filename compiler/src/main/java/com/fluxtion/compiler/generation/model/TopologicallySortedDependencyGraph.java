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
//      com.fluxtion.generation.model

import com.fluxtion.compiler.EventProcessorConfig;
import com.fluxtion.compiler.RootNodeConfig;
import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeFactoryRegistration;
import com.fluxtion.compiler.builder.factory.NodeNameProducer;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import com.fluxtion.compiler.generation.GenerationContext;
import com.fluxtion.compiler.generation.exporter.JgraphGraphMLExporter;
import com.fluxtion.compiler.generation.util.NaturalOrderComparator;
import com.fluxtion.runtime.annotations.AfterEvent;
import com.fluxtion.runtime.annotations.AfterTrigger;
import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnBatchEnd;
import com.fluxtion.runtime.annotations.OnBatchPause;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.PushReference;
import com.fluxtion.runtime.annotations.TearDown;
import com.fluxtion.runtime.annotations.TriggerEventOverride;
import com.fluxtion.runtime.annotations.builder.Config;
import com.fluxtion.runtime.annotations.builder.ConfigVariable;
import com.fluxtion.runtime.annotations.builder.ExcludeNode;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.annotations.builder.SepNode;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.node.Anchor;
import com.fluxtion.runtime.node.EventHandlerNode;
import com.fluxtion.runtime.partition.LambdaReflection.MethodReferenceReflection;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.googlecode.gentyref.GenericTypeReflector;
import net.vidageek.mirror.dsl.AccessorsController;
import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.reflect.dsl.ReflectionHandler;
import org.jgrapht.ext.IntegerEdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerConfigurationException;
import java.io.Writer;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Creates a sorted set of dependencies from a supplied set of instances.
 *
 * @author Greg Higgins
 */
public class TopologicallySortedDependencyGraph implements NodeRegistry {

    //TODO check there are no variable name clashes
    private final Logger LOGGER = LoggerFactory.getLogger(TopologicallySortedDependencyGraph.class);
    private final BiMap<Object, String> inst2NameTemp;
    private final SimpleDirectedGraph<Object, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
    private final SimpleDirectedGraph<Object, DefaultEdge> eventGraph = new SimpleDirectedGraph<>(DefaultEdge.class);
    private final Set<DefaultEdge> pushEdges = new HashSet<>();
    private final List<Object> topologicalHandlers = new ArrayList<>();
    private final List<Object> noPushTopologicalHandlers = new ArrayList<>();
    private final NodeFactoryRegistration nodeFactoryRegistration;
    private final HashMap<Class<?>, CbMethodHandle> class2FactoryMethod;
    private final HashMap<String, CbMethodHandle> name2FactoryMethod;
    private final List<Object> publicNodeList;
    private final GenerationContext generationContext;
    private final NodeNameProducer nameStrategy;
    private final EventProcessorConfig config;
    //TODO move this to constructor
    private Map<String, Auditor> registrationListenerMap;
    private BiMap<Object, String> inst2Name;
    private boolean processed = false;

    public TopologicallySortedDependencyGraph(Object... obj) {
        this(Arrays.asList(obj));
    }

    public TopologicallySortedDependencyGraph(List<?> nodes) {
        this(nodes, null, null, null, null, null);
    }

    public TopologicallySortedDependencyGraph(Map<Object, String> publicNodes) {
        this(null, publicNodes, null, null, null, null);
    }

    public TopologicallySortedDependencyGraph(EventProcessorConfig config) {
        this(config.getNodeList(),
                config.getPublicNodes(),
                config.getNodeFactoryRegistration(),
                GenerationContext.SINGLETON,
                config.getAuditorMap(),
                config);
    }

    /**
     * Create a new TopologicallySortedDependecyGraph
     *
     * @param nodes                   The set of nodes that will be sorted as a list.
     * @param publicNodes             Map of public available instances, the value is the
     *                                unique name of each instance. The names will override existing instances
     *                                in the nodes List or add the node to the set.
     * @param nodeFactoryRegistration factory description
     * @param context                 Generation context for this cycle
     * @param auditorMap              Auditors to inject
     * @param config                  Config for this generation cycle
     */
    public TopologicallySortedDependencyGraph(List<?> nodes, Map<Object, String> publicNodes,
                                              NodeFactoryRegistration nodeFactoryRegistration,
                                              GenerationContext context, Map<String, Auditor> auditorMap, EventProcessorConfig config) {
        this.config = config;
        this.nameStrategy = new NamingStrategy();
        this.inst2Name = HashBiMap.create();
        this.inst2NameTemp = HashBiMap.create();
        this.class2FactoryMethod = new HashMap<>();
        this.name2FactoryMethod = new HashMap<>();
        if (nodes == null) {
            nodes = Collections.EMPTY_LIST;
        }
        for (Object node : nodes) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("adding:'" + node + "' name:'" + nameNode(node) + "'");
            }
            inst2Name.put(node, nameNode(node));
        }
        //merge nodes from context 
        nodes = Collections.EMPTY_LIST;
        if (context != null && context.getNodeList() != null) {
            nodes = context.getNodeList();
        }
        addNodeList(nodes);
        if (config != null && config.getRootNodeConfig() != null) {
            addNodeList(config.getRootNodeConfig().getNodes());
        }

        //override node names
        publicNodeList = new ArrayList<>();
        if (context != null && context.getPublicNodes() != null) {
            inst2Name.putAll(context.getPublicNodes());
            publicNodeList.addAll(context.getPublicNodes().keySet());
        }
        if (publicNodes != null) {
            inst2Name.putAll(publicNodes);
            publicNodeList.addAll(publicNodes.keySet());
        }
        if (auditorMap == null) {
            auditorMap = new HashMap<>();
        }
        this.registrationListenerMap = auditorMap;
        registrationListenerMap.forEach((key, value) -> {
            inst2Name.put(value, key);
            publicNodeList.add(value);
        });
        //declarative nodes - add arguments to method and make defensive copy
        this.nodeFactoryRegistration = nodeFactoryRegistration;
        this.generationContext = context;
    }

    public static boolean trySetAccessible(Field field) {
        try {
            field.setAccessible(true);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    private void addNodeList(List<?> nodes) {
        if (nodes != null) {
            for (Object node : nodes) {
                if (inst2Name.containsKey(node)) {
                    continue;
                }
                String name = nameNode(node);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("from context adding:'" + node + "' name:'" + name + "'");
                }
                inst2Name.put(node, name);
            }
        }
    }

    /**
     * Accessor to the name mapping for an instance.
     *
     * @param node The instance to for this lookup.
     * @return the variable name of the instance.
     */
    public String variableName(Object node) {
        return inst2Name.get(node);
    }

    public Map<Object, String> getInstanceMap() {
        return Collections.unmodifiableMap(inst2Name);
    }

    public List<Object> getSortedDependents() throws Exception {
        generateDependencyTree();
        return Collections.unmodifiableList(topologicalHandlers);
    }

    public List<Object> getObjectSortedDependents() throws Exception {
        generateDependencyTree();
        return Collections.unmodifiableList(noPushTopologicalHandlers);
    }

    //TODO this should be a list that is sorted topologically and then
    // with natural order
    public Map<String, Auditor> getRegistrationListenerMap() {
        if (registrationListenerMap == null) {
            registrationListenerMap = new HashMap<>();
        }
        return Collections.unmodifiableMap(registrationListenerMap);
    }

    /**
     * @param obj The root object to search from in the graph.
     * @return a sorted dependents list with this object as a root.
     * @throws java.lang.Exception when generating graph
     */
    public List<Object> getSortedDependents(Object obj) throws Exception {
        generateDependencyTree();
        List<Integer> lst = new ArrayList<>();
        if (graph.containsVertex(obj)) {
            for (Iterator<Object> iterator = new DepthFirstIterator<>(graph, obj); iterator.hasNext(); ) {
                int idx = topologicalHandlers.indexOf(iterator.next());
                lst.add(idx);
            }
        }
        Collections.sort(lst);
        List<Object> cbList = new ArrayList<>();
        for (Integer idx : lst) {
            cbList.add(topologicalHandlers.get(idx));
        }
        return cbList;
    }

    public List<Object> getEventSortedDependents(Object obj) throws Exception {
        generateDependencyTree();
        List<Integer> lst = new ArrayList<>();
        if (eventGraph.containsVertex(obj)) {
            for (Iterator<Object> iter = new DepthFirstIterator<>(eventGraph, obj); iter.hasNext(); ) {
                int idx = topologicalHandlers.indexOf(iter.next());
                lst.add(idx);
            }
        }
        Collections.sort(lst);
        List<Object> cbList = new ArrayList<>();
        for (Integer idx : lst) {
            cbList.add(topologicalHandlers.get(idx));
        }
        return cbList;
    }

    /**
     * returns a list of the direct children of this object in the SEP.node in the SEP
     *
     * @param parent node in the SEP
     * @return list of direct children of this node
     */
    public List<?> getDirectChildren(Object parent) {
        ArrayList<Object> lst = new ArrayList<>();
        if (graph.containsVertex(parent)) {
            Set<DefaultEdge> outgoingEdgeSet = graph.outgoingEdgesOf(parent);
            for (DefaultEdge childEdge : outgoingEdgeSet) {
                lst.add(graph.getEdgeTarget(childEdge));
            }
        }
        return lst;
    }

    public List<?> getDirectChildrenListeningForEvent(Object parent) {
        ArrayList<Object> lst = new ArrayList<>();
        if (eventGraph.containsVertex(parent)) {
            Set<DefaultEdge> outgoingEdgeSet = eventGraph.outgoingEdgesOf(parent);
            for (DefaultEdge childEdge : outgoingEdgeSet) {
                lst.add(eventGraph.getEdgeTarget(childEdge));
            }
        }
        return lst;
    }

    /**
     * returns a list of the direct parents of this object in the SEP.
     *
     * @param child in the SEP
     * @return direct parents of this node
     */
    public List<?> getDirectParents(Object child) {
        ArrayList<Object> lst = new ArrayList<>();
        if (graph.containsVertex(child)) {
            Set<DefaultEdge> outgoingEdgeSet = graph.incomingEdgesOf(child);
            for (DefaultEdge parentEdge : outgoingEdgeSet) {
                lst.add(graph.getEdgeSource(parentEdge));
            }
        }
        return lst;
    }

    public List<?> getDirectParentsListeningForEvent(Object child) {
        ArrayList<Object> lst = new ArrayList<>();
        if (eventGraph.containsVertex(child)) {
            Set<DefaultEdge> outgoingEdgeSet = eventGraph.incomingEdgesOf(child);
            for (DefaultEdge parentEdge : outgoingEdgeSet) {
                lst.add(eventGraph.getEdgeSource(parentEdge));
            }
        }
        return lst;
    }

    @Override
    public <T> T registerPublicNode(T node, String variableName) {
        return registerNode(node, variableName, true);
    }

    public <T extends Auditor> T registerAuditor(T node, String auditorName) {
        T registerNode = registerNode(node, auditorName, true);
        registrationListenerMap.put(auditorName, registerNode);
        return registerNode;
    }

    @SuppressWarnings("unchecked")
    public <T> T registerNode(T node, String variableName, boolean isPublic) {
        if (variableName == null && inst2Name.containsKey(node)) {
            return (T) inst2Name.get(node);
        } else if (variableName == null) {
            variableName = nameNode(node);
        }
        if (inst2Name.containsValue(variableName) && !variableName.equals(inst2Name.get(node))) {
            throw new RuntimeException("Variable name:'" + variableName + "' "
                    + "already used for another node:'"
                    + inst2Name.inverse().get(variableName) + "', cannot add node:" + node);
        }
        if (inst2Name.containsKey(node) && !variableName.equals(inst2Name.get(node))) {
            throw new RuntimeException("Cannot remap node:" + node
                    + " to new variable name:'" + variableName + "' "
                    + " existing variable name:'" + inst2Name.get(node) + "'");

        }
        inst2Name.put(node, variableName);
        if (isPublic) {
            publicNodeList.add(node);
        }
        return node;
    }

    @Override
    public <T> T registerNode(T node, String variableName) {
        return registerNode(node, variableName, false);
    }

    @Override
    public <T> T findOrCreatePublicNode(Class<T> clazz, Map<String, Object> config, String variableName) {
        return findOrCreateNode(clazz, config, variableName, true);
    }

    @Override
    public <T> T findOrCreateNode(Class<T> clazz, Map<String, Object> config, String variableName) {
        return findOrCreateNode(clazz, config, variableName, false);
    }

    public <T> T findOrCreateNode(Class<T> clazz, Map<String, Object> config, String variableName, boolean isPublic) {
        return findOrCreateNode(clazz, config, variableName, isPublic, false, null);
    }

    @SuppressWarnings("unchecked")
    private <T> T findOrCreateNode(
            Class<T> clazz, Map<String, Object> config, String variableName, boolean isPublic, boolean useTempMap, String factoryName) {
        try {
            final CbMethodHandle handle;
            if (factoryName == null || factoryName.isEmpty()) {
                handle = class2FactoryMethod.get(clazz);
            } else {
                handle = name2FactoryMethod.get(factoryName);
                if (handle == null) {
                    throw new RuntimeException("No registered NodeFactory with name:'"
                            + factoryName + "' type:'"
                            + clazz.getCanonicalName() + "'");
                }
            }
            Object newNode;
            if (handle != null) {
                newNode = handle.method.invoke(handle.instance, config, this);
                if (newNode == null) {
                    return null;
                }
            } else {
                //try and build

                try {
                    newNode = clazz.getDeclaredConstructor().newInstance();
                } catch (IllegalAccessException | InstantiationException | NoSuchMethodException e) {
                    LOGGER.debug("missing default constructor - attempting construction bypass");
                    final net.vidageek.mirror.dsl.Mirror constructor = new Mirror();
                    newNode = constructor.on(clazz).invoke().constructor().bypasser();
                }
                AccessorsController mirror = new Mirror().on(newNode);
                ReflectionHandler<T> reflect = new Mirror().on(clazz).reflect();
                Set<Map.Entry<String, Object>> entrySet = config == null ? Collections.EMPTY_SET : config.entrySet().stream()
                        .filter(keyValue -> reflect.field(keyValue.getKey()) != null)
                        .collect(Collectors.toSet());
                //set all fields accessible
                ReflectionUtils.getFields(clazz).forEach(TopologicallySortedDependencyGraph::trySetAccessible);
                //set none string properties
                entrySet.stream().filter((Map.Entry<String, ?> keyValue) -> {
                            Field field = reflect.field(keyValue.getKey());
                            return field.getType() != String.class && keyValue.getValue().getClass() != String.class;
                        })
                        .forEach((Map.Entry<String, ?> map) -> mirror.set().field(map.getKey()).withValue(map.getValue()));
                //set where source and target are string
                entrySet.stream()
                        .filter(map -> reflect.field(map.getKey()).getType() == String.class
                                && map.getValue().getClass() == String.class)
                        .forEach(map -> mirror.set().field(map.getKey()).withValue(map.getValue()));
                entrySet.stream()
                        .filter(map -> reflect.field(map.getKey()).getType() != String.class
                                && map.getValue().getClass() == String.class)
                        .forEach(map -> {
                            Class<?> clazz1 = mirror.get().field(map.getKey()).getClass();

                            switch (clazz1.getSimpleName()) {
                                case "Integer":
                                    mirror.set().field(map.getKey()).withValue(Integer.valueOf((String) map.getValue()));
                                    break;
                                case "Double":
                                    mirror.set().field(map.getKey()).withValue(Double.valueOf((String) map.getValue()));
                                    break;
                                case "Float":
                                    mirror.set().field(map.getKey()).withValue(Float.valueOf((String) map.getValue()));
                                    break;
                                case "Short":
                                    mirror.set().field(map.getKey()).withValue(Short.valueOf((String) map.getValue()));
                                    break;
                                case "Byte":
                                    mirror.set().field(map.getKey()).withValue(Byte.valueOf((String) map.getValue()));
                                    break;
                                case "Long":
                                    mirror.set().field(map.getKey()).withValue(Long.valueOf((String) map.getValue()));
                                    break;
                                case "Character":
                                    mirror.set().field(map.getKey()).withValue(((String) map.getValue()).charAt(0));
                                    break;
                                default:
                                    throw new RuntimeException("Type not supported in default factory ");
                            }
                        });
            }
            if (!inst2Name.containsKey(newNode)) {
                String name = nameNode(newNode);
                if (useTempMap) {
                    inst2NameTemp.put(newNode, (variableName == null || isBlank(variableName)) ? name : variableName);
                } else {
                    inst2Name.put(newNode, (variableName == null || isBlank(variableName)) ? name : variableName);
                }
            } else {
                String name = inst2Name.get(newNode);
                newNode = inst2Name.inverse().get(name);
            }
            if (handle != null) {
                NodeFactory<T> factory = (NodeFactory<T>) handle.instance;
                if (isPublic) {
                    publicNodeList.add(newNode);
                }
                factory.postInstanceRegistration(config, this, (T) newNode);
            }
            return (T) newNode;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            //no recovery - log and throw runtime exception
            LOGGER.error("error creating node with factory", ex);
            throw new RuntimeException("error creating node with factory", ex);
        }
    }

    /**
     * Generates the dependency tree for introspection.
     *
     * @throws Exception when problem generating the dependency tree
     */
    public synchronized void generateDependencyTree() throws Exception {
        if (processed) {
            return;
        }

        if (nodeFactoryRegistration != null) {

            //store the factory callbacks
            for (Class<? extends NodeFactory<?>> clazz : nodeFactoryRegistration.factoryClassSet) {
                NodeFactory<?> factory = clazz.getDeclaredConstructor().newInstance();
                registerNodeFactory(factory);
            }
            //override any classes with pre-initialised NodeFactories
            for (NodeFactory<?> factory : nodeFactoryRegistration.factorySet) {
                registerNodeFactory(factory);
            }
            //loop through root instance and
            RootNodeConfig rootNodeConfig = config.getRootNodeConfig();
            if (rootNodeConfig != null && rootNodeConfig.getRootClass() != null) {
                Object newNode = findOrCreateNode(
                        rootNodeConfig.getRootClass(), rootNodeConfig.getConfig(), rootNodeConfig.getName());
                publicNodeList.add(newNode);
            }
        }
        //add injected instances created by factories
        addNodesFromContext();
        for (Map.Entry<Object, String> entry : inst2Name.entrySet()) {
            Object object = entry.getKey();
            if (Anchor.class.isAssignableFrom(object.getClass())) {
                Anchor anchor = (Anchor) object;
                graph.addVertex(anchor.getAnchor());
                graph.addVertex(anchor.getAfterAnchor());
                pushEdges.add(graph.addEdge(anchor.getAnchor(), anchor.getAfterAnchor()));
            } else {
                walkDependencies(object);
            }
        }
        inst2Name.putAll(inst2NameTemp);
        inst2Name.entrySet().removeIf(o -> Anchor.class.isAssignableFrom(o.getKey().getClass()));
        inst2Name.entrySet().removeIf(o -> o.getKey().getClass().isAnnotationPresent(ExcludeNode.class));

        //all instances are in inst2Name, can now generate final graph
        for (Map.Entry<Object, String> entry : inst2Name.entrySet()) {
            Object object = entry.getKey();
            walkDependencies(object);
        }

        //create a topological sortedset and put into list
//        PriorityQueue<Object> pq = new PriorityQueue<>(Math.max(1, inst2Name.size()), new NaturalOrderComparator<>(Collections.unmodifiableMap(inst2Name)));
        for (Iterator<Object> topologicalIter = new TopologicalOrderIterator<>(graph, new NaturalOrderComparator<>(Collections.unmodifiableMap(inst2Name)));
            //        for (Iterator topologicalIter = new TopologicalOrderIterator<>(graph);
             topologicalIter.hasNext(); ) {
            Object value = topologicalIter.next();
            if (value.getClass().isAnnotationPresent(ExcludeNode.class)) {
                graph.removeVertex(value);
                eventGraph.removeVertex(value);
            } else {
                topologicalHandlers.add(value);
            }
        }

        //if topologicalHandlers is missing nodes then add in a random order
        for (Map.Entry<Object, String> entry : inst2Name.entrySet()) {
            Object node = entry.getKey();
            if (!topologicalHandlers.contains(node)) {
                topologicalHandlers.add(node);
            }
        }

        buildNonPushSortedHandlers();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("GRAPH:" + graph);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SORTED LIST:" + topologicalHandlers);
        }
        processed = true;
    }

    @SuppressWarnings("unchecked")
    private void buildNonPushSortedHandlers() {
        SimpleDirectedGraph<Object, DefaultEdge> cloneGraph = (SimpleDirectedGraph<Object, DefaultEdge>) graph.clone();
        pushEdges.stream()
                .filter(Objects::nonNull)
                .forEach((DefaultEdge edge) -> {
                    Object edgeSource = graph.getEdgeSource(edge);
                    Object edgeTarget = graph.getEdgeTarget(edge);
                    cloneGraph.removeEdge(edgeSource, edgeTarget);
                    cloneGraph.addEdge(edgeTarget, edgeSource);
                });

        //create a topological sortedset and put into list
//        PriorityQueue<Object> pq = new PriorityQueue<>(Math.max(1, inst2Name.size()), new NaturalOrderComparator<>(Collections.unmodifiableMap(inst2Name)));
        for (Iterator<Object> topologicalIter = new TopologicalOrderIterator<>(cloneGraph, new NaturalOrderComparator<>(Collections.unmodifiableMap(inst2Name)));
            //        for (Iterator topologicalIter = new TopologicalOrderIterator<>(graph);
             topologicalIter.hasNext(); ) {
            Object value = topologicalIter.next();
            if (topologicalHandlers.contains(value)) {
                noPushTopologicalHandlers.add(value);
            }
        }

        //if topologicalHandlers is missing nodes then add in a random order
        for (Map.Entry<Object, String> entry : inst2Name.entrySet()) {
            Object node = entry.getKey();
            if (!noPushTopologicalHandlers.contains(node) && topologicalHandlers.contains(node)) {
                noPushTopologicalHandlers.add(node);
            }
        }
//        noPushTopologicalHandlers.removeIf(o -> o.getClass().getAnnotation(ExcludeNode.class) != null);
    }

    private void addNodesFromContext() {
        if (generationContext != null) {
            addNodeList(generationContext.getNodeList());
        }
    }

    private void registerNodeFactory(NodeFactory<?> obj) throws NoSuchMethodException, SecurityException {
        @SuppressWarnings("unchecked") Class<? extends NodeFactory<?>> clazz = (Class<? extends NodeFactory<?>>) obj.getClass();
        Method createMethod = clazz.getMethod("createNode", Map.class, NodeRegistry.class);
//        Type genericReturnType = createMethod.getGenericReturnType();
        final Class<?> targetClass;
        if (obj.injectionType() != null) {
            targetClass = obj.injectionType();
        } else {
            ParameterizedType paramType = (ParameterizedType) GenericTypeReflector.getExactSuperType(clazz, NodeFactory.class);
            targetClass = (Class<?>) paramType.getActualTypeArguments()[0];
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Registered factory:" + clazz.getCanonicalName() + " building:" + targetClass);
        }
        class2FactoryMethod.put(targetClass, new CbMethodHandle(createMethod, obj, "node_factory_" + targetClass.getName()));
        //
        if (obj.factoryName() != null && !obj.factoryName().isEmpty()) {
            name2FactoryMethod.put(obj.factoryName(),
                    new CbMethodHandle(createMethod, obj, "node_factory_" + targetClass.getName()));
        }
        Map<String, Auditor> auditorMap = new HashMap<>();
        obj.preSepGeneration(generationContext, auditorMap);
        auditorMap.forEach((key, value) -> registerAuditor(value, key));
        //set target language
    }

    private void walkDependenciesForEventHandling(Object object) throws IllegalArgumentException, IllegalAccessException {
        final Class<?> clazz = object.getClass();
        @SuppressWarnings("unchecked") Set<Field> s = ReflectionUtils.getAllFields(clazz);
        Field[] fields = new Field[s.size()];
        @SuppressWarnings("unchecked") boolean overrideEventTrigger = ReflectionUtils.getAllFields(clazz, ReflectionUtils.withAnnotation(TriggerEventOverride.class)).stream()
                .anyMatch(f -> {
                    try {
                        f.setAccessible(true);
                        return f.get(object) != null;
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    }
                });
        fields = s.toArray(fields);
        for (Field field : fields) {
            if (!trySetAccessible(field)) {
                continue;
            }
            Object refField = field.get(object);
            String refName = inst2Name.get(refField);
            if (refField != null && refField.equals(object)) {
                //no self reference loops
                break;
            }

            if (field.getAnnotation(NoTriggerReference.class) != null) {
                continue;
            }
            if (overrideEventTrigger && field.getAnnotation(TriggerEventOverride.class) == null) {
                continue;
            }
            if (field.getType().isArray()) {
                Object array = field.get(object);
                if (array == null) {
                    continue;
                }
                int length = Array.getLength(array);
                for (int i = 0; i < length; i++) {
                    refField = Array.get(array, i);
                    if (inst2Name.containsKey(refField)) {
                        eventGraph.addVertex(object);
                        eventGraph.addVertex(refField);
                        eventGraph.addEdge(refField, object);
                        walkDependenciesForEventHandling(refField);
                    }
                }
            } else if (List.class
                    .isAssignableFrom(field.getType())) {
                Collection<?> list = (Collection<?>) field.get(object);
                if (list == null) {
                    continue;
                }
                boolean pushCollection = field.getAnnotation(PushReference.class) != null;
                for (Object parent : list) {
                    if (inst2Name.containsKey(parent)) {
                        eventGraph.addVertex(object);
                        eventGraph.addVertex(parent);
                        if (pushCollection) {
                            eventGraph.addEdge(object, parent);
                        } else {
                            eventGraph.addEdge(parent, object);
                            walkDependenciesForEventHandling(parent);
                        }
                    }
                }
            } else if (refName != null) {
                if (handlesEvents(refField)) {
                    eventGraph.addVertex(object);
                    eventGraph.addVertex(refField);
                    if (field.getAnnotation(PushReference.class) != null) {
                        eventGraph.addEdge(object, refField);
                    } else {
                        eventGraph.addEdge(refField, object);
                        walkDependenciesForEventHandling(refField);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private String getInstanceName(Field field, Object node) throws IllegalArgumentException, IllegalAccessException {
        Object refField = field.get(node);
        String refName = inst2Name.get(refField);
        boolean addNode = field.getAnnotation(SepNode.class) != null
                && !field.getType().isArray()
                && !Collection.class.isAssignableFrom(field.getType());
        if (refField != null && field.getAnnotation(ExcludeNode.class) == null) {
            addNode |= !ReflectionUtils.getAllMethods(
                    refField.getClass(),
                    annotationPredicate()
            ).isEmpty();
            addNode |= EventHandlerNode.class.isAssignableFrom(refField.getClass())
                    | refField.getClass().getAnnotation(SepNode.class) != null;
        }
        if (refName == null && addNode && !inst2NameTemp.containsKey(refField) && refField != null) {
            LOGGER.debug("cannot find node in supplied list, but has SepNode annotation adding to managed node list");
            refName = nameNode(refField);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("from @SepNode adding:'" + refField + "' name:'" + refName + "'");
            }
            inst2NameTemp.put(refField, refName);
            walkDependencies(refField);
        }
        return refName;
    }

    @SuppressWarnings("unchecked")
    private void implicitAddVectorMember(Object refField, Field collection) {
        boolean addNode;
        if (refField != null && !inst2Name.containsKey(refField) && !inst2NameTemp.containsKey(refField)) {
            addNode = !ReflectionUtils.getAllMethods(
                    refField.getClass(),
                    annotationPredicate()
            ).isEmpty();
            addNode |= EventHandlerNode.class.isAssignableFrom(refField.getClass())
                    | refField.getClass().getAnnotation(SepNode.class) != null;
            if (addNode | collection.getAnnotation(SepNode.class) != null) {
                inst2NameTemp.put(refField, nameNode(refField));
            }
        }
    }

    private Predicate<AnnotatedElement> annotationPredicate() {
        return ReflectionUtils.withAnnotation(AfterEvent.class)
                .or(ReflectionUtils.withAnnotation(OnEventHandler.class))
                .or(ReflectionUtils.withAnnotation(Inject.class))
                .or(ReflectionUtils.withAnnotation(OnBatchEnd.class))
                .or(ReflectionUtils.withAnnotation(OnBatchPause.class))
                .or(ReflectionUtils.withAnnotation(OnTrigger.class))
                .or(ReflectionUtils.withAnnotation(AfterTrigger.class))
                .or(ReflectionUtils.withAnnotation(OnParentUpdate.class))
                .or(ReflectionUtils.withAnnotation(TearDown.class))
                .or(ReflectionUtils.withAnnotation(Initialise.class))
                .or(ReflectionUtils.withAnnotation(TriggerEventOverride.class))
                ;
    }

    private Predicate<AnnotatedElement> eventHandlingAnnotationPredicate() {
        return ReflectionUtils.withAnnotation(OnEventHandler.class)
                .or(ReflectionUtils.withAnnotation(OnTrigger.class))
                .or(ReflectionUtils.withAnnotation(TriggerEventOverride.class))
                ;
    }

    private boolean handlesEvents(Object obj) {
        return EventHandlerNode.class.isAssignableFrom(obj.getClass())
                || !ReflectionUtils.getAllMethods(obj.getClass(), eventHandlingAnnotationPredicate()).isEmpty();
    }

    private void walkDependencies(Object object) throws IllegalArgumentException, IllegalAccessException {
        walkDependenciesForEventHandling(object);
        @SuppressWarnings("unchecked") Set<Field> s = ReflectionUtils.getAllFields(object.getClass());
        Field[] fields = new Field[s.size()];
        fields = s.toArray(fields);
        for (Field field : fields) {
            if (!trySetAccessible(field) || Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            Object refField = field.get(object);
            if (inst2Name.containsKey(refField) && refField != object) {
                refField = inst2Name.inverse().get(inst2Name.get(refField));
                try {
                    field.set(object, refField);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    //throw new RuntimeException(e);
                }
            }

            String refName = getInstanceName(field, object);
            if (refField != null && refField.equals(object)) {
                //no self reference loops
                break;
            }
            if (field.getType().isArray()) {
                Object array = field.get(object);
                if (array == null) {
                    continue;
                }
                int length = Array.getLength(array);
                for (int i = 0; i < length; i++) {
                    refField = Array.get(array, i);
                    implicitAddVectorMember(refField, field);
                    if (inst2Name.containsKey(refField) || inst2NameTemp.containsKey(refField)) {
                        graph.addVertex(object);
                        graph.addVertex(refField);
                        graph.addEdge(refField, object);
                        walkDependencies(refField);
                    } else if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("mismatch for:" + refField);
                        for (Object obj : inst2Name.keySet()) {
                            LOGGER.debug(obj + "==" + refField + " " + (obj == refField));
                            if (obj != refField) {
                                LOGGER.debug("obj.equals(refField)" + obj.equals(refField));
                                LOGGER.debug("match value from map refField:" + inst2Name.get(refField));
                                LOGGER.debug("match value from map obj:" + inst2Name.get(obj));
                            }
                        }
                    }
                }
            } else if (Collection.class.isAssignableFrom(field.getType())) {
                Collection<?> list = (Collection<?>) field.get(object);
                if (list == null) {
                    continue;
                }
                boolean pushCollection = field.getAnnotation(PushReference.class) != null;
                for (Object parent : list) {
                    implicitAddVectorMember(parent, field);
                    if (inst2Name.containsKey(parent) || inst2NameTemp.containsKey(parent)) {
                        graph.addVertex(object);
                        graph.addVertex(parent);

                        if (pushCollection) {
                            pushEdges.add(graph.addEdge(object, parent));
                        } else {
                            graph.addEdge(parent, object);
                            walkDependencies(parent);
                        }
                    }
                }
            } else if (refName != null) {
                graph.addVertex(object);
                graph.addVertex(refField);

                if (field.getAnnotation(PushReference.class) != null) {
                    pushEdges.add(graph.addEdge(object, refField));
                } else if (Anchor.class.isAssignableFrom(refField.getClass())) {
                    System.out.println("ANCHOR!!!");
                } else {
                    graph.addEdge(refField, object);
                    walkDependencies(refField);
                }

            } else if (refName == null
                    && refField != null
                    && MethodReferenceReflection.class.isAssignableFrom(refField.getClass())
                    && ((MethodReferenceReflection) refField).captured().length > 0
            ) {
                Object methodInstanceHolder = ((MethodReferenceReflection) refField).captured()[0];
                String instanceName = inst2Name.getOrDefault(methodInstanceHolder, nameNode(methodInstanceHolder));
                inst2NameTemp.put(methodInstanceHolder, instanceName);
                graph.addVertex(methodInstanceHolder);
                graph.addVertex(object);
                if (field.getAnnotation(PushReference.class) != null) {
                    pushEdges.add(graph.addEdge(object, methodInstanceHolder));
                    if (field.getAnnotation(NoTriggerReference.class) == null) {
                        eventGraph.addVertex(methodInstanceHolder);
                        eventGraph.addVertex(object);
                        eventGraph.addEdge(object, methodInstanceHolder);
                    }
                } else {
                    graph.addEdge(methodInstanceHolder, object);
                    if (field.getAnnotation(NoTriggerReference.class) == null && handlesEvents(methodInstanceHolder)) {
                        eventGraph.addVertex(methodInstanceHolder);
                        eventGraph.addVertex(object);
                        eventGraph.addEdge(methodInstanceHolder, object);
                    }
                }
                walkDependencies(methodInstanceHolder);
            }
            //check inject annotation for field
            Inject injecting = field.getAnnotation(Inject.class);
            if (injecting != null & refName == null & field.get(object) == null) {
                String factoryVariableName = injecting.factoryVariableName();
                String factoryName = injecting.factoryName();
                Set<java.lang.reflect.Field> fieldNames = ReflectionUtils.getAllFields(object.getClass(), ReflectionUtils.withName(factoryVariableName));
                if (factoryVariableName.length() > 0 && fieldNames.size() > 0) {
                    java.lang.reflect.Field f = fieldNames.iterator().next();
                    f.setAccessible(true);
                    if (f.get(object) != null) {
                        if (f.getType().equals(String.class)) {
                            factoryName = (String) f.get(object);
                        } else {
                            throw new IllegalArgumentException(
                                    "Inject.factoryVariableName() should be the variable name of a String field: " + f);
                        }
                    }
                }
                HashMap<String, Object> map = new HashMap<>();
                HashMap<String, Object> overrideMap = new HashMap<>();

                ConfigVariable[] overrideConfigs = field.getAnnotationsByType(ConfigVariable.class);
                for (ConfigVariable overrideConfig : overrideConfigs) {
                    String fieldFilter = overrideConfig.field();
                    String key = overrideConfig.key();
                    Object value = new Mirror().on(object).get().field(fieldFilter);
                    overrideMap.put(key, value);
                }
                //inject config from annotations over global
                Config[] configArray = field.getAnnotationsByType(Config.class);
                for (Config config : configArray) {
                    map.put(config.key(), config.value());
                }
                //inject config from variables over global + annotation
                Set<Map.Entry<String, Object>> entrySet = overrideMap.entrySet();
                entrySet.forEach((overrideEntry) -> map.put(overrideEntry.getKey(), overrideEntry.getValue()));
                map.put(NodeFactory.FIELD_KEY, field);
                //merge configs to single map
                //a hack to get inject working - this needs to be re-factored!!
                BiMap<Object, String> oldMap = inst2Name;
                inst2Name = inst2NameTemp;
                Object newNode = null;
                if (injecting.singleton()) {
                    newNode = inst2Name.keySet().stream()
                            .filter(o -> o.getClass() == field.getType())
                            .findFirst().orElse(null);

                }
                if (newNode == null) {
                    newNode = findOrCreateNode(field.getType(), map, injecting.singletonName(), false, true, factoryName);
                }
                inst2Name = oldMap;
                addNodesFromContext();
                field.set(object, newNode);
                //walkDependencies for any injected node
                //otherwise we will not add nodes dependencies created
                //by the child node
                walkDependencies(newNode);
            }
        }
    }

    public boolean isPublicNode(Object node) {
        return publicNodeList.contains(node);
    }

    public EventProcessorConfig getConfig() {
        return config;
    }

    void sortNodeList(List<CbMethodHandle> dispatchMethods) {
        dispatchMethods.sort((CbMethodHandle handle0, CbMethodHandle handle1) -> {
            if (handle0.instance == handle1.instance) {
                if (handle0.isEventHandler && !handle1.isEventHandler) {
                    return -1;
                } else if (!handle0.isEventHandler && handle1.isEventHandler) {
                    return +1;
                } else {
                    return handle0.method.getName().compareTo(handle1.method.getName());
                }
            }
            return (topologicalHandlers.indexOf(handle0.instance) - topologicalHandlers.indexOf(handle1.instance));
        });
    }

    /**
     * exports graph as graphml, can be exported with and without event as nodes
     * on the graph.
     *
     * @param writer    target
     * @param addEvents flag to control inclusion of events as nodes
     * @throws SAXException                      problem writing jpgraphMl
     * @throws TransformerConfigurationException problem writing jpgraphMl
     */
    public void exportAsGraphMl(Writer writer, boolean addEvents) throws SAXException, TransformerConfigurationException {
        //graphml representation
        VertexNameProvider<Object> np = vertex -> {
            String name = variableName(vertex);
            if (name == null) {
                name = ((Class<?>) vertex).getSimpleName();
            }
            return name;
        };
        JgraphGraphMLExporter<Object, Object> mlExporter = new JgraphGraphMLExporter<>(np, np,
                new IntegerEdgeNameProvider<>(), new IntegerEdgeNameProvider<>());
        @SuppressWarnings("unchecked") SimpleDirectedGraph<Object, Object> exportGraph = (SimpleDirectedGraph<Object, Object>) graph.clone();
        if (addEvents) {
            graph.vertexSet().forEach((t) -> {
                Method[] methodList = t.getClass().getMethods();
                for (Method method : methodList) {
                    if (method.getAnnotation(OnEventHandler.class) != null) {
                        @SuppressWarnings("unchecked") Class<? extends Event> eventTypeClass = (Class<? extends Event>) method.getParameterTypes()[0];
                        exportGraph.addVertex(eventTypeClass);
                        exportGraph.addEdge(eventTypeClass, t);
                    }
                }
                if (t instanceof EventHandlerNode) {
                    EventHandlerNode<?> eh = (EventHandlerNode<?>) t;
                    Class<?> eventClass = eh.eventClass();
                    if (eventClass != null) {
                        exportGraph.addVertex(eventClass);
                        exportGraph.addEdge(eventClass, t);
                    }
                }
            });

//            pushEdges.stream()
//                    .filter(Objects::nonNull)
//                    .forEach((DefaultEdge edge) -> {
//                        Object edgeSource = graph.getEdgeSource(edge);
//                        Object edgeTarget = graph.getEdgeTarget(edge);
//                        exportGraph.removeEdge(edgeSource, edgeTarget);
//                        exportGraph.addEdge(edgeTarget, edgeSource);
//                    });

        }
        mlExporter.export(writer, exportGraph);//new EdgeReversedGraph(graph));
    }

    private String nameNode(Object node) {
        return nameStrategy.mappedNodeName(node);
    }

}
