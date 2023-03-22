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
package com.fluxtion.compiler;

import com.fluxtion.compiler.builder.callback.CallBackDispatcherFactory;
import com.fluxtion.compiler.builder.callback.CallbackNodeFactory;
import com.fluxtion.compiler.builder.callback.DirtyStateMonitorFactory;
import com.fluxtion.compiler.builder.callback.EventDispatcherFactory;
import com.fluxtion.compiler.builder.callback.EventProcessorCallbackInternalFactory;
import com.fluxtion.compiler.builder.context.EventProcessorContextFactory;
import com.fluxtion.compiler.builder.context.InstanceSupplierFactory;
import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeFactoryRegistration;
import com.fluxtion.compiler.builder.factory.NodeNameLookupFactory;
import com.fluxtion.compiler.builder.factory.NodeNameProducer;
import com.fluxtion.compiler.builder.factory.SingletonNodeFactory;
import com.fluxtion.compiler.builder.filter.EventHandlerFilterOverride;
import com.fluxtion.compiler.builder.input.SubscriptionManagerFactory;
import com.fluxtion.compiler.builder.time.ClockFactory;
import com.fluxtion.compiler.generation.serialiser.FieldContext;
import com.fluxtion.compiler.generation.serialiser.TimeSerializer;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.audit.EventLogControlEvent.LogLevel;
import com.fluxtion.runtime.audit.EventLogManager;
import com.fluxtion.runtime.time.Clock;
import lombok.ToString;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Configuration used by Fluxtion event stream compiler at generation time to
 * control the processing logic of the {@link com.fluxtion.runtime.EventProcessor}
 *
 * @author Greg Higgins
 */
@ToString
public class EventProcessorConfig {

    private final Set<Class<?>> interfaces = new HashSet<>();
    private final Clock clock = ClockFactory.SINGLETON;
    private final Map<String, String> class2replace = new HashMap<>();
    private final Map<Object, Integer> filterMap = new HashMap<>();
    private final Map<Class<?>, Function<FieldContext, String>> classSerializerMap = new HashMap<>();
    private String templateFile;
    private List<Object> nodeList;
    private HashMap<Object, String> publicNodes;
    private HashMap<String, Auditor> auditorMap;
    private NodeFactoryRegistration nodeFactoryRegistration;
    private RootNodeConfig rootNodeConfig;
    private boolean inlineEventHandling = false;
    private boolean supportDirtyFiltering = true;
    private boolean assignPrivateMembers = false;
    private boolean instanceOfDispatch = true;

    public EventProcessorConfig() {
        this.nodeFactoryRegistration = new NodeFactoryRegistration(NodeFactoryConfig.required.getFactoryClasses());
        classSerializerMap.put(Duration.class, TimeSerializer::durationToSource);
    }

    /**
     * Add a node to the SEP. The node will have private final scope, the
     * variable name of the node will be generated from {@link NodeNameProducer}
     * strategy.<p>
     * Fluxtion will check if this node is already in the node set and will
     * return the previously added node.
     *
     * @param <T>  The type of the node to add to the SEP
     * @param node the node instance to add
     * @return The de-duplicated added node
     */
    @SuppressWarnings("unchecked")
    public <T> T addNode(T node) {
        if (getNodeList() == null) {
            setNodeList(new ArrayList<>());
        }
        if (!getNodeList().contains(node)) {
            getNodeList().add(node);
            return node;
        }
        return (T) getNodeList().get(getNodeList().indexOf(node));
    }

    public void addNode(Object... nodeList) {
        Arrays.asList(nodeList).forEach(this::addNode);
    }

    /**
     * Add a node to the SEP. The node will have public final scope, the
     * variable name of the node will be generated from {@link NodeNameProducer}
     * strategy if the provided name is null.<p>
     * Fluxtion will check if this node is already in the node set and will
     * return the previously added node.
     *
     * @param <T>  The type of the node to add to the SEP
     * @param node the node instance to add
     * @param name the variable name of the node
     * @return The de-duplicated added node
     */
    @SuppressWarnings("unchecked")
    public <T> T addNode(T node, String name) {
        addNode(node);
        addPublicNode(node, name);
        return (T) getNodeList().get(getNodeList().indexOf(node));
    }

//    public void addNode(MethodReferenceReflection methodReference){
//
//    }

    /**
     * Add a node to the SEP. The node will have public final scope, the
     * variable name of the node will be generated from {@link NodeNameProducer}
     * strategy if the provided name is null.<p>
     * Fluxtion will check if this node is already in the node set and will
     * return the previously added node.
     *
     * @param <T>  The type of the node to add to the SEP
     * @param node the node instance to add
     * @param name the variable name of the node
     * @return The de-duplicated added node
     */
    public <T> T addPublicNode(T node, String name) {
        if (getPublicNodes() == null) {
            setPublicNodes(new HashMap<>());
        }
        getPublicNodes().put(node, name);
        return node;
    }

