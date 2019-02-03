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
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.generator.model;

import com.google.common.base.Predicates;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.fluxtion.api.annotations.AfterEvent;
import com.fluxtion.api.annotations.FilterId;
import com.fluxtion.api.annotations.FilterType;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnBatchEnd;
import com.fluxtion.api.annotations.OnBatchPause;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnEventComplete;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.api.annotations.PushReference;
import com.fluxtion.api.annotations.TearDown;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.api.lifecycle.FilteredEventHandler;
import com.fluxtion.builder.generation.FilterDescription;
import com.fluxtion.builder.generation.FilterDescriptionProducer;
import com.fluxtion.generator.model.Field.MappedField;
import com.fluxtion.generator.util.ClassUtils;
import com.fluxtion.generator.util.NaturalOrderComparator;
import com.fluxtion.api.event.Event;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import static java.util.Arrays.stream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.jodah.typetools.TypeResolver;
import org.reflections.ReflectionUtils;
import static org.reflections.ReflectionUtils.withAnnotation;
import static org.reflections.ReflectionUtils.withModifier;
import static org.reflections.ReflectionUtils.withName;
import static org.reflections.ReflectionUtils.withParameters;
import static org.reflections.ReflectionUtils.withParametersCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fluxtion.api.audit.Auditor;
import com.google.common.base.Predicate;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.logging.Level;
import static org.reflections.ReflectionUtils.getConstructors;

/**
 * A class defining the meta-data for the SEP.This class can be introspected
 * after generateMetaModel() has been called and used for code generation.
 *
 * @author Greg Higgins
 */
public class SimpleEventProcessorModel {

    private final Logger LOGGER = LoggerFactory.getLogger(SimpleEventProcessorModel.class);

    /**
     * the nodes managed by this SEP in a alphabetically sorted list. There is
     * only a single instance for a node within this list.
     */
    private List<Field> nodeFields;
    /**
     * the nodes managed by this SEP in a topologically sorted list. There is
     * only a single instance for a node within this list.
     */
    private List<Field> nodeFieldsSortedTopologically;

    /**
     * Audit fields implementing NodeRegistrationListener interface
     */
    private List<Field> registrationListenerFields;

    /**
     * life-cycle callback methods for initialise, sorted in call order.
     */
    private final ArrayList<CbMethodHandle> initialiseMethods;

    /**
     * life-cycle callback methods for end of batch, sorted in call order.
     */
    private final ArrayList<CbMethodHandle> eventEndMethods;

    /**
     * life-cycle callback methods for end of batch, sorted in call order.
     */
    private final ArrayList<CbMethodHandle> batchEndMethods;

    /**
     * life-cycle callback methods for batch pause, sorted in call order.
     */
    private final ArrayList<CbMethodHandle> batchPauseMethods;

    /**
     * life-cycle callback methods for tearDown, sorted in call order.
     */
    private final ArrayList<CbMethodHandle> tearDownMethods;

    /**
     * The dependency model for this SEP
     */
    private final TopologicallySortedDependecyGraph dependencyGraph;

    /**
     * Map of constructor argument lists for a node.
     */
    private Map<Object, List<MappedField>> constructorArgdMap;

    /**
     * Map of bean property mutators for a node.
     */
    private Map<Object, List<String>> beanPropertyMap;

    private Set<Class<?>> importClasses;

    private Map<String, String> mappedClasses;

    /**
     * The dispatch map for event handling, grouped by event filter id's. Class
     * is the class of the Event. Integer is the filter Id. These methods are
     * annotated with {@link OnEvent}
     */
    private final Map<Class, Map<FilterDescription, List<CbMethodHandle>>> dispatchMap;

    /**
     * The dispatch map for post event handling, grouped by event filter id's.
     * Class is the class of the Event. Integer is the filter Id. These methods
     * are annotated with {@link OnEventComplete}
     */
    private final Map<Class, Map<FilterDescription, List<CbMethodHandle>>> postDispatchMap;

    /**
     * Map of callback methods of a node's direct dependents that will be
     * notified when the dependency changes.
     */
    private Map<Object, List<CbMethodHandle>> parentUpdateListenerMethodMap;

    /**
     * Map of update callbacks, object is the node in the SEP, the value is the
     * update method.
     */
    private Map<Object, CbMethodHandle> node2UpdateMethodMap;

    private final ArrayList<FilterDescription> filterDescriptionList;

    private FilterDescriptionProducer flterProducer;

    /**
     * Map of a sep node fields to dirty field. Mappings only exist when dirty
     * support is configured for the whole SEP and the node supports dirty
     * notification.
     */
    private Map<Field, DirtyFlag> dirtyFieldMap;

    /**
     * Multimap of the guard conditions protecting a node
     */
    private Multimap<Object, DirtyFlag> nodeGuardMap;
    /**
     * Filter map, override filter mapping for an instance, the String
     * represents the fqn the instance should be mapped to.
     */
    private final Map<Object, Integer> filterMap;

    /**
     * Node class map, overrides the class of a node
     */
    private final Map<Object, String> nodeClassMap;

    /**
     * Comparator for alphanumeric support, where integers are sorted by value
     * not alphabetically.
     */
    private NaturalOrderComparator comparator;

    /**
     * Is this model configured to generate support for dirty notifications and
     * subsequent event filtering.
     */
    private boolean supportDirtyFiltering;

    public SimpleEventProcessorModel(TopologicallySortedDependecyGraph dependencyGraph) throws Exception {
        this(dependencyGraph, new HashMap());
    }

    public SimpleEventProcessorModel(TopologicallySortedDependecyGraph dependencyGraph, Map<Object, Integer> filterMap) throws Exception {
        this(dependencyGraph, filterMap, null);
    }

    public SimpleEventProcessorModel(TopologicallySortedDependecyGraph dependencyGraph,
            Map<Object, Integer> filterMap,
            Map<Object, String> nodeClassMap) throws Exception {
        this.dependencyGraph = dependencyGraph;
        this.dependencyGraph.generateDependencyTree();
        this.filterMap = filterMap == null ? new HashMap<>() : filterMap;
        this.flterProducer = new DefaultFilterDescriptionProducer();
        this.nodeClassMap = nodeClassMap == null ? Collections.EMPTY_MAP : nodeClassMap;
        constructorArgdMap = new HashMap<>();
        beanPropertyMap = new HashMap<>();
        initialiseMethods = new ArrayList<>();
        tearDownMethods = new ArrayList<>();
        batchEndMethods = new ArrayList<>();
        batchPauseMethods = new ArrayList<>();
        eventEndMethods = new ArrayList<>();
        dispatchMap = new HashMap<>();
        postDispatchMap = new HashMap<>();
        filterDescriptionList = new ArrayList<>();
        parentUpdateListenerMethodMap = new HashMap<>();
        comparator = new NaturalOrderComparator();
        dirtyFieldMap = new HashMap<>();
        nodeGuardMap = HashMultimap.create();
        node2UpdateMethodMap = new HashMap<>();
        importClasses = new HashSet<>();
        mappedClasses = new HashMap<>();
    }

