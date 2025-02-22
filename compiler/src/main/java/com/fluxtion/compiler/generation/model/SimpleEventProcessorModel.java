/*
 * Copyright (c) 2019-2025 gregory higgins.
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

import com.fluxtion.compiler.builder.filter.DefaultFilterDescriptionProducer;
import com.fluxtion.compiler.builder.filter.EventHandlerFilterOverride;
import com.fluxtion.compiler.builder.filter.FilterDescription;
import com.fluxtion.compiler.builder.filter.FilterDescriptionProducer;
import com.fluxtion.compiler.generation.model.Field.MappedField;
import com.fluxtion.compiler.generation.serialiser.FieldSerializer;
import com.fluxtion.compiler.generation.util.ClassUtils;
import com.fluxtion.compiler.generation.util.NaturalOrderComparator;
import com.fluxtion.runtime.annotations.*;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.annotations.builder.ConstructorArg;
import com.fluxtion.runtime.annotations.builder.FluxtionIgnore;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.node.EventHandlerNode;
import com.fluxtion.runtime.time.Clock;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.jodah.typetools.TypeResolver;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.LongAdder;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.fluxtion.compiler.generation.model.ConstructorMatcherPredicate.*;
import static com.fluxtion.compiler.generation.util.SuperMethodAnnotationScanner.annotationInHierarchy;
import static java.util.Arrays.stream;

/**
 * A class defining the meta-data for the SEP.This class can be introspected
 * after generateMetaModel() has been called and used for code generation.
 *
 * @author Greg Higgins
 */
@Slf4j
public class SimpleEventProcessorModel {

    private final Logger LOGGER = LoggerFactory.getLogger(SimpleEventProcessorModel.class);

    /**
     * the nodes managed by this SEP in an alphabetically sorted list. There is
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
     * life-cycle callback methods for initialise, sorted in call order.
     */
    private final ArrayList<CbMethodHandle> startMethods;
    /**
     * life-cycle callback methods for initialise, sorted in call order.
     */
    private final ArrayList<CbMethodHandle> startCompleteMethods;
    /**
     * life-cycle callback methods for initialise, sorted in call order.
     */
    private final ArrayList<CbMethodHandle> stopMethods;

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
    private final TopologicallySortedDependencyGraph dependencyGraph;

    /**
     * Map of constructor argument lists for a node.
     */
    private final Map<Object, List<Field.MappedField>> constructorArgumentMap;

    /**
     * Map of bean property mutators for a node.
     */
    private final Map<Object, List<String>> beanPropertyMap;

    private final Set<Class<?>> importClasses;

    /**
     * A topologically sorted list of all {@link CbMethodHandle} in this graph. These methods are
     * annotated with {@link OnTrigger} or {@link OnEventHandler}
     */
    private List<CbMethodHandle> allEventCallBacks;

    /**
     * A topologically sorted list of all post event {@link CbMethodHandle} in this graph. These methods are
     * annotated with {@link AfterTrigger}
     */
    private List<CbMethodHandle> allPostEventCallBacks;

    /**
     * The dispatch map for event handling, grouped by event filter id's. Class
     * is the class of the Event. Integer is the filter Id. These methods are
     * annotated with {@link OnTrigger} or {@link OnEventHandler}
     */
    private final Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> dispatchMap;

    /**
     * The dispatch map for post event handling, grouped by event filter id's.
     * Class is the class of the Event. Integer is the filter Id. These methods
     * are annotated with {@link AfterTrigger}
     */
    private final Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> postDispatchMap;

    /**
     * The dispatch map for event handling, grouped by event filter id's. Class
     * is the class of the Event. Integer is the filter Id. These methods are
     * annotated with {@link OnEventHandler}. Methods annotated with {@link OnTrigger} are not in this dispatchMap
     */
    private Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> handlerOnlyDispatchMap;

    /**
     * Map of callback methods of a node's direct dependents that will be
     * notified when the dependency changes.
     */
    private final Map<Object, List<CbMethodHandle>> parentUpdateListenerMethodMap;

    /**
     * Map of update callbacks, object is the node in the SEP, the value is the
     * update method.
     */
    private final Map<Object, CbMethodHandle> node2UpdateMethodMap;

    private final ArrayList<FilterDescription> filterDescriptionList;

    private final FilterDescriptionProducer filterProducer;

    /**
     * Map of a sep node fields to dirty field. Mappings only exist when dirty
     * support is configured for the whole SEP and the node supports dirty
     * notification.
     */
    private final Map<Field, DirtyFlag> dirtyFieldMap;

    /**
     * Multimap of the guard conditions protecting a node
     */
    private final Multimap<Object, DirtyFlag> nodeGuardMap;
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
    private final NaturalOrderComparator<?> comparator;

    /**
     * Is this model configured to generate support for dirty notifications and
     * subsequent event filtering.
     */
    private boolean supportDirtyFiltering;
    @Setter
    @Getter
    private boolean dispatchOnlyVersion = false;

    private final FieldSerializer fieldSerializer;
    private List<CbMethodHandle> triggerOnlyCallBacks;
    private Set<Object> forkedTriggerInstances;

    public SimpleEventProcessorModel(TopologicallySortedDependencyGraph dependencyGraph) throws Exception {
        this(dependencyGraph, new HashMap<>());
    }

    public SimpleEventProcessorModel(TopologicallySortedDependencyGraph dependencyGraph, Map<Object, Integer> filterMap) throws Exception {
        this(dependencyGraph, filterMap, null);
    }

