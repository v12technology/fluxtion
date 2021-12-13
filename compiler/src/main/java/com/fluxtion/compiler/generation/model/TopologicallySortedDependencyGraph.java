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

import com.fluxtion.compiler.builder.generation.GenerationContext;
import com.fluxtion.compiler.builder.generation.NodeNameProducer;
import com.fluxtion.compiler.builder.node.DeclarativeNodeConiguration;
import com.fluxtion.compiler.builder.node.NodeFactory;
import com.fluxtion.compiler.builder.node.NodeRegistry;
import com.fluxtion.compiler.builder.node.SEPConfig;
import com.fluxtion.compiler.generation.exporter.JgraphGraphMLExporter;
import com.fluxtion.compiler.generation.util.NaturalOrderComparator;
import com.fluxtion.runtim.FilteredEventHandler;
import com.fluxtion.runtim.annotations.*;
import com.fluxtion.runtim.annotations.builder.*;
import com.fluxtion.runtim.audit.Auditor;
import com.fluxtion.runtim.event.Event;
import com.google.common.base.Predicates;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.googlecode.gentyref.GenericTypeReflector;
import net.vidageek.mirror.dsl.AccessorsController;
import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.reflect.dsl.ReflectionHandler;
import org.jgrapht.DirectedGraph;
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
import java.lang.reflect.Field;
import java.lang.reflect.*;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.reflections.ReflectionUtils.getAllFields;
import static org.reflections.ReflectionUtils.withAnnotation;

/**
 * Creates a sorted set of dependencies from a supplied set of instances.
 *
 * @author Greg Higgins
 */
public class TopologicallySortedDependencyGraph implements NodeRegistry {

    //TODO move this to constructor
    private Map<String, Auditor> registrationListenerMap;

    //TODO check there are no variable name clashes
    private final Logger LOGGER = LoggerFactory.getLogger(TopologicallySortedDependencyGraph.class);
    private BiMap<Object, String> inst2Name;
    private final BiMap<Object, String> inst2NameTemp;
    private final SimpleDirectedGraph<Object, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
    private final DirectedGraph<Object, DefaultEdge> eventGraph = new SimpleDirectedGraph<>(DefaultEdge.class);
    private final Set<DefaultEdge> pushEdges = new HashSet<>();
    private final List<Object> topologicalHandlers = new ArrayList<>();
    private final List<Object> noPushTopologicalHandlers = new ArrayList<>();
    private boolean processed = false;
    private final DeclarativeNodeConiguration declarativeNodeConiguration;
    private final HashMap<Class<?>, CbMethodHandle> class2FactoryMethod;
    private final List<Object> publicNodeList;
    private final GenerationContext generationContext;
    private final NodeNameProducer nameStrategy;
    private final SEPConfig config;

    public TopologicallySortedDependencyGraph(Object... obj) {
        this(Arrays.asList(obj));
    }

    public TopologicallySortedDependencyGraph(List<?> nodes) {
        this(nodes, null, null, null, null, null);
    }

    public TopologicallySortedDependencyGraph(Map<Object, String> publicNodes) {
        this(null, publicNodes, null, null, null, null);
    }

    public TopologicallySortedDependencyGraph(DeclarativeNodeConiguration declarativeNodeConiguration) {
        this(null, null, declarativeNodeConiguration, null, null, null);
    }

    public TopologicallySortedDependencyGraph(List<?> nodes, Map<Object, String> publicNodes) {
        this(nodes, publicNodes, null, null, null, null);
    }

    public TopologicallySortedDependencyGraph(SEPConfig config) {
        this(config.nodeList,
                config.publicNodes,
                config.declarativeConfig,
                GenerationContext.SINGLETON,
                config.auditorMap,
                config);
    }