    /**
     * generates the SEP model.
     *
     * @throws Exception exception during model generation
     */
    public void generateMetaModel() throws Exception {
        generateMetaModel(false);
    }

    public void generateMetaModel(boolean supportDirtyFiltering) throws Exception {
        LOGGER.debug("start model");
        nodeFields = new ArrayList<>();
        nodeFieldsSortedTopologically = new ArrayList<>();
        registrationListenerFields = new ArrayList<>();
        this.supportDirtyFiltering = supportDirtyFiltering;
        generateDependentFields();
        generateComplexConstructors();
        generatePropertyAssignments();
        lifeCycleHandlers();
        eventHandlers();
        buildDirtySupport();
        filterList();
        LOGGER.debug("complete model");
    }

    private void generateDependentFields() throws Exception {
        for (Object object : dependencyGraph.getSortedDependents()) {
            final String name = dependencyGraph.variableName(object);
            //TODO - map the class name to another class that will be provided later
            final String overridenClassName = nodeClassMap.get(object);
            final String defaultClassName = object.getClass().getCanonicalName();
            final String className = overridenClassName == null ? defaultClassName : overridenClassName;
            final boolean isPublic = dependencyGraph.isPublicNode(object);
            nodeFields.add(new Field(className, name, object, isPublic));
            nodeFieldsSortedTopologically.add(new Field(className, name, object, isPublic));

        }
        //add the audit listeners
        dependencyGraph.getRegistrationListenerMap().entrySet().stream().forEach((Map.Entry<String, Auditor> t) -> {
            String name = t.getKey();
            Object instance = t.getValue();
            //nodeFields.add(new Field(instance.getClass().getCanonicalName(), name, instance, true));
            registrationListenerFields.add(new Field(instance.getClass().getCanonicalName(), name, instance, true));
        });
        Collections.sort(nodeFields, (Field o1, Field o2) -> comparator.compare((o1.fqn + o1.name), (o2.fqn + o2.name)));
        Collections.sort(registrationListenerFields, (Field o1, Field o2) -> comparator.compare((o1.fqn + o1.name), (o2.fqn + o2.name)));
    }

    private void generatePropertyAssignments() {

        nodeFields.forEach(new Consumer<Field>() {
            @Override
            public void accept(Field f) {
                try {
                    final Object field = f.instance;
                    LOGGER.debug("mapping property mutators for var:{}", f.name);
                    List<String> properties = stream(Introspector.getBeanInfo(f.instance.getClass()).getPropertyDescriptors())
                            .filter((PropertyDescriptor p) -> p.getWriteMethod() != null)
                            .filter((PropertyDescriptor p) -> ClassUtils.propertySupported(p, f, nodeFields))
                            .map(p -> ClassUtils.mapPropertyToJavaSource(p, f, nodeFields, importClasses))
                            .filter(s -> s != null)
                            .collect(Collectors.toList());

                    LOGGER.debug("{} properties:{}", f.name, properties);
                    beanPropertyMap.put(field, properties);
                } catch (IntrospectionException ex) {
                    LOGGER.warn("could not process bean properties", ex);
                }
            }
        });
    }