    /**
     * Adds an {@link Auditor} to this SEP. The Auditor will have public final
     * scope and can be accessed via the provided variable name.
     *
     * @param <T>      The type of the Auditor
     * @param listener Auditor instance
     * @param name     public name of Auditor
     * @return the added Auditor
     */
    public <T extends Auditor> T addAuditor(T listener, String name) {
        if (getAuditorMap() == null) {
            setAuditorMap(new HashMap<>());
        }
        getAuditorMap().put(name, listener);
        return listener;
    }

    /**
     * Maps a class name from one String to another in the generated output.
     *
     * @param originalFqn Class name to replace
     * @param mappedFqn   Class name replacement
     */
    public void mapClass(String originalFqn, String mappedFqn) {
        getClass2replace().put(originalFqn, mappedFqn);
    }

    /**
     * adds a clock to the generated SEP.
     *
     * @return the clock in generated SEP
     */
    public Clock clock() {
        addAuditor(clock, "clock");
        return clock;
    }

    /**
     * Add an {@link EventLogManager} auditor to the generated SEP. Specify
     * the level at which method tracing will take place.
     */
    public void addEventAudit(LogLevel tracingLogLevel) {
        addAuditor(new EventLogManager().tracingOn(tracingLogLevel), EventLogManager.NODE_NAME);
    }

    public void addEventAudit(LogLevel tracingLogLevel, boolean printEventToString) {
        addAuditor(new EventLogManager().tracingOn(tracingLogLevel).printEventToString(printEventToString), EventLogManager.NODE_NAME);
    }

    public void addInterfaceImplementation(Class<?> clazz) {
        interfaces.add(clazz);
    }

    public Set<Class<?>> interfacesToImplement() {
        return interfaces;
    }

    /**
     * Users can override this method and add SEP description logic here. The
     * buildConfig method will be called by the Fluxtion generator at build
     * time.
     */
    public void buildConfig() {
    }