    public SimpleEventProcessorModel(TopologicallySortedDependencyGraph dependencyGraph,
                                     Map<Object, Integer> filterMap,
                                     Map<Object, String> nodeClassMap) throws Exception {
        this.dependencyGraph = dependencyGraph;
        this.dependencyGraph.generateDependencyTree();
        this.filterMap = filterMap == null ? new HashMap<>() : filterMap;
        this.filterProducer = new DefaultFilterDescriptionProducer();
        this.nodeClassMap = nodeClassMap == null ? Collections.emptyMap() : nodeClassMap;
        constructorArgumentMap = new HashMap<>();
        beanPropertyMap = new HashMap<>();
        initialiseMethods = new ArrayList<>();
        startMethods = new ArrayList<>();
        startCompleteMethods = new ArrayList<>();
        stopMethods = new ArrayList<>();
        tearDownMethods = new ArrayList<>();
        batchEndMethods = new ArrayList<>();
        batchPauseMethods = new ArrayList<>();
        eventEndMethods = new ArrayList<>();
        dispatchMap = new HashMap<>();
        postDispatchMap = new HashMap<>();
        filterDescriptionList = new ArrayList<>();
        parentUpdateListenerMethodMap = new HashMap<>();
        comparator = new NaturalOrderComparator<>();
        dirtyFieldMap = new HashMap<>();
        nodeGuardMap = HashMultimap.create();
        node2UpdateMethodMap = new HashMap<>();
        importClasses = new HashSet<>();
        fieldSerializer = new FieldSerializer(dependencyGraph.getConfig());
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

    public void generateMetaModelInMemory(boolean supportDirtyFiltering) throws Exception {
        LOGGER.debug("start model");
        nodeFields = new ArrayList<>();
        nodeFieldsSortedTopologically = new ArrayList<>();
        registrationListenerFields = new ArrayList<>();
        this.supportDirtyFiltering = supportDirtyFiltering;
        generateDependentFields();
        lifeCycleHandlers();
        eventHandlers();
        buildDirtySupport();
        filterList();
        LOGGER.debug("complete model");
    }

    private void generateDependentFields() throws Exception {
        for (Object object : dependencyGraph.getObjectSortedDependents()) {
            final String name = dependencyGraph.variableName(object);
            //TODO - map the class name to another class that will be provided later
            final String classNameOverride = nodeClassMap.get(object);
            final String defaultClassName = object.getClass().getCanonicalName();
            final String className = classNameOverride == null ? defaultClassName : classNameOverride;
            final boolean isPublic = dependencyGraph.isPublicNode(object);
            nodeFields.add(new Field(className, name, object, isPublic));
            nodeFieldsSortedTopologically.add(new Field(className, name, object, isPublic));

        }
        //add the audit listeners
        dependencyGraph.getRegistrationListenerMap().forEach((name, value) ->
                registrationListenerFields.add(
                        new Field(value.getClass().getCanonicalName(), name, value, true)
                )
        );
        nodeFields.sort((Field o1, Field o2) -> comparator.compare((o1.fqn + o1.name), (o2.fqn + o2.name)));
        //sort by topological order
        registrationListenerFields.sort((Field o1, Field o2) -> {
            int idx1 = nodeFieldsSortedTopologically.indexOf(o1);
            int idx2 = nodeFieldsSortedTopologically.indexOf(o2);
            if (o1.instance instanceof Clock) {
                return -1;
            }
            if (o2.instance instanceof Clock) {
                return 1;
            }
            if (idx1 > -1 || idx2 > -1) {
                return idx2 - idx1;
            }
            return comparator.compare((o1.fqn + o1.name), (o2.fqn + o2.name));
        });
    }

    private void generatePropertyAssignments() {
        nodeFields.forEach(f -> {
            try {
                final Object field = f.instance;
                LOGGER.debug("mapping property mutators for var:{}", f.name);
                List<String> properties = stream(Introspector.getBeanInfo(f.instance.getClass()).getPropertyDescriptors())
                        .filter((PropertyDescriptor p) -> p.getWriteMethod() != null)
                        .filter((PropertyDescriptor p) -> fieldSerializer.propertySupported(p, f, nodeFields))
                        .filter(p -> {
                            boolean isConstructorArg = false;
                            try {
                                isConstructorArg = null != ClassUtils.getReflectField(field.getClass(), p.getName()).getAnnotation(ConstructorArg.class);
                            } catch (NoSuchFieldException ex) {
                                LOGGER.warn("cannot process field for ConstructorArg annotation", ex);
                            }
                            return !isConstructorArg;
                        })
                        .map(p -> fieldSerializer.mapPropertyToJavaSource(p, f, nodeFields, importClasses))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                LOGGER.debug("{} properties:{}", f.name, properties);
                beanPropertyMap.put(field, properties);
            } catch (IntrospectionException ex) {
                LOGGER.warn("could not process bean properties", ex);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void generateComplexConstructors() {
        nodeFields.forEach(f -> {
            HashSet<Field.MappedField> privateFields = new HashSet<>();
            final Object field = f.instance;
            LOGGER.debug("mapping constructor for var:{} {}", f.name, f);
            List<?> directParents = dependencyGraph.getDirectParents(field);
            Field.MappedField[] cstrArgList = new Field.MappedField[(directParents.size()) + 200];
            Class<?> fieldClass = field.getClass();
            boolean[] hasCstrAnnotations = new boolean[]{false};
            Set<String> assignedFieldNames = ReflectionUtils.getConstructors(fieldClass).stream()
                    .map(Constructor::getParameters)
                    .flatMap(Arrays::stream)
                    .filter(p -> p.getAnnotation(AssignToField.class) != null)
                    .map(p -> p.getAnnotation(AssignToField.class).value())
                    .collect(Collectors.toSet());

            ReflectionUtils.getAllFields(fieldClass, (java.lang.reflect.Field input) -> {
                final boolean isCstrArg = Objects.requireNonNull(input).getAnnotation(ConstructorArg.class) != null;
                //TODO check is not public
                String fieldName = input.getName();
                if ((isCstrArg || assignedFieldNames.contains(fieldName)) && !Modifier.isStatic(input.getModifiers())) {
                    hasCstrAnnotations[0] = true;
                    LOGGER.debug("field marked as constructor arg: {}", fieldName);
                    LOGGER.debug("hasCstrAnnotations:" + hasCstrAnnotations[0]);
                } else if (input.getAnnotation(FluxtionIgnore.class) != null) {
                    return false;
                } else if (Modifier.isStatic(input.getModifiers()) || !Modifier.isFinal(input.getModifiers()) || Modifier.isTransient(input.getModifiers())) {
//                if (Modifier.isStatic(input.getModifiers()) || (Modifier.isPublic(input.getModifiers()) && !Modifier.isFinal(input.getModifiers()))) {
                    LOGGER.debug("ignoring field:{} public:{} final:{} transient:{} static:{}",
                            fieldName,
                            Modifier.isPublic(input.getModifiers()),
                            Modifier.isFinal(input.getModifiers()),
                            Modifier.isTransient(input.getModifiers()),
                            Modifier.isStatic(input.getModifiers())
                    );
                    return false;
                }
                try {
                    if (!TopologicallySortedDependencyGraph.trySetAccessible(input)) {
                        return false;
                    }
                    final Object parent = input.get(field);
                    if (parent == null) {
                        return false;
                    }
                    if (directParents.contains(parent)) {
                        final Field.MappedField mappedField = new Field.MappedField(fieldName, getFieldForInstance(parent));
                        mappedField.derivedVal = fieldSerializer.mapToJavaSource(input.get(field), nodeFields, importClasses);
                        privateFields.add(mappedField);
                    } else if (List.class.isAssignableFrom(parent.getClass()) || Set.class.isAssignableFrom(parent.getClass())) {
                        //
                        Class collectionClass = List.class.isAssignableFrom(parent.getClass()) ? List.class : Set.class;
                        Field.MappedField collectionField = new Field.MappedField(fieldName, collectionClass);
                        Collection<?> collection = (Collection<?>) parent;
                        for (Object element : collection) {
                            collectionField.addField(getFieldForInstance(element));
                        }
                        collectionField.derivedVal = fieldSerializer.mapToJavaSource(parent, nodeFields, importClasses);
                        if (!collectionField.isEmpty() || collectionField.derivedVal.length() > 1) {
                            privateFields.add(collectionField);
                            LOGGER.debug("collection field:{}, val:{}", fieldName, input.get(field));
                        }
                    } else if (fieldSerializer.typeSupported(input.getType())) {
                        LOGGER.debug("primitive field:{}, val:{}", fieldName, input.get(field));
                        Field.MappedField primitiveField = new Field.MappedField(fieldName, input.get(field));
                        primitiveField.derivedVal = fieldSerializer.mapToJavaSource(input.get(field), nodeFields, importClasses);
                        privateFields.add(primitiveField);
                    } else if (fieldSerializer.typeSupported(input.get(field).getClass())) {
                        LOGGER.debug("primitive field:{}, val:{}", fieldName, input.get(field));
                        Field.MappedField primitiveField = new Field.MappedField(fieldName, input.get(field));
                        primitiveField.derivedVal = fieldSerializer.mapToJavaSource(input.get(field), nodeFields, importClasses);
                        privateFields.add(primitiveField);
                    }
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    java.util.logging.Logger.getLogger(SimpleEventProcessorModel.class.getName()).log(Level.SEVERE, null, ex);
                }
                return false;
            });

            if (privateFields.isEmpty() & !hasCstrAnnotations[0]) {
                LOGGER.debug("{}:default constructor applicable", f.name);
//                continue;
            } else {
                LOGGER.debug("{}:match complex constructor private fields:{}", f.name, privateFields);
                if (ReflectionUtils.getConstructors(fieldClass, matchConstructorNameAndType(cstrArgList, privateFields)).isEmpty()) {
                    Set<Constructor> constructors = ReflectionUtils.getConstructors(fieldClass, matchConstructorType(cstrArgList, privateFields));
                    if (constructors.isEmpty()) {
                        throw new RuntimeException("cannot find matching constructor for:" + f
                                + " failed to match for these fields:" + privateFields.stream()
                                .map(MappedField::getMappedName)
                                .collect(Collectors.joining(", ", "[", "]")));
                    }
                    List<String> fieldsThatClash = validateNoTypeClash(privateFields, constructors.iterator().next());
                    if (!fieldsThatClash.isEmpty()) {
                        throw new RuntimeException(
                                "cannot find matching constructor for:" + f
                                        + " use @" + AssignToField.class.getSimpleName()
                                        + " to resolve clashing types these fields:"
                                        + fieldsThatClash.stream().collect(Collectors.joining(", ", "[", "]")));
                    }
                }
                List<Field.MappedField> collect = Arrays.stream(cstrArgList).filter(Objects::nonNull).collect(Collectors.toList());
                constructorArgumentMap.put(field, collect);
            }
        });
    }

    public List<Field.MappedField> constructorArgs(Object field) {
        List<Field.MappedField> args = constructorArgumentMap.get(field);
        return args == null ? Collections.emptyList() : args;
    }

    public List<String> beanProperties(Object field) {
        List<String> args = beanPropertyMap.get(field);
        return args == null ? Collections.emptyList() : args;
    }

    private void lifeCycleHandlers() throws Exception {
        Map<Object, String> inst2Name = dependencyGraph.getInstanceMap();
        List<Object> topologicalHandlers = dependencyGraph.getSortedDependents();
        List<Object> objectTopologicalHandler = dependencyGraph.getObjectSortedDependents();
        Multimap<Object, CbMethodHandle> parentListenerMultiMap = HashMultimap.create();
        Multimap<Object, CbMethodHandle> parentListenerMultiMapUnmatched = HashMultimap.create();

        /*
          Add inexact match
         */
        for (Object object : objectTopologicalHandler) {
            String name = inst2Name.get(object);
            Method[] methodList = object.getClass().getMethods();
            for (Method method : methodList) {
                if (annotationInHierarchy(method, Initialise.class)) {
                    initialiseMethods.add(new CbMethodHandle(method, object, name));
                    if (LOGGER.isDebugEnabled()) {
                        final String validCb = name + "." + method.getName() + "()";
                        LOGGER.debug("initialise call back : " + validCb);
                    }
                }
                if (annotationInHierarchy(method, Start.class)) {
                    startMethods.add(new CbMethodHandle(method, object, name));
                    if (LOGGER.isDebugEnabled()) {
                        final String validCb = name + "." + method.getName() + "()";
                        LOGGER.debug("start call back : " + validCb);
                    }
                }
                if (annotationInHierarchy(method, StartComplete.class)) {
                    startCompleteMethods.add(new CbMethodHandle(method, object, name));
                    if (LOGGER.isDebugEnabled()) {
                        final String validCb = name + "." + method.getName() + "()";
                        LOGGER.debug("startComplete call back : " + validCb);
                    }
                }
                if (annotationInHierarchy(method, TearDown.class)) {
                    tearDownMethods.add(0, new CbMethodHandle(method, object, name));
                    if (LOGGER.isDebugEnabled()) {
                        final String validCb = name + "." + method.getName() + "()";
                        LOGGER.debug("tear down call back : " + validCb);
                    }
                }
                if (annotationInHierarchy(method, Stop.class)) {
                    stopMethods.add(0, new CbMethodHandle(method, object, name));
                    if (LOGGER.isDebugEnabled()) {
                        final String validCb = name + "." + method.getName() + "()";
                        LOGGER.debug("stop call back : " + validCb);
                    }
                }
            }
        }


        for (Object object : topologicalHandlers) {
            String name = inst2Name.get(object);
            Method[] methodList = object.getClass().getMethods();
            for (Method method : methodList) {
                if (annotationInHierarchy(method, OnBatchEnd.class)) {
                    //revered for the batch callbacks
                    batchEndMethods.add(0, new CbMethodHandle(method, object, name));
                    if (LOGGER.isDebugEnabled()) {
                        final String validCb = name + "." + method.getName() + "()";
                        LOGGER.debug("batch end call back : " + validCb);
                    }
                }
                if (annotationInHierarchy(method, OnBatchPause.class)) {
                    //revered for the batch callbacks
                    batchPauseMethods.add(0, new CbMethodHandle(method, object, name));
                    if (LOGGER.isDebugEnabled()) {
                        final String validCb = name + "." + method.getName() + "()";
                        LOGGER.debug("batch pause call back : " + validCb);
                    }
                }
                if (annotationInHierarchy(method, AfterEvent.class)) {
                    //revered for the batch callbacks
                    eventEndMethods.add(0, new CbMethodHandle(method, object, name));
                    if (LOGGER.isDebugEnabled()) {
                        final String validCb = name + "." + method.getName() + "()";
                        LOGGER.debug("event end call back : " + validCb);
                    }
                }

                if (annotationInHierarchy(method, OnTrigger.class)) {
                    node2UpdateMethodMap.put(object, new CbMethodHandle(method, object, name));
                }

                if (method.getAnnotation(OnEventHandler.class) != null) {
                    node2UpdateMethodMap.put(object, new CbMethodHandle(method, object, name));
                }

                if (method.getAnnotation(OnParentUpdate.class) != null) {
                    final CbMethodHandle cbMethodHandle = new CbMethodHandle(method, object, name);
                    String val = method.getAnnotation(OnParentUpdate.class).value();
                    if (method.getParameterTypes().length != 1) {
                        final String errorMsg = "Cannot create OnParentUpdate callback method must have a single parameter "
                                + cbMethodHandle;
                        LOGGER.error(errorMsg);
                        throw new RuntimeException(errorMsg);
                    }
                    ParentFilter filter = new ParentFilter(method.getParameterTypes()[0], val, cbMethodHandle);

                    if (val != null && val.length() > 0) {
                        java.lang.reflect.Field field;
                        field = ClassUtils.getReflectField(object.getClass(), val);
                        field.setAccessible(true);
                        if (field.getAnnotation(NoTriggerReference.class) != null || field.getAnnotation(PushReference.class) != null) {
                            LOGGER.debug("IGNORING NoEventReference for parentUpdate");
                            //continue;
                        }
                        if (field.get(object) != null) {
                            Object parent = field.get(object);
                            //array
                            if (field.getType().isArray()) {
                                final Class<?> classType = field.getType().getComponentType();
                                int length = Array.getLength(parent);
                                for (int i = 0; i < length; i++) {
                                    ParentFilter testFilter = new ParentFilter(classType, val, null);
                                    if (testFilter.match(filter)) {
                                        //store in exact match map for merging later
                                        parentListenerMultiMap.put(Array.get(parent, i), cbMethodHandle);
                                    }
                                }
                            }
                            //list
                            if (Collection.class.isAssignableFrom(field.getType())) {
                                ParameterizedType integerListType = (ParameterizedType) field.getGenericType();
                                Class<?> classTypeX = Object.class;
                                if (integerListType.getActualTypeArguments()[0] instanceof Class) {
                                    classTypeX = (Class<?>) integerListType.getActualTypeArguments()[0];
                                }
                                final Class<?> classType = classTypeX;
                                @SuppressWarnings("unchecked") Collection<Object> list = (Collection<Object>) field.get(object);
                                list.forEach((parent1) -> {
                                    ParentFilter testFilter = new ParentFilter(classType, val, null);
                                    if (testFilter.match(filter)) {
                                        parentListenerMultiMap.put(parent1, cbMethodHandle);
                                    }
                                });
                            }
                            //scalar
                            ParentFilter testFilter = new ParentFilter(parent.getClass(), val, null);
                            if (testFilter.exactmatch(filter)) {
                                //store in exact match map for merging later
                                parentListenerMultiMap.put(parent, cbMethodHandle);
                            } /*
                              Add inexact match
                             */ else if (testFilter.match(filter)) {
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
        for (Object parent : topologicalHandlers) {
            parentUpdateListenerMethodMap.put(parent, new ArrayList<>());
            List<?> directChildren = dependencyGraph.getDirectChildren(parent);
            Collection<CbMethodHandle> childCbList = parentListenerMultiMap.get(parent);
            Set<Object> mappedCbs = childCbList.stream().map(cb -> cb.instance).collect(Collectors.toSet());
            directChildren.stream()
                    .filter((child) -> !mappedCbs.contains(child))
                    .map(parentListenerMultiMapUnmatched::get)
                    .map((cbs) -> ClassUtils.findBestParentCB(parent, cbs))
                    .filter(Objects::nonNull)
                    .forEach((bestParentCB) -> parentListenerMultiMap.put(parent, bestParentCB));
        }
        parentListenerMultiMap.keySet().forEach((parent) ->
                parentUpdateListenerMethodMap.put(parent, new ArrayList<>(parentListenerMultiMap.get(parent)))
        );
        parentUpdateListenerMethodMap.values().forEach(dependencyGraph::sortNodeList);
    }

    private void eventHandlers() throws Exception {
        List<Object> topologicalHandlers = dependencyGraph.getSortedDependents();
        List<EventCallList> eventCbList = new ArrayList<>();
        for (Object object : topologicalHandlers) {
            String name = dependencyGraph.getInstanceMap().get(object);
            if (object instanceof EventHandlerNode) {
                eventCbList.add(new EventCallList((EventHandlerNode<?>) object));
            }
            Method[] methodList = object.getClass().getMethods();
            for (Method method : methodList) {
                if (method.getAnnotation(OnEventHandler.class) != null) {
                    eventCbList.add(new EventCallList(object, method));
                }
            }
            Class<?> clazz = object.getClass();
            //exported services
            for (AnnotatedType annotatedInterface : ClassUtils.getAllAnnotatedAnnotationTypes(clazz, ExportService.class)) {
                if (annotatedInterface.isAnnotationPresent(ExportService.class)) {
                    Class<?> interfaceType = (Class<?>) annotatedInterface.getType();
                    boolean propagateClass = ClassUtils.isPropagatingExportService(clazz, interfaceType);
                    dependencyGraph.getConfig().addInterfaceImplementation(interfaceType);
                    for (Method method : interfaceType.getMethods()) {
                        String exportMethodName = method.getName();
                        try {
                            method = object.getClass().getMethod(exportMethodName, method.getParameterTypes());
                            Method cbMethod = method;
                            try {
                                cbMethod = object.getClass().getMethod(exportMethodName, method.getParameterTypes());
                            } catch (NoSuchMethodException e) {

                            }

                            boolean noPropagateMethod = cbMethod.getAnnotation(NoPropagateFunction.class) != null
                                    || !propagateClass;
                            LongAdder argNumber = new LongAdder();
                            boolean booleanReturn = method.getReturnType() == boolean.class;
                            StringBuilder signature = booleanReturn
                                    ? new StringBuilder("@Override\npublic boolean " + exportMethodName)
                                    : new StringBuilder("@Override\npublic void " + exportMethodName);
                            signature.append('(');
                            StringJoiner sj = new StringJoiner(", ");
                            Type[] params = method.getGenericParameterTypes();
                            for (int j = 0; j < params.length; j++) {
                                String param = params[j].getTypeName()
                                        .replace("$", ".")
                                        .replace("java.lang.", "");
                                if (method.isVarArgs() && (j == params.length - 1)) // replace T[] with T...
                                    param = param.replaceFirst("\\[\\]$", "...");
                                param += " arg" + argNumber.intValue();
                                sj.add(param);
                                argNumber.increment();
                            }
                            signature.append(sj);
                            signature.append(")");
                            eventCbList.add(new EventCallList(object, method, signature.toString(), !noPropagateMethod));
                            if (!noPropagateMethod) {
                                node2UpdateMethodMap.put(object, new CbMethodHandle(method, object, name));
                            }
                        } catch (NoSuchMethodException e) {

                        }
                    }
                }
            }
        }


        //build the no filter handlers - ready to merge in with the filtered lists
        for (EventCallList eventCb : eventCbList) {
            if (eventCb.isFiltered) {
                continue;
            }
            Class<?> eventClass = eventCb.eventTypeClass;
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
        Set<Class<?>> eventClassSet = dispatchMap.keySet();
        for (Class<?> eventClass : eventClassSet) {
            Map<FilterDescription, List<CbMethodHandle>> handlerMap = getHandlerMap(eventClass);
            List<CbMethodHandle> noFilterList = handlerMap.get(FilterDescription.NO_FILTER) == null ?
                    Collections.emptyList() : handlerMap.get(FilterDescription.NO_FILTER);
            List<CbMethodHandle> inverseList = handlerMap.get(FilterDescription.INVERSE_FILTER) == null ?
                    Collections.emptyList() : handlerMap.get(FilterDescription.INVERSE_FILTER);
            HashSet<CbMethodHandle> set = new HashSet<>(inverseList);
            set.addAll(noFilterList);
            if (set.size() > 0) {
                List<CbMethodHandle> callList = new ArrayList<>(set);
                dependencyGraph.sortNodeList(callList);
                handlerMap.put(FilterDescription.DEFAULT_FILTER, callList);
            }
            //postHandler
            handlerMap = getPostHandlerMap(eventClass);
            noFilterList = handlerMap.get(FilterDescription.NO_FILTER) == null ?
                    Collections.emptyList() : handlerMap.get(FilterDescription.NO_FILTER);
            inverseList = handlerMap.get(FilterDescription.INVERSE_FILTER) == null ?
                    Collections.emptyList() : handlerMap.get(FilterDescription.INVERSE_FILTER);
            set = new HashSet<>(inverseList);
            set.addAll(noFilterList);
            if (set.size() > 0) {
                List<CbMethodHandle> callList = new ArrayList<>(set);
                dependencyGraph.sortNodeList(callList);
                Collections.reverse(callList);
                handlerMap.put(FilterDescription.DEFAULT_FILTER, callList);
            }
        }

        //loop through the eventCbList and create the dispatch Map
        for (EventCallList eventCb : eventCbList) {
            int filterId = eventCb.filterId;
            String filterString = eventCb.filterString;
            boolean isIntFilter = eventCb.isIntFilter;
            boolean isFiltering = eventCb.isFiltered;
            @SuppressWarnings("unchecked") Class<? extends Event> eventClass = (Class<? extends Event>) eventCb.eventTypeClass;
            final FilterDescription filterDescription;
            if (isIntFilter && isFiltering) {
                filterDescription = filterProducer.getFilterDescription(eventClass, filterId);
            } else if (isFiltering) {
                filterDescription = filterProducer.getFilterDescription(eventClass, filterString);
            } else {
                //Ignore as this non-filtered dispatch - already resolved
                continue;
            }
            filterDescription.setExportFunction(eventCb.exportMethod);
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
        buildSubClassHandlers();
        buildGlobalDispatchList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(dispatchMapToString());
        }
    }

    private void buildGlobalDispatchList() {
        allEventCallBacks = dispatchMap.values().stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .flatMap(List::stream).distinct().collect(Collectors.toList());
        dependencyGraph.sortNodeList(allEventCallBacks);

        allPostEventCallBacks = postDispatchMap.values().stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .flatMap(List::stream).distinct().collect(Collectors.toList());
        dependencyGraph.sortNodeList(allPostEventCallBacks);
        Collections.reverse(allPostEventCallBacks);

        handlerOnlyDispatchMap = new HashMap<>();
        Set<Class<?>> keySet = dispatchMap.keySet();
        HashSet<Class<?>> classSet = new HashSet<>(keySet);
        ArrayList<Class<?>> clazzList = new ArrayList<>(classSet);
        clazzList.sort(Comparator.comparing(Class::getName));

        for (Class evenClazz : clazzList) {
            Map<FilterDescription, List<CbMethodHandle>> originalFilterMap = dispatchMap.get(evenClazz);
            HashMap<FilterDescription, List<CbMethodHandle>> filterDispatchMap = new HashMap<>();
            handlerOnlyDispatchMap.put(evenClazz, filterDispatchMap);
            originalFilterMap.forEach((filter, cbList) -> {
                filterDispatchMap.put(
                        filter,
                        cbList.stream()
                                .filter(cb -> cb.isEventHandler() || cb.isNoPropagateEventHandler())
                                .collect(Collectors.toList())
                );
            });
        }
    }

    public List<?> getDirectChildrenListeningForEvent(Object parent) {
        return dependencyGraph.getDirectChildrenListeningForEvent(parent);
    }

    @SuppressWarnings("unchecked")
    private void buildSubClassHandlers() {
        Set<Class<?>> eventClassSet = dispatchMap.keySet();
        for (Class<?> eventClass : eventClassSet) {
            LOGGER.debug("------- START superclass merge Class:" + eventClass.getSimpleName() + " START -----------");
            Map<FilterDescription, List<CbMethodHandle>> targetHandlerMap = getHandlerMap(eventClass);
            LOGGER.debug("targetHandlerMap before merge:{}", targetHandlerMap);
            dispatchMap.entrySet().stream().filter((Map.Entry<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> e) -> {
                        Class<?> key = e.getKey();
                        final boolean match = key.isAssignableFrom(eventClass) && eventClass != key;
                        if (match) {
                            LOGGER.debug(key.getSimpleName() + " IS superclass of:" + eventClass.getSimpleName());
                        } else {
                            LOGGER.debug(key.getSimpleName() + " NOT superclass of:" + eventClass.getSimpleName());
                        }
                        return match;
                    })
                    .map(Map.Entry::getValue)
                    .map((Map<FilterDescription, List<CbMethodHandle>> e) -> {
                        HashMap<FilterDescription, List<CbMethodHandle>> newMap = new HashMap<>();
                        e.forEach((key, value) -> newMap.put(key.changeClass((Class<? extends Event>) eventClass), value));
                        return newMap;
                    })
                    .forEach(fd -> fd.forEach((key, callList) -> targetHandlerMap.merge(key, callList, (List<CbMethodHandle> t, List<CbMethodHandle> u) -> {
                        LOGGER.debug("merging:{}", u);
                        t.removeAll(u);
                        t.addAll(u);
                        dependencyGraph.sortNodeList(t);
                        return t;
                    })));
            LOGGER.debug("targetHandlerMap after merge:{}", targetHandlerMap);
            LOGGER.debug("------- END superclass merge Class:" + eventClass.getSimpleName() + " END -----------\n");
        }

    }

    private Map<FilterDescription, List<CbMethodHandle>> getHandlerMap(Class<?> eventClass) {
        return dispatchMap.computeIfAbsent(eventClass, k -> new HashMap<>());
    }

    private Map<FilterDescription, List<CbMethodHandle>> getPostHandlerMap(Class<?> eventClass) {
        return postDispatchMap.computeIfAbsent(eventClass, k -> new HashMap<>());
    }

    private boolean noDirtyFlagNeeded(Field node) {
        boolean notRequired = dependencyGraph.getDirectChildrenListeningForEvent(node.instance).isEmpty()
                && parentUpdateListenerMethodMap.get(node.instance).isEmpty();
        Method[] methodList = node.instance.getClass().getDeclaredMethods();
        for (Method method : methodList) {
            if (annotationInHierarchy(method, AfterTrigger.class)) {
                notRequired = false;
            }
        }
        return notRequired;
    }

    private void buildDirtySupport() throws Exception {
        if (supportDirtyFiltering()) {
            for (Field node : nodeFields) {
                if (noDirtyFlagNeeded(node)) {
                    continue;
                }
                CbMethodHandle cbHandle = node2UpdateMethodMap.get(node.instance);
                if (cbHandle != null && cbHandle.method.getReturnType() == boolean.class) {
                    DirtyFlag flag = new DirtyFlag(node, "isDirty_" + node.name);
                    dirtyFieldMap.put(node, flag);
                } else if (cbHandle != null && cbHandle.method.getReturnType() == void.class) {
                    DirtyFlag flag = new DirtyFlag(node, "isDirty_" + node.name, true);
                    dirtyFieldMap.put(node, flag);
                }
            }
            //build the guard conditions for nodes. loop in topological order
            for (Object node : dependencyGraph.getSortedDependents()) {
                List<?> directParents = dependencyGraph.getDirectParentsListeningForEvent(node);
                if (directParents.isEmpty()) {
                    continue;
                }
                CbMethodHandle cb = node2UpdateMethodMap.get(node);
                final boolean invertedDirtyHandler = cb != null && cb.invertedDirtyHandler;
                final boolean failIfNotGuarded = cb != null && cb.failBuildOnUnguardedTrigger();
                //get parents of node and loop through
                Set<DirtyFlag> guardSet = new HashSet<>();
                for (Object parent : directParents) {
                    //get dirty field for node
                    DirtyFlag parentDirtyFlag = getDirtyFlagForUpdateCb(node2UpdateMethodMap.get(parent));
                    DirtyFlag methodFlag = null;
                    if (parentDirtyFlag != null) {
                        parentDirtyFlag.requiresInvert |= invertedDirtyHandler;
                        methodFlag = parentDirtyFlag.clone();
                        methodFlag.requiresInvert = invertedDirtyHandler;
                    }
                    //get the guards for the parent using the multimap
                    Collection<DirtyFlag> parentDirtyFlags = nodeGuardMap.get(parent);
                    //if parent guard != null add as a guard to multimap, continue
                    //else if guards!=null add to multimap, continue
                    //else clear mutlimap, break
                    if (methodFlag != null) {
                        guardSet.add(methodFlag);
                    } else if (!parentDirtyFlags.isEmpty()) {
                        guardSet.addAll(parentDirtyFlags);
                    } else {
                        guardSet.clear();
                        break;
                    }
                }
                if (failIfNotGuarded && guardSet.isEmpty()) {
                    String failMessage = "Failed guard check for trigger method:" + cb;
                    throw new RuntimeException(failMessage);
                }
                nodeGuardMap.putAll(node, guardSet);
            }

        }
    }

    private void filterList() {
        Set<FilterDescription> uniqueFilterSet = new HashSet<>();
        for (Map<FilterDescription, List<CbMethodHandle>> value : dispatchMap.values()) {
            uniqueFilterSet.addAll(value.keySet());
        }
        for (Map<FilterDescription, List<CbMethodHandle>> value : postDispatchMap.values()) {
            uniqueFilterSet.addAll(value.keySet());
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
     * <p>
     * Parents can be traced all the way to the root for dirty support,
     * effectively inheriting dirty support down the call tree.
     *
     * @param node the node to introspect
     * @return collection of dirty flags that guard the node
     */
    public Collection<DirtyFlag> getNodeGuardConditions(Object node) {
        final ArrayList<DirtyFlag> guards = new ArrayList<>(nodeGuardMap.get(node));
        guards.sort((DirtyFlag o1, DirtyFlag o2) -> comparator.compare(o1.name, o2.name));
        return guards;
    }

    /**
     * Provides a list of guard conditions for a node, but only if
     * supportDirtyFiltering is configured and all of the parents of the node
     * support the dirty flag.If any parent does not support the dirty flag then
     * the node updated method will always be called after a parent has been
     * notified of an event.
     *
     * @param cb method callback
     * @return collection of dirty flags that guard the node
     */
    public Collection<DirtyFlag> getNodeGuardConditions(CbMethodHandle cb) {
        if (cb.postEventHandler && dependencyGraph.getDirectParents(cb.instance).isEmpty()) {
            return getDirtyFlagForNode(cb.instance) == null
                    ? Collections.emptyList()
                    : Collections.singletonList(getDirtyFlagForNode(cb.instance));
        }
        return cb.isEventHandler ? Collections.emptySet() : getNodeGuardConditions(cb.instance);
    }

    public DirtyFlag getDirtyFlagForUpdateCb(CbMethodHandle cbHandle) {
        DirtyFlag flag = null;
        if (supportDirtyFiltering() && cbHandle != null) {
            flag = dirtyFieldMap.get(getFieldForInstance(cbHandle.instance));
            if (cbHandle.method.getReturnType() != boolean.class && flag != null) {
                //trap the case where eventhandler and onEvent in same class
                //and onEvent does not return true
                flag.alwaysDirty = true;
            }
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

    public Field getFieldForName(String name) {
        return nodeFields.stream().filter(f -> f.name.equals(name)).findFirst().orElse(null);
    }

    /**
     * returns all the {@link OnTrigger} {@link CbMethodHandle}'s that depend upon this node.
     *
     * @return dependents that will be notified with methods @{@link OnTrigger}
     */
    public Set<Object> getOnTriggerDependenciesForNode(CbMethodHandle callSource) {
        if (callSource.isNoPropagateEventHandler()) {
            return Collections.emptySet();
        }
        return getOnTriggerDependenciesForNode(callSource.getInstance());
    }

    @SuppressWarnings("unchecked")
    public Set<Object> getOnTriggerDependenciesForNode(Object instance) {
        return getDirectChildrenListeningForEvent(instance).stream()
                .peek(o -> log.debug("checking for OnEvent instance:{}", o))
                .filter(object -> !ReflectionUtils.getAllMethods(object.getClass(), ReflectionUtils.withAnnotation(OnTrigger.class)).isEmpty())
                .collect(Collectors.toSet());
    }

    public String getMappedClass(String className) {
        if (dependencyGraph == null || dependencyGraph.getConfig() == null) {
            return className;
        }
        return dependencyGraph.getConfig().getClass2replace().getOrDefault(className, className);
    }

    private boolean supportDirtyFiltering() {
        return supportDirtyFiltering;
    }

    public List<Field> getNodeFields() {
        return Collections.unmodifiableList(nodeFields);
    }

    public List<Field> getTopologicallySortedNodeFields() {
        return Collections.unmodifiableList(nodeFieldsSortedTopologically);
    }

    public List<Field> getNodeRegistrationListenerFields() {
        return Collections.unmodifiableList(registrationListenerFields);
    }

    public List<CbMethodHandle> getInitialiseMethods() {
        return Collections.unmodifiableList(initialiseMethods);
    }

    public List<CbMethodHandle> getStartMethods() {
        return Collections.unmodifiableList(startMethods);
    }

    public List<CbMethodHandle> getStartCompleteMethods() {
        return Collections.unmodifiableList(startCompleteMethods);
    }

    public List<CbMethodHandle> getStopMethods() {
        return Collections.unmodifiableList(stopMethods);
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

    public List<CbMethodHandle> getDispatchMapForGraph() {
        return Collections.unmodifiableList(allEventCallBacks);
    }

    public List<CbMethodHandle> getAllPostEventCallBacks() {
        return Collections.unmodifiableList(allPostEventCallBacks);
    }

    public List<CbMethodHandle> getTriggerOnlyCallBacks() {
        if (triggerOnlyCallBacks == null) {
            triggerOnlyCallBacks = Collections.unmodifiableList(allEventCallBacks.stream()
                    .filter(cb -> !(cb.isEventHandler() || cb.isNoPropagateEventHandler()))
                    .collect(Collectors.toList()));
        }
        return triggerOnlyCallBacks;
    }

    public Set<Object> getForkedTriggerInstances() {
        if (forkedTriggerInstances == null) {
            forkedTriggerInstances = Collections.unmodifiableSet(getTriggerOnlyCallBacks().stream()
                    .filter(CbMethodHandle::isForkExecution)
                    .map(CbMethodHandle::getInstance)
                    .collect(Collectors.toSet()));
        }
        return forkedTriggerInstances;
    }

    public Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> getDispatchMap() {
        return Collections.unmodifiableMap(dispatchMap);
    }

    public Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> getPostDispatchMap() {
        return Collections.unmodifiableMap(postDispatchMap);
    }

    public Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> getHandlerOnlyDispatchMap() {
        return Collections.unmodifiableMap(handlerOnlyDispatchMap);
    }

    public Map<Method, ExportFunctionData> getExportedFunctionMap() {
        return Collections.unmodifiableMap(dependencyGraph.getExportedFunctionMap());
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

    public FieldSerializer getFieldSerializer() {
        return fieldSerializer;
    }

    public Set<Class<?>> getImportClasses() {
        return Collections.unmodifiableSet(importClasses);
    }

    private String dispatchMapToString() {
        StringBuilder result = new StringBuilder("DispatchMap[\n");

        Set<Class<?>> keySet = dispatchMap.keySet();
        for (Class<?> eventId : keySet) {
            result.append("\tEvent Id:").append(eventId).append("\n");
            Map<FilterDescription, List<CbMethodHandle>> cbMap = dispatchMap.get(eventId);
            Set<FilterDescription> filterIdSet = cbMap.keySet();
            for (FilterDescription filterDescription : filterIdSet) {
                int filterId = filterDescription.value;
                result.append("\t\tFilter Id:").append(filterId).append("\n");
                List<CbMethodHandle> cbList = cbMap.get(filterDescription);
                for (CbMethodHandle cbMethod : cbList) {
                    result.append("\t\t\t").append(cbMethod).append("\n");
                }
            }
        }
        result.append("]\n");

        //post dispatch
        result.append("PostDispatchMap[\n");
        keySet = postDispatchMap.keySet();
        for (Class<?> eventId : keySet) {
            result.append("\tEvent Id:").append(eventId).append("\n");
            Map<FilterDescription, List<CbMethodHandle>> cbMap = dispatchMap.get(eventId);
            Set<FilterDescription> filterIdSet = cbMap.keySet();
            for (FilterDescription filterDescription : filterIdSet) {
                int filterId = filterDescription.value;
                result.append("\t\tFilter Id:").append(filterId).append("\n");
                List<CbMethodHandle> cbList = cbMap.get(filterDescription);
                for (CbMethodHandle cbMethod : cbList) {
                    result.append("\t\t\t").append(cbMethod).append("\n");
                }
            }
        }
        result.append("]");
        return result.toString();
    }

    /**
     * A helper class, holds the call tree and meta-data for an event type
     */
    private class EventCallList {

        final int filterId;
        final String filterString;
        final boolean isIntFilter;
        final boolean isFiltered;
        final boolean isInverseFiltered;
        final Class<?> eventTypeClass;
        final Method exportMethod;
        private final List<?> sortedDependents;
        private final List<CbMethodHandle> dispatchMethods;
        /**
         * the set of methods to be called on a unwind of an event annotated
         * with {@link AfterTrigger}
         */
        private final List<CbMethodHandle> postDispatchMethods;

        EventCallList(EventHandlerNode<?> eh) throws Exception {
            if (filterMap.containsKey(eh)) {
                filterId = filterMap.get(eh);
            } else {
                filterId = eh.filterId();
            }
            sortedDependents = dependencyGraph.getEventSortedDependents(eh);
            dispatchMethods = new ArrayList<>();
            postDispatchMethods = new ArrayList<>();
            exportMethod = null;
            if (eh.eventClass() == null) {
                eventTypeClass = (TypeResolver.resolveRawArguments(EventHandlerNode.class, eh.getClass()))[0];
            } else {
                eventTypeClass = eh.eventClass();
            }
            @SuppressWarnings("unchecked") Set<Method> ehMethodList = ReflectionUtils.getAllMethods(eh.getClass(),
                    ReflectionUtils.withModifier(Modifier.PUBLIC)
                            .and(ReflectionUtils.withName("onEvent"))
                            .and(ReflectionUtils.withParametersCount(1))
            );
            Method onEventMethod = ehMethodList.iterator().next();
            String name = dependencyGraph.variableName(eh);
            final CbMethodHandle cbMethodHandle = new CbMethodHandle(onEventMethod, eh, name, eventTypeClass, true, false);
            dispatchMethods.add(cbMethodHandle);
            node2UpdateMethodMap.put(eh, cbMethodHandle);
            for (int i = 1; i < sortedDependents.size(); i++) {
                Object object = sortedDependents.get(i);
                if (object == eh) {
                    continue;
                }
                name = dependencyGraph.variableName(object);
                Method[] methodList = object.getClass().getMethods();
                for (Method method : methodList) {
                    if (annotationInHierarchy(method, OnTrigger.class)) {
                        dispatchMethods.add(new CbMethodHandle(method, object, name));
                    }
                    if (annotationInHierarchy(method, AfterTrigger.class)) {
                        postDispatchMethods.add(new CbMethodHandle(method, object, name));
                    }
                }
            }
            filterString = eh.filterString();
            boolean isStrFilter = filterString != null && !filterString.isEmpty();
            isIntFilter = filterId != Event.NO_INT_FILTER;
            isFiltered = filterId != Event.NO_INT_FILTER || isStrFilter;
            isInverseFiltered = false;
        }

        EventCallList(Object instance, Method onEventMethod, String exportedMethodName, boolean propagate) throws Exception {
            if (propagate) {
                sortedDependents = dependencyGraph.getEventSortedDependents(instance);
            } else {
                sortedDependents = Collections.EMPTY_LIST;
            }
            exportMethod = onEventMethod;
            dispatchMethods = new ArrayList<>();
            postDispatchMethods = new ArrayList<>();
            eventTypeClass = ExportFunctionMarker.class;
            filterId = 0;
            filterString = exportedMethodName;
            isIntFilter = false;
            isFiltered = true;
            isInverseFiltered = false;
            String name = dependencyGraph.variableName(instance);
            dispatchMethods.add(new CbMethodHandle(onEventMethod, instance, name, eventTypeClass, true, true));
            //check for @OnEventComplete on the root of the event tree
            Method[] methodList = instance.getClass().getMethods();
            for (Method method : methodList) {
                if (annotationInHierarchy(method, AfterTrigger.class)) {
                    postDispatchMethods.add(new CbMethodHandle(method, instance, name));
                }
            }

            for (int i = 0; i < sortedDependents.size(); i++) {
                Object object = sortedDependents.get(i);
                if (object == instance) {
                    continue;
                }
                name = dependencyGraph.variableName(object);
                methodList = object.getClass().getMethods();
                for (Method method : methodList) {
                    if (annotationInHierarchy(method, OnTrigger.class)) {
                        dispatchMethods.add(new CbMethodHandle(method, object, name));
                    }
                    if (annotationInHierarchy(method, AfterTrigger.class) && i > 0) {
                        postDispatchMethods.add(new CbMethodHandle(method, object, name));
                    }
                }
            }
        }

        @SuppressWarnings("unchecked")
        EventCallList(Object instance, Method onEventMethod) throws Exception {
            String tmpFilterString = null;
            int tmpFilterId = 0;
            boolean tmpIsIntFilter = true;
            boolean tmpIsFiltered = true;
            boolean tmpIsInverseFiltered = false;
            exportMethod = null;
            Set<java.lang.reflect.Field> fields = ReflectionUtils.getAllFields(instance.getClass(), ReflectionUtils.withAnnotation(FilterId.class));
            OnEventHandler annotation = onEventMethod.getAnnotation(OnEventHandler.class);
            //int attribute filter on annoatation 
            int filterIdOverride = annotation.filterId();
            String filterStringOverride = annotation.filterStringFromClass() != void.class ? annotation.filterStringFromClass().getCanonicalName() : annotation.filterString();
            Set<java.lang.reflect.Field> s = ReflectionUtils.getAllFields(instance.getClass(), ReflectionUtils.withName(annotation.filterVariable()));
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
                    } else {
                        filterStringOverride = f.get(instance).toString();
                    }
                }
            }
            boolean overrideFilter = filterIdOverride != Integer.MAX_VALUE;
            boolean overideStringFilter = filterStringOverride != null && !filterStringOverride.isEmpty();
            OptionalInt overrideMethodFilter = filterMap.entrySet().stream()
                    .filter(e -> e.getKey() instanceof EventHandlerFilterOverride)
                    .filter(e -> {
                        EventHandlerFilterOverride override = (EventHandlerFilterOverride) e.getKey();
                        return override.getEventHandlerInstance() == instance
                                && override.getEventType() == onEventMethod.getParameterTypes()[0];
                    })
                    .mapToInt(Entry::getValue)
                    .findFirst();
            if (overrideMethodFilter.isPresent()) {
                tmpFilterId = overrideMethodFilter.getAsInt();
            } else if (filterMap.containsKey(instance)) {
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
                tmpIsInverseFiltered = annotation.value() == FilterType.defaultCase;
            } else {
                java.lang.reflect.Field field = fields.iterator().next();
                field.setAccessible(true);
                Class<?> type = field.getType();
                if (type == int.class) {
                    tmpFilterId = field.getInt(instance);
                    tmpIsFiltered = tmpFilterId != Event.NO_INT_FILTER;
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
                sortedDependents = dependencyGraph.getEventSortedDependents(instance);
            } else {
                sortedDependents = Collections.EMPTY_LIST;
            }
            dispatchMethods = new ArrayList<>();
            postDispatchMethods = new ArrayList<>();
            eventTypeClass = annotation.ofType() == void.class ? onEventMethod.getParameterTypes()[0] : annotation.ofType();
            String name = dependencyGraph.variableName(instance);
            dispatchMethods.add(new CbMethodHandle(onEventMethod, instance, name, eventTypeClass, true, false));
            //check for @OnEventComplete on the root of the event tree
            Method[] methodList = instance.getClass().getMethods();
            for (Method method : methodList) {
                if (annotationInHierarchy(method, AfterTrigger.class)) {
                    postDispatchMethods.add(new CbMethodHandle(method, instance, name));
                }
            }

            for (int i = 0; i < sortedDependents.size(); i++) {
                Object object = sortedDependents.get(i);
                if (object == instance) {
                    continue;
                }
                name = dependencyGraph.variableName(object);
                methodList = object.getClass().getMethods();
                for (Method method : methodList) {
                    if (annotationInHierarchy(method, OnTrigger.class)) {
                        dispatchMethods.add(new CbMethodHandle(method, object, name));
                    }
                    if (annotationInHierarchy(method, AfterTrigger.class) && i > 0) {
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