    private void generateComplexConstructors() {
        nodeFields.forEach(f -> {
            HashSet<MappedField> privateFields = new HashSet<>();
//            HashSet<MappedField> privateCollections = new HashSet<>();
            final Object field = f.instance;
            LOGGER.debug("mapping constructor for var:{}", f.name);
            List<?> directParents = dependencyGraph.getDirectParents(field);
            MappedField[] cstrArgList = new MappedField[(directParents.size()) + 200];
            Class<?> fieldClass = field.getClass();
            //is !public && isDirectParent

            Set<java.lang.reflect.Field> fields = ReflectionUtils.getAllFields(fieldClass, (Predicate<java.lang.reflect.Field>) (java.lang.reflect.Field input) -> {
                //TODO check is not public
                if (Modifier.isStatic(input.getModifiers()) || !Modifier.isFinal(input.getModifiers())) {
//                if (Modifier.isStatic(input.getModifiers()) || (Modifier.isPublic(input.getModifiers()) && !Modifier.isFinal(input.getModifiers()))) {
                    LOGGER.debug("ignoring field:{} public:{} final:{} static:{}",
                            input.getName(),
                            Modifier.isPublic(input.getModifiers()),
                            Modifier.isFinal(input.getModifiers()),
                            Modifier.isStatic(input.getModifiers())
                    );
                    return false;
                }
                try {
                    input.setAccessible(true);
                    final Object parent = input.get(field);
                    if (parent == null) {
                        return false;
                    }
                    if (directParents.contains(parent)) {
                        final MappedField mappedField = new MappedField(input.getName(), getFieldForInstance(parent));
                        mappedField.derivedVal = ClassUtils.mapToJavaSource(input.get(field), nodeFields, importClasses);
                        privateFields.add(mappedField);
                    } else if (List.class.isAssignableFrom(parent.getClass())) {
                        //
                        MappedField collectionField = new MappedField(input.getName());
                        List collection = (List) parent;
                        for (Object element : collection) {
                            collectionField.addField(getFieldForInstance(element));
                        }
                        collectionField.derivedVal = ClassUtils.mapToJavaSource(parent, nodeFields, importClasses);
                        if (!collectionField.isEmpty() || collectionField.derivedVal.length() > 1) {
                            privateFields.add(collectionField);
                            LOGGER.debug("collection field:{}, val:{}", input.getName(), input.get(field));                        
                        }
                    } else if (MappedField.typeSupported(input)) {
                        LOGGER.debug("primitive field:{}, val:{}", input.getName(), input.get(field));
                        MappedField primitiveField = new MappedField(input.getName(), input.get(field));
                        primitiveField.derivedVal = ClassUtils.mapToJavaSource(input.get(field), nodeFields, importClasses);
                        privateFields.add(primitiveField);
                    }
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    java.util.logging.Logger.getLogger(SimpleEventProcessorModel.class.getName()).log(Level.SEVERE, null, ex);
                }
                return false;
            });

            if (privateFields.isEmpty()) {
                LOGGER.debug("{}:default constructor applicable", f.name);
//                continue;
            } else {
                LOGGER.debug("{}:match comoplex constructor private fields:{}", f.name, privateFields);
                boolean[] matched = new boolean[]{false};
//                Set<Constructor> allConstructors = getAllConstructors(fieldClass, input -> {
                Set<Constructor> allConstructors = getConstructors(fieldClass, input -> {
//                    Class[] parameterTypes = input.getParameterTypes();
                    boolean match = matched[0];
                    if (match) {
                        LOGGER.debug("already matched constructor, ignoring");
                        return false;
                    } else {
                        LOGGER.debug("unmatched constructor, reset construtorArgs");
                        Arrays.fill(cstrArgList, null);
                    }
                    Parameter[] parameters = input.getParameters();
                    int parameterCount = parameters.length;
                    if (parameterCount == 0 || parameterCount != privateFields.size()) {
                        LOGGER.debug("parameterCount:{} privateFieldsCoumt:{} mismatch reject consturtcor",
                                parameterCount, privateFields.size());
                    } else {
                        //possible match
                        int matchCount = 0;
                        for (MappedField mappedInstance : privateFields) {
                            Object parentInstance = mappedInstance.instance;
                            String varName = mappedInstance.mappedName;
                            Class<?> parentClass = mappedInstance.parentClass();
                            LOGGER.debug("match field var:{}, type:{}", varName, parentClass);
//                            Class<?> parentClass = mappedInstance.collection?List.class:parentInstance.getClass();
                            boolean matchOnName = false;
                            LOGGER.debug("matching contructor by type and name");
                            //match array
                            for (int i = 0; i < parameters.length; i++) {
                                if (parameters[i] == null) {
                                    continue;
                                }
                                String paramName = parameters[i].getName();
                                Class parameterType = parameters[i].getType();
                                LOGGER.debug("constructor parameter type:{}, paramName:{}, varName:{}",
                                        parameterType, paramName, varName);
                                if (parameterType != null && parameterType.isAssignableFrom(parentClass) && paramName.equals(varName)) {
                                    matchCount++;
                                    parameters[i] = null;
                                    cstrArgList[i] = mappedInstance;
                                    matchOnName = true;
                                    LOGGER.debug("matched constructor arg:{}, by type and name", paramName);
                                    break;
                                }
                            }
                            if (!matchOnName) {
                                LOGGER.debug("no match, matching contructor by type only");
                                for (int i = 0; i < parameters.length; i++) {
                                    if (parameters[i] == null) {
                                        continue;
                                    }
                                    Class parameterType = parameters[i].getType();
                                    String paramName = parameters[i].getName();
                                    LOGGER.debug("constructor parameter type:{}, paramName:{}, varName:{}",
                                            parameterType, paramName, varName);
                                    if (parameterType != null && parameterType.isAssignableFrom(parentClass)) {
                                        matchCount++;
                                        parameters[i] = null;
                                        cstrArgList[i] = mappedInstance;
                                        matchOnName = true;
                                        LOGGER.debug("matched constructor arg:{}, by type only", paramName);
                                        break;
                                    }
                                }
                                if (!matchOnName) {
                                    LOGGER.debug("no match for varName:{}", varName);
                                    break;
                                }
                            }
                        }
                        if (matchCount == parameterCount) {
                            LOGGER.debug("matched constructor:{}", input);
                            matched[0] = true;
                        } else {
                            LOGGER.debug("unmatched constructor:{}", input);
                        }
                    }
                    return matched[0];
                });
                List<MappedField> collect = Arrays.stream(cstrArgList).filter(f1 -> f1 != null).collect(Collectors.toList());
                constructorArgdMap.put(field, collect);
            }
        });
    }

    public List<MappedField> constructorArgs(Object field) {
        List<MappedField> args = constructorArgdMap.get(field);
        return args == null ? Collections.EMPTY_LIST : args;
    }

    public List<String> beanProperties(Object field) {
        List<String> args = beanPropertyMap.get(field);
        return args == null ? Collections.EMPTY_LIST : args;
    }