    /**
     * Create a new TopologicallySortedDependecyGraph
     *
     * @param nodes The set of nodes that will be sorted as a list.
     * @param publicNodes Map of public available instances, the value is the
     * unique name of each instance. The names will override existing instances
     * in the nodes List or add the node to the set.
     * @param declarativeNodeConiguration factory description
     * @param context Generation context for this cycle
     * @param auditorMap Auditors to inject
     * @param config Config for this generation cycle
     *
     */
    public TopologicallySortedDependencyGraph(List<?> nodes, Map<Object, String> publicNodes,
                                              DeclarativeNodeConiguration declarativeNodeConiguration,
                                              GenerationContext context, Map<String, Auditor> auditorMap, SEPConfig config) {
        this.config = config;
        this.nameStrategy = new NamingStrategy();
        this.inst2Name = HashBiMap.create();
        this.inst2NameTemp = HashBiMap.create();
        this.class2FactoryMethod = new HashMap<>();
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
        this.declarativeNodeConiguration = declarativeNodeConiguration;
        this.generationContext = context;
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
     *
     * @param obj The root object to search from in the graph.
     *
     * @return a sorted dependents list with this object as a root.
     * @throws java.lang.Exception when generating graph
     *
     */
    public List<Object> getSortedDependents(Object obj) throws Exception {
        generateDependencyTree();
        List<Integer> lst = new ArrayList<>();
        if (graph.containsVertex(obj)) {
            for (Iterator<Object> iterator = new DepthFirstIterator<>(graph, obj); iterator.hasNext();) {
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
            for (Iterator<Object> iter = new DepthFirstIterator<>(eventGraph, obj); iter.hasNext();) {
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
    public <T> T findOrCreatePublicNode(Class<T> clazz, Map<?,?> config, String variableName) {
        return findOrCreateNode(clazz, config, variableName, true);
    }

    @Override
    public <T> T findOrCreateNode(Class<T> clazz, Map<?,?> config, String variableName) {
        return findOrCreateNode(clazz, config, variableName, false);
    }

    public <T> T findOrCreateNode(Class<T> clazz, Map<?,?> config, String variableName, boolean isPublic) {
        return findOrCreateNode(clazz, config, variableName, isPublic, false);
    }

    @SuppressWarnings("unchecked")
    private <T> T findOrCreateNode(Class<T> clazz, Map<?,?> config, String variableName, boolean isPublic, boolean useTempMap) {
        try {
            CbMethodHandle handle = class2FactoryMethod.get(clazz);
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
                } catch (IllegalAccessException | InstantiationException | NoSuchMethodException  e) {
                    LOGGER.debug("missing default constructor - attempting construction bypass");
                    final net.vidageek.mirror.dsl.Mirror constructor = new Mirror();
                    newNode = constructor.on(clazz).invoke().constructor().bypasser();
                }

                AccessorsController mirror = new Mirror().on(newNode);
                ReflectionHandler<T> reflect = new Mirror().on(clazz).reflect();
                Set<? extends Map.Entry<String, ?>> entrySet = (Set<? extends Map.Entry<String, ?>>) (Object)config.entrySet();
                //set all fields accessible
                ReflectionUtils.getFields(clazz).forEach(f -> f.setAccessible(true));
                //set none string properties
                entrySet.stream()
                        .filter((Map.Entry<String, ?> map) -> {
                            Field field = reflect.field(map.getKey());
                            return field.getType() != String.class
                                    && map.getValue().getClass() != String.class;
                        })
                        .forEach((Map.Entry<String, ?> map) -> mirror.set().field(map.getKey()).withValue(map.getValue()));
                //set where source and target are string
                entrySet.stream()
                        .filter(map -> reflect.field(map.getKey()).getType() == String.class
                        && map.getValue().getClass() == String.class)
                        .forEach(map -> mirror.set().field(map.getKey()).withValue(map.getValue()));
//                        .forEach(map -> mirror.set().field(map.getKey));
                //convert where 
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
                factory.postInstanceRegistration((Map<Object,Object>)config, this, (T)newNode);
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

        if (declarativeNodeConiguration != null) {

            /*
              TODO create the NodeBuilder, passing this in as a reference loop
              through declarativeNodeMap and create each root with a well-known
              name, by calling NodeBuilder.createInstance(). Add each instance
              to inst2Name map.

             */
            //store the factory callbacks
            for (Class<? extends NodeFactory<?>> clazz : declarativeNodeConiguration.factoryClassSet) {
                NodeFactory<?> factory = clazz.getDeclaredConstructor().newInstance();
                registerNodeFactory(factory);
            }
            //override any any classes with pre-initialised NodeFactories
            for (NodeFactory<?> factory : declarativeNodeConiguration.factorySet) {
                registerNodeFactory(factory);
            }
            //loop through root instance and 
            for (Map.Entry<Class<?>, String> rootNode : declarativeNodeConiguration.rootNodeMappings.entrySet()) {
                Object newNode = findOrCreateNode(rootNode.getKey(), declarativeNodeConiguration.config, rootNode.getValue());
                publicNodeList.add(newNode);
            }
        }
        //add injected instances created by factories
        addNodesFromContext();
        for (Map.Entry<Object, String> entry : inst2Name.entrySet()) {
            Object object = entry.getKey();
            walkDependencies(object);
        }
        inst2Name.putAll(inst2NameTemp);

        //all instances are in inst2Name, can now generate final graph
        for (Map.Entry<Object, String> entry : inst2Name.entrySet()) {
            Object object = entry.getKey();
            walkDependencies(object);
        }

        //create a topological sortedset and put into list
        PriorityQueue<Object> pq = new PriorityQueue<>(Math.max(1, inst2Name.size()), new NaturalOrderComparator<>(Collections.unmodifiableMap(inst2Name)));
        for (Iterator<Object> topologicalIter = new TopologicalOrderIterator<>(graph, pq);
                //        for (Iterator topologicalIter = new TopologicalOrderIterator<>(graph);
                topologicalIter.hasNext();) {
            Object value = topologicalIter.next();
            topologicalHandlers.add(value);
        }

        //if topologicalHandlers is missing nodes then add in a random order
        for (Map.Entry<Object, String> entry : inst2Name.entrySet()) {
            Object node = entry.getKey();
            if (!topologicalHandlers.contains(node)) {
                topologicalHandlers.add(node);
            }
        }

        topologicalHandlers.removeIf(o -> o.getClass().getAnnotation(ExcludeNode.class)!=null);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("GRAPH:" + graph);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SORTED LIST:" + topologicalHandlers);
        }
        buildNonPushSortedHandlers();
        processed = true;
    }

    @SuppressWarnings("unchecked")
    private void buildNonPushSortedHandlers(){
        DirectedGraph<Object, DefaultEdge>  cloneGraph = (DirectedGraph<Object, DefaultEdge>) graph.clone();
        pushEdges.stream()
                .filter(Objects::nonNull)
                .forEach((DefaultEdge edge) -> {
                    Object edgeSource = graph.getEdgeSource(edge);
                    Object edgeTarget = graph.getEdgeTarget(edge);
                    cloneGraph.removeEdge(edgeSource, edgeTarget);
                    cloneGraph.addEdge(edgeTarget, edgeSource);
                });

        //create a topological sortedset and put into list
        PriorityQueue<Object> pq = new PriorityQueue<>(Math.max(1, inst2Name.size()), new NaturalOrderComparator<>(Collections.unmodifiableMap(inst2Name)));
        for (Iterator<Object> topologicalIter = new TopologicalOrderIterator<>(cloneGraph, pq);
            //        for (Iterator topologicalIter = new TopologicalOrderIterator<>(graph);
             topologicalIter.hasNext();) {
            Object value = topologicalIter.next();
            if(topologicalHandlers.contains(value)){
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

    }

    private void addNodesFromContext() {
        if (generationContext != null) {
            addNodeList(generationContext.getNodeList());
        }
    }

    private void registerNodeFactory(NodeFactory<?> obj) throws NoSuchMethodException, SecurityException {
        @SuppressWarnings("unchecked") Class<? extends NodeFactory<?>> clazz = ( Class<? extends NodeFactory<?>>)obj.getClass();
        Method createMethod = clazz.getMethod("createNode", Map.class, NodeRegistry.class);
//        Type genericReturnType = createMethod.getGenericReturnType();
        ParameterizedType paramType = (ParameterizedType) GenericTypeReflector.getExactSuperType(clazz, NodeFactory.class);
        Class<?> targetClass = (Class<?>) paramType.getActualTypeArguments()[0];
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Registered factory:" + clazz.getCanonicalName() + " building:" + targetClass);
        }
        class2FactoryMethod.put(targetClass, new CbMethodHandle(createMethod, obj, "node_factory_" + targetClass.getName()));
        //
        obj.preSepGeneration(generationContext);
        //set target language
    }

    private void walkDependenciesForEventHandling(Object object) throws IllegalArgumentException, IllegalAccessException {
        final Class<?> clazz = object.getClass();
        @SuppressWarnings("unchecked") Set<Field> s = getAllFields(clazz);
        Field[] fields = new Field[s.size()];
        @SuppressWarnings("unchecked") boolean overrideEventTrigger = getAllFields(clazz, withAnnotation(TriggerEventOverride.class)).stream()
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
            field.setAccessible(true);
            Object refField = field.get(object);
            String refName = inst2Name.get(refField);

            if (field.getAnnotation(NoEventReference.class) != null) {
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

    @SuppressWarnings("unchecked")
    private String getInstanceName(Field field, Object node) throws IllegalArgumentException, IllegalAccessException {
        field.setAccessible(true);
        Object refField = field.get(node);
        String refName = inst2Name.get(refField);
        boolean addNode = field.getAnnotation(SepNode.class) != null;
        if (refField != null && field.getAnnotation(ExcludeNode.class)==null) {
            addNode |= !ReflectionUtils.getAllMethods(
                    refField.getClass(),
                    Predicates.or(
                            ReflectionUtils.withAnnotation(AfterEvent.class),
                            ReflectionUtils.withAnnotation(EventHandler.class),
                            ReflectionUtils.withAnnotation(Inject.class),
                            ReflectionUtils.withAnnotation(OnBatchEnd.class),
                            ReflectionUtils.withAnnotation(OnBatchPause.class),
                            ReflectionUtils.withAnnotation(OnEvent.class),
                            ReflectionUtils.withAnnotation(OnEventComplete.class),
                            ReflectionUtils.withAnnotation(OnParentUpdate.class),
                            ReflectionUtils.withAnnotation(TearDown.class),
                            ReflectionUtils.withAnnotation(TriggerEventOverride.class)
                    )
            ).isEmpty();
            addNode |= FilteredEventHandler.class.isAssignableFrom(refField.getClass());
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
    private void implicitAddVectorMember(Object refField) {
        boolean addNode;
        if (refField != null && !inst2Name.containsKey(refField) && !inst2NameTemp.containsKey(refField)) {
            addNode = !ReflectionUtils.getAllMethods(
                    refField.getClass(),
                    Predicates.or(
                            ReflectionUtils.withAnnotation(AfterEvent.class),
                            ReflectionUtils.withAnnotation(EventHandler.class),
                            ReflectionUtils.withAnnotation(Inject.class),
                            ReflectionUtils.withAnnotation(OnBatchEnd.class),
                            ReflectionUtils.withAnnotation(OnBatchPause.class),
                            ReflectionUtils.withAnnotation(OnEvent.class),
                            ReflectionUtils.withAnnotation(OnEventComplete.class),
                            ReflectionUtils.withAnnotation(OnParentUpdate.class),
                            ReflectionUtils.withAnnotation(TearDown.class),
                            ReflectionUtils.withAnnotation(TriggerEventOverride.class)
                    )
            ).isEmpty();
            addNode |= FilteredEventHandler.class.isAssignableFrom(refField.getClass());
            if(addNode){
                inst2NameTemp.put(refField, nameNode(refField));
            }
        }
    }

    private void walkDependencies(Object object) throws IllegalArgumentException, IllegalAccessException {
        walkDependenciesForEventHandling(object);
        @SuppressWarnings("unchecked") Set<Field> s = ReflectionUtils.getAllFields(object.getClass());
        Field[] fields = new Field[s.size()];
        fields = s.toArray(fields);
        for (Field field : fields) {
            field.setAccessible(true);
            Object refField = field.get(object);
            String refName = getInstanceName(field, object);
            if (field.getType().isArray()) {
                Object array = field.get(object);
                if (array == null) {
                    continue;
                }
                int length = Array.getLength(array);
                for (int i = 0; i < length; i++) {
                    refField = Array.get(array, i);
                    implicitAddVectorMember(refField);
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
            } else if (Collection.class
                    .isAssignableFrom(field.getType())) {
                Collection<?> list = (Collection<?>) field.get(object);
                if (list == null) {
                    continue;
                }
                boolean pushCollection = field.getAnnotation(PushReference.class) != null;
                for (Object parent : list) {
                    implicitAddVectorMember(parent);
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
                } else {
                    graph.addEdge(refField, object);
                    walkDependencies(refField);
                }

            }
            //check inject annotation for field
            Inject injecting = field.getAnnotation(Inject.class);
            if (injecting != null & refName == null & field.get(object) == null) {
                HashMap<Object, Object> map = new HashMap<>();
                HashMap<Object, Object> overrideMap = new HashMap<>();

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
                Set<Map.Entry<Object, Object>> entrySet = overrideMap.entrySet();
                entrySet.forEach((overrideEntry) -> map.put(overrideEntry.getKey(), overrideEntry.getValue()));
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
                    newNode = findOrCreateNode(field.getType(), map, injecting.singletonName(), false, true);
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

    public SEPConfig getConfig() {
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
     * @param writer target
     * @param addEvents flag to control inclusion of events as nodes
     * @throws SAXException problem writing jpgraphMl
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
        @SuppressWarnings("unchecked") SimpleDirectedGraph<Object, Object>  exportGraph = (SimpleDirectedGraph<Object, Object> ) graph.clone();
        if (addEvents) {
            graph.vertexSet().forEach((t) -> {
                Method[] methodList = t.getClass().getMethods();
                for (Method method : methodList) {
                    if (method.getAnnotation(com.fluxtion.runtim.annotations.EventHandler.class) != null) {
                        @SuppressWarnings("unchecked") Class<? extends Event> eventTypeClass = (Class<? extends Event>) method.getParameterTypes()[0];
                        exportGraph.addVertex(eventTypeClass);
                        exportGraph.addEdge(eventTypeClass, t);
                    }
                }
                if (t instanceof FilteredEventHandler) {
                    FilteredEventHandler<?> eh = (FilteredEventHandler<?>) t;
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