    /**
     * the name of the template file to use as an input
     */
    public String getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }

    /**
     * the nodes included in this graph
     */
    public List<Object> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<Object> nodeList) {
        this.nodeList = nodeList;
    }

    /**
     * Variable names overrides for public nodes, these will be well known and
     * addressable from outside the SEP.
     */
    public HashMap<Object, String> getPublicNodes() {
        return publicNodes;
    }

    public void setPublicNodes(HashMap<Object, String> publicNodes) {
        this.publicNodes = publicNodes;
    }

    public HashMap<String, Auditor> getAuditorMap() {
        return auditorMap;
    }

    public void setAuditorMap(HashMap<String, Auditor> auditorMap) {
        this.auditorMap = auditorMap;
    }

    /**
     * Node Factory configuration
     */
    public NodeFactoryRegistration getNodeFactoryRegistration() {
        return nodeFactoryRegistration;
    }

    public void setNodeFactoryRegistration(NodeFactoryRegistration nodeFactoryRegistration) {
        //add defaults
        nodeFactoryRegistration.factoryClassSet.addAll(NodeFactoryConfig.required.getFactoryClasses());
        this.nodeFactoryRegistration = nodeFactoryRegistration;
    }

    /**
     * Makes available in the graph an injectable instance that other nodes can inject see {@link com.fluxtion.runtime.annotations.builder.Inject}.
     * The factoryName parameter must match the factoryName attribute in the inject annotation
     * <pre>
     * {@literal }@Inject(factoryName = "someUniqueName")
     *  public RoomSensor roomSensor2;
     *
     * </pre>
     * If no inject annotations reference the instance it will not be added to the graph
     *
     * @param factoryName        The unique name for this instance
     * @param injectionType      The type of injection
     * @param injectableInstance The instance to inject
     * @param <T>                The concrete type of the injected instance
     * @param <S>                The type of the injected instance
     * @return
     */
    public <T, S extends T> EventProcessorConfig registerInjectable(String factoryName, Class<T> injectionType, S injectableInstance) {
        nodeFactoryRegistration.factorySet.add(new SingletonNodeFactory<>(injectableInstance, injectionType, factoryName));
        return this;
    }

    /**
     * Makes available in the graph an injectable instance that other nodes can inject see {@link com.fluxtion.runtime.annotations.builder.Inject}.
     * The factoryName parameter must match the factoryName attribute in the inject annotation
     * <pre>
     * {@literal }@Inject(factoryName = "someUniqueName")
     *  public RoomSensor roomSensor2;
     *
     * </pre>
     * If no inject annotations reference the instance it will not be added to the graph
     *
     * @param factoryName        The unique name for this instance
     * @param injectableInstance The instance to inject
     * @param <T>                The concrete type of the injected instance and the type of the injected instance
     * @return
     */
    public <T> EventProcessorConfig registerInjectable(String factoryName, T injectableInstance) {
        registerInjectable(factoryName, (Class<T>) injectableInstance.getClass(), injectableInstance);
        return this;
    }

    public RootNodeConfig getRootNodeConfig() {
        return rootNodeConfig;
    }

    public void setRootNodeConfig(RootNodeConfig rootNodeConfig) {
        this.rootNodeConfig = rootNodeConfig;
    }

    /**
     * overrides the filter integer id's for a set of instances
     */
    public Map<Object, Integer> getFilterMap() {
        return filterMap;
    }

    public void setFilterMap(Map<Object, Integer> filterMap) {
        this.filterMap.clear();
        this.filterMap.putAll(filterMap);
    }

    /**
     * Overrides the filterId for any methods annotated with {@link com.fluxtion.runtime.annotations.OnEventHandler} in
     * an instance or for an {@link com.fluxtion.runtime.node.EventHandlerNode}.
     * <p>
     * If a single {@link com.fluxtion.runtime.annotations.OnEventHandler} annotated method needs to be overridden then
     * use {@link this#overrideOnEventHandlerFilterId(Object, Class, int)}
     *
     * @param eventHandler the event handler instance to override filterId
     * @param newFilterId  the new filterId
     */
    public void overrideOnEventHandlerFilterId(Object eventHandler, int newFilterId) {
        getFilterMap().put(eventHandler, newFilterId);
    }

    /**
     * Overrides the filterId for a method annotated with {@link com.fluxtion.runtime.annotations.OnEventHandler} in
     * an instance handling a particular event type.
     *
     * @param eventHandler the event handler instance to override filterId
     * @param eventClass   The event handler methods of this type to override filterId
     * @param newFilterId  the new filterId
     */
    public void overrideOnEventHandlerFilterId(Object eventHandler, Class<?> eventClass, int newFilterId) {
        getFilterMap().put(new EventHandlerFilterOverride(eventHandler, eventClass, newFilterId), newFilterId);
    }

    /**
     * Register a custom serialiser that maps a field to source at generation time
     *
     * @param classToSerialize      the class type to support custom serialisation
     * @param serializationFunction The instance to source function
     * @return current {@link EventProcessorConfig}
     */
    public EventProcessorConfig addClassSerializer(
            Class<?> classToSerialize, Function<FieldContext, String> serializationFunction) {
        classSerializerMap.put(classToSerialize, serializationFunction);
        return this;
    }

    public Map<Class<?>, Function<FieldContext, String>> getClassSerializerMap() {
        return classSerializerMap;
    }

    /**
     * configures generated code to inline the event handling methods or not.
     */
    public boolean isInlineEventHandling() {
        return inlineEventHandling;
    }

    public void setInlineEventHandling(boolean inlineEventHandling) {
        this.inlineEventHandling = inlineEventHandling;
    }

    /**
     * configures generated code to support dirty filtering
     */
    public boolean isSupportDirtyFiltering() {
        return supportDirtyFiltering;
    }

    public void setSupportDirtyFiltering(boolean supportDirtyFiltering) {
        this.supportDirtyFiltering = supportDirtyFiltering;
    }

    /**
     * attempt to assign private member variables, some platforms will support
     * access to non-public scoped members. e.g. reflection utilities in Java.
     */
    public boolean isAssignPrivateMembers() {
        return assignPrivateMembers;
    }

    public void setAssignPrivateMembers(boolean assignPrivateMembers) {
        this.assignPrivateMembers = assignPrivateMembers;
    }

    /**
     * Map an original fully qualified class name into a new value. Can be
     * useful if generated code wants to remove all dependencies to Fluxtion
     * classes and replaced with user classes.
     */
    public Map<String, String> getClass2replace() {
        return class2replace;
    }

    public boolean isInstanceOfDispatch() {
        return instanceOfDispatch;
    }

    public void setInstanceOfDispatch(boolean instanceOfDispatch) {
        this.instanceOfDispatch = instanceOfDispatch;
    }

    enum NodeFactoryConfig {
        required(
                CallBackDispatcherFactory.class,
                CallbackNodeFactory.class,
                ClockFactory.class,
                InstanceSupplierFactory.class,
                DirtyStateMonitorFactory.class,
                EventDispatcherFactory.class,
                EventProcessorCallbackInternalFactory.class,
                EventProcessorContextFactory.class,
                NodeNameLookupFactory.class,
                SubscriptionManagerFactory.class
        );

        private final HashSet<Class<? extends NodeFactory<?>>> defaultFactories = new HashSet<>();

        NodeFactoryConfig(Class<? extends NodeFactory<?>>... factoryClasses) {
            Arrays.asList(factoryClasses).forEach(defaultFactories::add);
        }

        public Set<Class<? extends NodeFactory<?>>> getFactoryClasses() {
            return new HashSet<>(defaultFactories);
        }
    }
}