    private void lifeCycleHandlers() throws Exception {
        Map<Object, String> inst2Name = dependencyGraph.getInstanceMap();
        List<Object> topologicalHandlers = dependencyGraph.getSortedDependents();
        Multimap<Object, CbMethodHandle> parentListenerMultiMap = HashMultimap.create();
        Multimap<Object, CbMethodHandle> parentListenerMultiMapUnmatched = HashMultimap.create();

        for (Iterator<Object> it = topologicalHandlers.iterator(); it.hasNext();) {
            Object object = it.next();
            String name = inst2Name.get(object);
//            Method[] methodList = object.getClass().getDeclaredMethods();
            Method[] methodList = object.getClass().getMethods();
            for (Method method : methodList) {
                if (method.getAnnotation(Initialise.class) != null) {
                    initialiseMethods.add(new CbMethodHandle(method, object, name));
                    if (LOGGER.isDebugEnabled()) {
                        final String validCb = name + "." + method.getName() + "()";
                        LOGGER.debug("initialse call back : " + validCb);
                    }
                }
                if (method.getAnnotation(TearDown.class) != null) {
                    tearDownMethods.add(0, new CbMethodHandle(method, object, name));
                    if (LOGGER.isDebugEnabled()) {
                        final String validCb = name + "." + method.getName() + "()";
                        LOGGER.debug("tear down call back : " + validCb);
                    }
                }
                if (method.getAnnotation(OnBatchEnd.class) != null) {
                    //revered for the batch callbacks
                    batchEndMethods.add(0, new CbMethodHandle(method, object, name));
                    if (LOGGER.isDebugEnabled()) {
                        final String validCb = name + "." + method.getName() + "()";
                        LOGGER.debug("batch end call back : " + validCb);
                    }
                }
                if (method.getAnnotation(OnBatchPause.class) != null) {
                    //revered for the batch callbacks
                    batchPauseMethods.add(0, new CbMethodHandle(method, object, name));
                    if (LOGGER.isDebugEnabled()) {
                        final String validCb = name + "." + method.getName() + "()";
                        LOGGER.debug("batch pause call back : " + validCb);
                    }
                }
                if (method.getAnnotation(AfterEvent.class) != null) {
                    //revered for the batch callbacks
                    eventEndMethods.add(0, new CbMethodHandle(method, object, name));
                    if (LOGGER.isDebugEnabled()) {
                        final String validCb = name + "." + method.getName() + "()";
                        LOGGER.debug("event end call back : " + validCb);
                    }
                }

                if (method.getAnnotation(OnEvent.class) != null) {
                    node2UpdateMethodMap.put(object, new CbMethodHandle(method, object, name));
                }

                if (method.getAnnotation(com.fluxtion.api.annotations.EventHandler.class) != null) {
                    node2UpdateMethodMap.put(object, new CbMethodHandle(method, object, name));
                }

                if (method.getAnnotation(OnParentUpdate.class) != null) {
                    final CbMethodHandle cbMethodHandle = new CbMethodHandle(method, object, name);
                    String val = method.getAnnotation(OnParentUpdate.class).value();
                    if (method.getParameterTypes() == null || method.getParameterTypes().length != 1) {
                        final String errorMsg = "Cannot create OnParentUpdate callback method must have a single parameter "
                                + cbMethodHandle;
                        LOGGER.error(errorMsg);
                        throw new RuntimeException(errorMsg);
                    }
                    ParentFilter filter = new ParentFilter(method.getParameterTypes()[0], val, cbMethodHandle);

                    if (val != null && val.length() > 0) {
                        java.lang.reflect.Field field = null;
                        try {
                            field = object.getClass().getDeclaredField(val);
                        } catch (Exception e) {
                            try {
                                field = object.getClass().getField(val);
                            } catch (Exception e1) {
                            }
                        }
                        field.setAccessible(true);
                        if (field != null && field.getAnnotation(NoEventReference.class) != null || field.getAnnotation(PushReference.class) != null) {
//                            System.out.println("IGNORING NoEventReference for parentUpdate");
                            //continue;
                        }
                        if (field != null && field.get(object) != null) {
                            Object parent = field.get(object);
                            //array
                            if (field.getType().isArray()) {
                                final Class classType = field.getType().getComponentType();
                                int length = Array.getLength(parent);
                                for (int i = 0; i < length; i++) {
                                    ParentFilter testFilter = new ParentFilter(classType, val, null);
                                    if (testFilter.exactmatch(filter)) {
                                        //store in exact match map for merging later
                                        parentListenerMultiMap.put(Array.get(parent, i), cbMethodHandle);
                                    }
                                }
                            }
                            //list
                            if (Collection.class.isAssignableFrom(field.getType())) {
                                ParameterizedType integerListType = (ParameterizedType) field.getGenericType();
                                final Class<?> classType = (Class<?>) integerListType.getActualTypeArguments()[0];
                                Collection list = (Collection) field.get(object);
                                list.forEach((parent1) -> {
                                    ParentFilter testFilter = new ParentFilter(classType, val, null);
                                    if (testFilter.exactmatch(filter)) {
                                        parentListenerMultiMap.put(parent1, cbMethodHandle);
                                    }
                                });
                            }
                            //scalar
                            ParentFilter testFilter = new ParentFilter(parent.getClass(), val, null);
                            if (testFilter.exactmatch(filter)) {
                                //store in exact match map for merging later
                                parentListenerMultiMap.put(parent, cbMethodHandle);
                            } /**
                             * Add inexact match
                             */
                            else if (testFilter.match(filter)) {
                                parentListenerMultiMap.put(parent, cbMethodHandle);
                            }

                        } else {
                            LOGGER.debug("Cannot create OnParentUpdate callback" + cbMethodHandle
                                    + " no parent field matches:'" + val + "'");
                        }
                    } else {
                        //store for matching later
                        parentListenerMultiMapUnmatched.put(object, cbMethodHandle);
                    }

                }
            }
        }
        createParentCallBacks(parentListenerMultiMap, parentListenerMultiMapUnmatched);
    }

    private void createParentCallBacks(Multimap<Object, CbMethodHandle> parentListenerMultiMap, Multimap<Object, CbMethodHandle> parentListenerMultiMapUnmatched) throws Exception {
        List<Object> topologicalHandlers = dependencyGraph.getSortedDependents();
        for (Iterator<Object> it = topologicalHandlers.iterator(); it.hasNext();) {
            Object parent = it.next();
            parentUpdateListenerMethodMap.put(parent, new ArrayList());
//            List<?> directChildren = dependencyGraph.getDirectChildrenListeningForEvent(parent);
            List<?> directChildren = dependencyGraph.getDirectChildren(parent);

            Collection<CbMethodHandle> childCbList = parentListenerMultiMap.get(parent);

            Set<Object> mappedCbs = childCbList.stream().map(cb -> cb.instance).collect(Collectors.toSet());

            directChildren.stream()
                    .filter((child) -> !mappedCbs.contains(child))
                    .map((child) -> parentListenerMultiMapUnmatched.get(child))
                    .map((cbs) -> ClassUtils.findBestParentCB(parent, cbs))
                    .filter((bestParentCB) -> (bestParentCB != null))
                    .forEach((bestParentCB) -> {
                        parentListenerMultiMap.put(parent, bestParentCB);
                    });
        }
        parentListenerMultiMap.keySet().stream().forEach((parent) -> {
            parentUpdateListenerMethodMap.put(parent, new ArrayList(parentListenerMultiMap.get(parent)));
        });
        parentUpdateListenerMethodMap.values().forEach(dependencyGraph::sortNodeList);
    }

    private void eventHandlers() throws Exception {

        List<Object> topologicalHandlers = dependencyGraph.getSortedDependents();
        List<EventCallList> eventCbList = new ArrayList<>();
        //TEMP storage for the root event handlers
        Set<Object> TEMP_EH_SET = new HashSet<>();
        for (Iterator<Object> it = topologicalHandlers.iterator(); it.hasNext();) {
            Object object = it.next();
            if (object instanceof FilteredEventHandler) {
                eventCbList.add(new EventCallList((FilteredEventHandler) object));
                TEMP_EH_SET.add(object);
            }
            //if(object.getClass().get)
            Method[] methodList = object.getClass().getMethods();
            for (Method method : methodList) {
                if (method.getAnnotation(com.fluxtion.api.annotations.EventHandler.class) != null) {
                    eventCbList.add(new EventCallList(object, method));
                    TEMP_EH_SET.add(object);
                }
            }
        }
        //build the no filter handlers - ready to merge in with the filtered lists
        for (EventCallList eventCb : eventCbList) {
            if (eventCb.isFiltered) {
                continue;
            }
            Class eventClass = eventCb.eventTypeClass;
            //onEvent handlers
            Map<FilterDescription, List<CbMethodHandle>> handlerMap = getHandlerMap(eventClass);
            List<CbMethodHandle> callList = new ArrayList<>(eventCb.dispatchMethods);
            if (!eventCb.isInverseFiltered) {
                if (handlerMap.get(FilterDescription.NO_FILTER) == null) {
                    handlerMap.put(FilterDescription.NO_FILTER, callList);
                } else {
                    handlerMap.get(FilterDescription.NO_FILTER).addAll(callList);
                }
            } else {
                if (handlerMap.get(FilterDescription.INVERSE_FILTER) == null) {
                    handlerMap.put(FilterDescription.INVERSE_FILTER, callList);
                } else {
                    handlerMap.get(FilterDescription.INVERSE_FILTER).addAll(callList);
                }
//                handlerMap.put(FilterDescription.INVERSE_FILTER, callList);
            }
            //onEventComplete handlers
            handlerMap = getPostHandlerMap(eventClass);
            callList = new ArrayList<>(eventCb.postDispatchMethods);
            if (!eventCb.isInverseFiltered) {
                if (handlerMap.get(FilterDescription.NO_FILTER) == null) {
                    handlerMap.put(FilterDescription.NO_FILTER, callList);
                } else {
                    handlerMap.get(FilterDescription.NO_FILTER).addAll(callList);
                }
//                handlerMap.put(FilterDescription.NO_FILTER, callList);
            } else {
                if (handlerMap.get(FilterDescription.INVERSE_FILTER) == null) {
                    handlerMap.put(FilterDescription.INVERSE_FILTER, callList);
                } else {
                    handlerMap.get(FilterDescription.INVERSE_FILTER).addAll(callList);
                }
//                handlerMap.put(FilterDescription.INVERSE_FILTER, callList);
            }
        }

        //merge inverse and no filter to default
        Set<Class> eventClassSet = dispatchMap.keySet();
        for (Class eventClass : eventClassSet) {
            Map<FilterDescription, List<CbMethodHandle>> handlerMap = getHandlerMap(eventClass);
            List<CbMethodHandle> noFilterList = handlerMap.get(FilterDescription.NO_FILTER) == null ? Collections.EMPTY_LIST : handlerMap.get(FilterDescription.NO_FILTER);
            List<CbMethodHandle> inverseList = handlerMap.get(FilterDescription.INVERSE_FILTER) == null ? Collections.EMPTY_LIST : handlerMap.get(FilterDescription.INVERSE_FILTER);
            HashSet<CbMethodHandle> set = new HashSet<>(inverseList);
            set.addAll(noFilterList);
            if (set.size() > 0) {
                List<CbMethodHandle> callList = new ArrayList<>(set);
                dependencyGraph.sortNodeList(callList);
                handlerMap.put(FilterDescription.DEFAULT_FILTER, callList);
            }
            //posthandler
            handlerMap = getPostHandlerMap(eventClass);
            noFilterList = handlerMap.get(FilterDescription.NO_FILTER) == null ? Collections.EMPTY_LIST : handlerMap.get(FilterDescription.NO_FILTER);
            inverseList = handlerMap.get(FilterDescription.INVERSE_FILTER) == null ? Collections.EMPTY_LIST : handlerMap.get(FilterDescription.INVERSE_FILTER);
            set = new HashSet<>(inverseList);
            set.addAll(noFilterList);
            if (set.size() > 0) {
                List<CbMethodHandle> callList = new ArrayList<>(set);
                dependencyGraph.sortNodeList(callList);
                handlerMap.put(FilterDescription.DEFAULT_FILTER, callList);
            }
        }

        //loop through the eventCbList and create the dispatch Map
        for (EventCallList eventCb : eventCbList) {
            int filterId = eventCb.filterId;
            String filterString = eventCb.filterString;
            boolean isIntFilter = eventCb.isIntFilter;
            boolean isFiltering = eventCb.isFiltered;
            Class eventClass = eventCb.eventTypeClass;
            FilterDescription filterDescription = FilterDescription.NO_FILTER;
            if (isIntFilter && isFiltering) {
                filterDescription = flterProducer.getFilterDescription(eventClass, filterId);
            } else if (isFiltering) {
                filterDescription = flterProducer.getFilterDescription(eventClass, filterString);
            } else {
                //Ignore as this non-filtered dispatch - already resolved
                continue;
            }
            //TODO add null filter singleton
            Map<FilterDescription, List<CbMethodHandle>> handlerMap = getHandlerMap(eventClass);
            //get the sublist for this event handler
            List<CbMethodHandle> callList = handlerMap.get(filterDescription);
            if (callList == null) {
                callList = new ArrayList<>();
                handlerMap.put(filterDescription, callList);
                callList.addAll(eventCb.dispatchMethods);

            } else {
                // another event handler has the same filter, need to create a
                //merge of both lists and then sort
                for (CbMethodHandle newCbMethod : eventCb.dispatchMethods) {
                    if (!callList.contains(newCbMethod)) {
                        callList.add(newCbMethod);
                    }
                }
            }
            //now add the non-filtered methods for the event class (if any) to the filtered cb's
            List<CbMethodHandle> nonFilterCbList = handlerMap.get(FilterDescription.NO_FILTER);
            if (nonFilterCbList != null) {
                for (CbMethodHandle nonFilterCb : nonFilterCbList) {
                    if (!callList.contains(nonFilterCb)) {
                        callList.add(nonFilterCb);
                    }
                }
            }
            //get the sublist for the event complete handlers
            Map<FilterDescription, List<CbMethodHandle>> postHandlerMap = getPostHandlerMap(eventClass);
            List<CbMethodHandle> postCallList = postHandlerMap.get(filterDescription);
            if (postCallList == null) {
                postCallList = new ArrayList<>();
                postHandlerMap.put(filterDescription, postCallList);
                postCallList.addAll(eventCb.postDispatchMethods);

            } else {
                // another event handler has the same filter, need to create a
                //merge of both lists and then sort
                for (CbMethodHandle newCbMethod : eventCb.postDispatchMethods) {
                    if (!postCallList.contains(newCbMethod)) {
                        postCallList.add(newCbMethod);
                    }
                }
            }
            //now add the non-filtered methods for the event class (if any) to the filtered cb's
            List<CbMethodHandle> nonFilterPoistCbList = postHandlerMap.get(FilterDescription.NO_FILTER);
            if (nonFilterPoistCbList != null) {
                for (CbMethodHandle nonFilterCb : nonFilterPoistCbList) {
                    if (!postCallList.contains(nonFilterCb)) {
                        postCallList.add(nonFilterCb);
                    }
                }
            }
            dependencyGraph.sortNodeList(callList);
            dependencyGraph.sortNodeList(postCallList);
            Collections.reverse(postCallList);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(dispatchMapToString());
        }

    }

    private Map<FilterDescription, List<CbMethodHandle>> getHandlerMap(Class eventClass) {
        Map<FilterDescription, List<CbMethodHandle>> handlerMap = dispatchMap.get(eventClass);
        if (handlerMap == null) {
            handlerMap = new HashMap<>();
            dispatchMap.put(eventClass, handlerMap);
        }
        return handlerMap;
    }

    private Map<FilterDescription, List<CbMethodHandle>> getPostHandlerMap(Class eventClass) {
        Map<FilterDescription, List<CbMethodHandle>> handlerMap = postDispatchMap.get(eventClass);
        if (handlerMap == null) {
            handlerMap = new HashMap<>();
            postDispatchMap.put(eventClass, handlerMap);
        }
        return handlerMap;
    }

    private void buildDirtySupport() throws Exception {
        if (supportDirtyFiltering()) {
            for (Field node : nodeFields) {
                CbMethodHandle cbHandle = node2UpdateMethodMap.get(node.instance);
                if (cbHandle != null && cbHandle.method.getReturnType() == boolean.class) {
                    DirtyFlag flag = new DirtyFlag(node, "isDirty_" + node.name);
                    dirtyFieldMap.put(node, flag);
                }
            }
            //build the guard conditions for nodes
            //loop in topological order
            for (Object node : dependencyGraph.getSortedDependents()) {
                //if no parents then continue
//                List<?> directParents = dependencyGraph.getDirectParents(node);
                List<?> directParents = dependencyGraph.getDirectParentsListeningForEvent(node);
                if (directParents.isEmpty()) {
                    continue;
                }
                //get parents of node and loop through
                Set<DirtyFlag> guardSet = new HashSet<>();
                parentLoop:
                for (Object parent : directParents) {
                    //get dirty field for node 
                    DirtyFlag parentDirtyFlag = getDirtyFlagForUpdateCb(node2UpdateMethodMap.get(parent));
                    //get the guards for the parent using the multimap
                    Collection<DirtyFlag> parentDirtyFlags = nodeGuardMap.get(parent);
                    //if parent guard != null add as a guard to multimap, continue
                    //else if guards!=null add to multimap, continue
                    //else clear mutlimap, break
                    if (parentDirtyFlag != null) {
                        guardSet.add(parentDirtyFlag);
                    } else if (!parentDirtyFlags.isEmpty()) {
                        guardSet.addAll(parentDirtyFlags);
                    } else {
                        guardSet.clear();
                        break;
                    }

                }
                nodeGuardMap.putAll(node, guardSet);
                //
            }

        }
    }

    private void filterList() {
        Set<FilterDescription> uniqueFilterSet = new HashSet<>();
        Collection<Map<FilterDescription, List<CbMethodHandle>>> values = dispatchMap.values();
        for (Map<FilterDescription, List<CbMethodHandle>> value : values) {
            Set<FilterDescription> keySet = value.keySet();
            for (FilterDescription filterDescription : keySet) {
                uniqueFilterSet.add(filterDescription);
            }
        }
        values = postDispatchMap.values();
        for (Map<FilterDescription, List<CbMethodHandle>> value : values) {
            Set<FilterDescription> keySet = value.keySet();
            for (FilterDescription filterDescription : keySet) {
                uniqueFilterSet.add(filterDescription);
            }
        }
        filterDescriptionList.addAll(uniqueFilterSet);
        filterDescriptionList.remove(FilterDescription.NO_FILTER);
        LOGGER.debug("filterList:" + filterDescriptionList);
    }

    public DirtyFlag getDirtyFlagForNode(Object node) {
        return dirtyFieldMap.get(getFieldForInstance(node));
    }

    /**
     * Provides a list of guard conditions for a node, but only if
     * supportDirtyFiltering is configured and all of the parents of the node
     * support the dirty flag. If any parent, direct or indirect does not
     * support the dirty flag then the node updated method will always be called
     * after a parent has been notified of an event.
     *
     * Parents can be traced all the way to the root for dirty support,
     * effectively inheriting dirty support down the call tree.
     *
     * @param node the node to introspect
     * @return collection of dirty flags that guard the node
     */
    public Collection<DirtyFlag> getNodeGuardConditions(Object node) {
        final ArrayList<DirtyFlag> guards = new ArrayList<>(nodeGuardMap.get(node));
        Collections.sort(guards, (DirtyFlag o1, DirtyFlag o2) -> comparator.compare(o1.name, o2.name));
        return guards;
    }

    public Collection<DirtyFlag> getNodeGuardConditions(CbMethodHandle cb) {
        if (cb.isPostEventHandler && dependencyGraph.getDirectParents(cb.instance).isEmpty()) {
            final DirtyFlag dirtyFlagForNode = getDirtyFlagForNode(cb.instance);
            List<DirtyFlag> flags = dirtyFlagForNode == null ? Collections.EMPTY_LIST : Arrays.asList(dirtyFlagForNode);
            Collections.sort(flags, (DirtyFlag o1, DirtyFlag o2) -> comparator.compare(o1.name, o2.name));
            return flags;
        }
        return cb.isEventHandler ? Collections.EMPTY_SET : getNodeGuardConditions(cb.instance);
    }

    /**
     * Provides a list of guard conditions for a node, but only if
     * supportDirtyFiltering is configured and all of the parents of the node
     * support the dirty flag. If any parent does not support the dirty flag
     * then the node updated method will always be called after a parent has
     * been notified of an event.
     *
     * @param node the node to introspect
     * @return collection of dirty flags that guard the node
     */
    public List<DirtyFlag> getNodeGuardConditions_OLD(Object node) {
        ArrayList<DirtyFlag> guards = new ArrayList<>();
        if (supportDirtyFiltering()) {
            List<?> directParents = dependencyGraph.getDirectParents(node);
            for (Object parent : directParents) {
                DirtyFlag parentDirtyFlag = getDirtyFlagForUpdateCb(node2UpdateMethodMap.get(parent));
                if (parentDirtyFlag == null) {
                    guards.clear();
                    break;
                } else {
                    guards.add(parentDirtyFlag);
                }
            }
        }
        return guards;
    }

    public DirtyFlag getDirtyFlagForUpdateCb(CbMethodHandle cbHandle) {
        DirtyFlag flag = null;
        if (supportDirtyFiltering() && cbHandle != null && cbHandle.method.getReturnType() == boolean.class) {
            flag = dirtyFieldMap.get(getFieldForInstance(cbHandle.instance));
        }
        return flag;
    }

    public Field getFieldForInstance(Object object) {
        Field ret = null;
        for (Field nodeField : nodeFields) {
            if (nodeField.instance == object) {
                ret = nodeField;
                break;
            }
        }
        return ret;
    }

    public String getMappedClass(String className) {
        if (dependencyGraph == null || dependencyGraph.getConfig() == null) {
            return className;
        }
        return dependencyGraph.getConfig().class2replace.getOrDefault(className, className);
    }

    private boolean supportDirtyFiltering() {
        return supportDirtyFiltering;
    }

    public List<Field> getNodeFields() {
        return Collections.unmodifiableList(nodeFields);
    }

    public List<Field> getTopologigcallySortedNodeFields() {
        return Collections.unmodifiableList(nodeFieldsSortedTopologically);
    }

    public List<Field> getNodeRegistrationListenerFields() {
        return Collections.unmodifiableList(registrationListenerFields);
    }

    public List<CbMethodHandle> getInitialiseMethods() {
        return Collections.unmodifiableList(initialiseMethods);
    }

    public List<CbMethodHandle> getTearDownMethods() {
        return Collections.unmodifiableList(tearDownMethods);
    }

    public List<CbMethodHandle> getBatchEndMethods() {
        return Collections.unmodifiableList(batchEndMethods);
    }

    public List<CbMethodHandle> getBatchPauseMethods() {
        return Collections.unmodifiableList(batchPauseMethods);
    }

    public List<CbMethodHandle> getEventEndMethods() {
        return Collections.unmodifiableList(eventEndMethods);
    }

    public Map<Class, Map<FilterDescription, List<CbMethodHandle>>> getDispatchMap() {
        return Collections.unmodifiableMap(dispatchMap);
    }

    public Map<Class, Map<FilterDescription, List<CbMethodHandle>>> getPostDispatchMap() {
        return Collections.unmodifiableMap(postDispatchMap);
    }

    public Map<Object, List<CbMethodHandle>> getParentUpdateListenerMethodMap() {
        return Collections.unmodifiableMap(parentUpdateListenerMethodMap);
    }

    public Map<Field, DirtyFlag> getDirtyFieldMap() {
        return Collections.unmodifiableMap(dirtyFieldMap);
    }

    public List<FilterDescription> getFilterDescriptionList() {
        return Collections.unmodifiableList(filterDescriptionList);
    }

    public Set<Class<?>> getImportClasses() {
        return Collections.unmodifiableSet(importClasses);
    }

    private String dispatchMapToString() {
        String result = "DispatchMap[\n";

        Set<Class> keySet = dispatchMap.keySet();
        for (Class eventId : keySet) {
            result += "\tEvent Id:" + eventId + "\n";
            Map<FilterDescription, List<CbMethodHandle>> cbMap = dispatchMap.get(eventId);
            Set<FilterDescription> filterIdSet = cbMap.keySet();
            for (FilterDescription filterDescription : filterIdSet) {
                int filterId = filterDescription.value;
                result += "\t\tFilter Id:" + filterId + "\n";
                List<CbMethodHandle> cbList = cbMap.get(filterDescription);
                for (CbMethodHandle cbMethod : cbList) {
                    result += "\t\t\t" + cbMethod + "\n";
                }
            }
        }
        result += "]\n";

        //post dispatch
        result += "PostDispatchMap[\n";
        keySet = postDispatchMap.keySet();
        for (Class eventId : keySet) {
            result += "\tEvent Id:" + eventId + "\n";
            Map<FilterDescription, List<CbMethodHandle>> cbMap = dispatchMap.get(eventId);
            Set<FilterDescription> filterIdSet = cbMap.keySet();
            for (FilterDescription filterDescription : filterIdSet) {
                int filterId = filterDescription.value;
                result += "\t\tFilter Id:" + filterId + "\n";
                List<CbMethodHandle> cbList = cbMap.get(filterDescription);
                for (CbMethodHandle cbMethod : cbList) {
                    result += "\t\t\t" + cbMethod + "\n";
                }
            }
        }
        result += "]";
        return result;
    }

    /**
     * A helper class, holds the call tree and meta-data for an event type
     */
    private class EventCallList {

//        final FilteredEventHandler eh;
        final int filterId;
        final String filterString;
        final boolean isIntFilter;
        final boolean isFiltered;
        final boolean isInverseFiltered;
        final Class eventTypeClass;
        private final List sortedDependents;
        private final List<CbMethodHandle> dispatchMethods;
        /**
         * the set of methods to be called on a unwind of an event annotated
         * with {@link OnEventComplete}
         */
        private List<CbMethodHandle> postDispatchMethods;

        EventCallList(FilteredEventHandler eh) throws Exception {
            if (filterMap.containsKey(eh)) {
                filterId = filterMap.get(eh);
            } else {
                filterId = eh.filterId();
            }
//            sortedDependents = dependencyGraph.getSortedDependents(eh);
            sortedDependents = dependencyGraph.getEventSortedDependents(eh);
            dispatchMethods = new ArrayList<>();
            postDispatchMethods = new ArrayList<>();
            Class searchClass = Event.class;
            if (eh.eventClass() == null) {
                eventTypeClass = (TypeResolver.resolveRawArguments(EventHandler.class, eh.getClass()))[0];
                searchClass = eventTypeClass;
            } else {
                eventTypeClass = eh.eventClass();
            }

            Set<Method> ehMethodList = ReflectionUtils.getAllMethods(eh.getClass(),
                    Predicates.and(
                            withModifier(Modifier.PUBLIC),
                            withName("onEvent"),
                            withParametersCount(1)),
                    withParameters(searchClass)
            );
            Method onEventMethod = ehMethodList.iterator().next();

            String name = dependencyGraph.variableName(eh);
            dispatchMethods.add(new CbMethodHandle(onEventMethod, eh, name, eventTypeClass, true));
            for (int i = 1; i < sortedDependents.size(); i++) {
                Object object = sortedDependents.get(i);
                name = dependencyGraph.variableName(object);
                Method[] methodList = object.getClass().getDeclaredMethods();

                for (Method method : methodList) {
                    if (method.getAnnotation(OnEvent.class) != null) {
                        dispatchMethods.add(new CbMethodHandle(method, object, name));
                    }
                    if (method.getAnnotation(OnEventComplete.class) != null) {
                        postDispatchMethods.add(new CbMethodHandle(method, object, name));
                    }
                }
            }
            filterString = null;
//            isIntFilter = true;
            isIntFilter = filterId != Event.NO_ID;
//            isFiltered = true;
            isFiltered = filterId != Event.NO_ID;
            isInverseFiltered = false;
        }

        EventCallList(Object instance, Method onEventMethod) throws Exception {
            String tmpFilterString = null;
            int tmpFilterId = 0;
            boolean tmpIsIntFilter = true;
            boolean tmpIsFiltered = true;
            boolean tmpIsInverseFiltered = false;
            Set<java.lang.reflect.Field> fields = ReflectionUtils.getAllFields(instance.getClass(), withAnnotation(FilterId.class));
            com.fluxtion.api.annotations.EventHandler annotation = onEventMethod.getAnnotation(com.fluxtion.api.annotations.EventHandler.class);
            //int attribute filter on annoatation 
            int filterIdOverride = annotation.filterId();
            //String attribute filter on annoatation 
            String genericFilter = "";
            if (onEventMethod.getGenericParameterTypes().length == 1 && onEventMethod.getGenericParameterTypes()[0] instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) onEventMethod.getGenericParameterTypes()[0];
                final Type actualType = pt.getActualTypeArguments()[0];
                genericFilter = actualType instanceof Class ? ((Class) actualType).getCanonicalName() : actualType.getTypeName();
            }
            String filterStringOverride = annotation.filterStringFromClass() != void.class ? annotation.filterStringFromClass().getCanonicalName() : annotation.filterString();
            filterStringOverride = filterStringOverride.isEmpty() ? genericFilter : filterStringOverride;
            Set<java.lang.reflect.Field> s = ReflectionUtils.getAllFields(instance.getClass(), withName(annotation.filterVariable()));
            if (annotation.filterVariable().length() > 0 && s.size() > 0) {
                java.lang.reflect.Field f = s.iterator().next();
                f.setAccessible(true);
                if (f.get(instance) != null) {
                    if (f.getType().equals(String.class)) {
                        filterStringOverride = (String) f.get(instance);
                        //                    filterStringOverride = (String) instance.getClass().getField(annotation.filterVariable()).get(instance);
                    } else if (f.getType().equals(int.class)) {
                        filterIdOverride = f.getInt(instance);
                        //                    filterIdOverride = instance.getClass().getField(annotation.filterVariable()).getInt(instance);
                    } else if (f.getType().equals(char.class)) {
                        filterIdOverride = f.getChar(instance);
                        //                    filterIdOverride = instance.getClass().getField(annotation.filterVariable()).getInt(instance);
                    } else if (f.getType().equals(byte.class)) {
                        filterIdOverride = f.getByte(instance);
                        //                    filterIdOverride = instance.getClass().getField(annotation.filterVariable()).getInt(instance);
                    } else if (f.getType().equals(short.class)) {
                        filterIdOverride = f.getShort(instance);
                        //                    filterIdOverride = instance.getClass().getField(annotation.filterVariable()).getInt(instance);
                    }
                }
            }
            boolean overrideFilter = filterIdOverride != Integer.MAX_VALUE;
            boolean overideStringFilter = filterStringOverride != null && !filterStringOverride.isEmpty();
            if (filterMap.containsKey(instance)) {
                tmpFilterId = filterMap.get(instance);
            } else if (fields.isEmpty() && overrideFilter) {
                tmpFilterId = filterIdOverride;
            } else if (fields.isEmpty() && overideStringFilter) {
                tmpFilterString = filterStringOverride;
                tmpIsIntFilter = false;
            } else if (fields.isEmpty()) {
                //no filtering
                tmpIsFiltered = false;
                tmpIsIntFilter = false;
                //EventHandler annotation = onEventMethod.getAnnotation(EventHandler.class);
                tmpIsInverseFiltered = annotation.value() == FilterType.unmatched;
            } else {
                java.lang.reflect.Field field = fields.iterator().next();
                field.setAccessible(true);
                Class type = field.getType();
                if (type == int.class) {
                    tmpFilterId = field.getInt(instance);
                    tmpIsFiltered = tmpFilterId != Event.NO_ID;
                } else if (type == String.class) {
                    tmpFilterString = (String) field.get(instance);
                    tmpIsIntFilter = false;
                    if (tmpFilterString == null || tmpFilterString.isEmpty()) {
                        tmpIsFiltered = false;
                    }
                } else {
                    //TODO support no filter ID - receives all events
                    throw new IllegalArgumentException("the annotation filter can only annotate int or String fields");
                }
            }

            if (annotation.propagate()) {
//                sortedDependents = dependencyGraph.getSortedDependents(instance);
                sortedDependents = dependencyGraph.getEventSortedDependents(instance);
            } else {
                sortedDependents = Collections.EMPTY_LIST;
            }

            dispatchMethods = new ArrayList<>();
            postDispatchMethods = new ArrayList<>();

            eventTypeClass = onEventMethod.getParameterTypes()[0];

            String name = dependencyGraph.variableName(instance);
            dispatchMethods.add(new CbMethodHandle(onEventMethod, instance, name, eventTypeClass, true));
            //check for @OnEventComplete on the root of the event tree
            Method[] methodList = instance.getClass().getDeclaredMethods();
            for (Method method : methodList) {
                if (method.getAnnotation(OnEventComplete.class) != null) {
                    postDispatchMethods.add(new CbMethodHandle(method, instance, name));
                }
            }

            for (int i = 0; i < sortedDependents.size(); i++) {
                Object object = sortedDependents.get(i);
                name = dependencyGraph.variableName(object);
                methodList = object.getClass().getDeclaredMethods();

                for (Method method : methodList) {
                    if (method.getAnnotation(OnEvent.class) != null) {
                        dispatchMethods.add(new CbMethodHandle(method, object, name));
                    }
                    if (method.getAnnotation(OnEventComplete.class) != null && i > 0) {
                        postDispatchMethods.add(new CbMethodHandle(method, object, name));
                    }
                }
            }
            filterId = tmpFilterId;
            filterString = tmpFilterString;
            isIntFilter = tmpIsIntFilter;
            isFiltered = tmpIsFiltered;
            isInverseFiltered = tmpIsInverseFiltered;
        }

    }
}
