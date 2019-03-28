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
package com.fluxtion.generator.targets;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.google.common.base.Predicates;
import com.fluxtion.builder.generation.FilterDescription;
import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.generator.model.CbMethodHandle;
import com.fluxtion.generator.model.DirtyFlag;
import com.fluxtion.generator.model.Field;
import com.fluxtion.generator.model.InvokerFilterTarget;
import com.fluxtion.generator.model.SimpleEventProcessorModel;
import static com.fluxtion.generator.targets.JavaGenHelper.mapWrapperToPrimitive;
import com.fluxtion.generator.util.NaturalOrderComparator;
import com.fluxtion.api.audit.Auditor;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.list.dsl.MirrorList;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.reflections.ReflectionUtils;
import static org.reflections.ReflectionUtils.withModifier;
import static org.reflections.ReflectionUtils.withName;
import static org.reflections.ReflectionUtils.withType;

/**
 *
 * @author Greg Higgins
 */
public class SepJavaSourceModelHugeFilter {

    /**
     * max number of cases statements to generate before using a dispatch map.
     */
    private int maxFilterBranches = 2;

    /**
     * String representation of life-cycle callback methods for initialise,
     * sorted in call order, in a list.
     */
    private final ArrayList<String> initialiseMethodList;
    /**
     * String representation of life-cycle callback methods for initialise,
     * sorted in call order.
     */
    private String initialiseMethods;

    /**
     * String representation of life-cycle callback methods for end of batch,
     * sorted in call order.
     */
    private final ArrayList<String> batchEndMethodList;
    /**
     * String representation of life-cycle callback methods for end of batch,
     * sorted in call order.
     */
    private String batchEndMethods;

    /**
     * String representation of life-cycle callback methods for after event
     * processing, sorted in call order.
     */
    private final ArrayList<String> eventEndMethodList;

    /**
     * String representation of life-cycle callback methods for end of batch,
     * sorted in call order.
     */
    private String eventEndMethods;

    /**
     * String representation of life-cycle callback methods for batch pause,
     * sorted in call order.
     */
    private final ArrayList<String> batchPauseMethodList;

    /**
     * String representation of life-cycle callback methods for batch pause,
     * sorted in call order.
     */
    private String batchPauseMethods;

    /**
     * String representation of life-cycle callback methods for tearDown, sorted
     * in call order.
     */
    private final ArrayList<String> tearDownMethodList;

    /**
     * String representation of life-cycle callback methods for tearDown, sorted
     * in call order.
     */
    private String tearDownMethods;

    /**
     * String representation of node declarations as a list.
     */
    private final ArrayList<String> nodeDeclarationList;

    private final ArrayList<String> importList;

    /**
     * String representation of node declarations.
     */
    private String nodeDeclarations;

    /**
     * String representation of dirty flag.
     */
    private String dirtyFlagDeclarations;

    /**
     * String representation of all dirty flag reset to false.
     */
    private String resetDirtyFlags;
    /**
     * String representation of filter constants declarations.
     */
    private String filterConstantDeclarations;

    /**
     * String representation of the initial member assignments for each node
     */
    private final ArrayList<String> nodeMemberAssignmentList;

    /**
     * String representation of the initial member assignments for each node
     */
    private String nodeMemberAssignments;

    /**
     * String representation of top level event dispatch, branches on event type
     *
     */
    private String eventDispatch;

    /**
     * String representing the delegated event handling methods for a specific
     * event type, will include branching based on filterId.
     */
    private String eventHandlers;

    /**
     * A list of invokers for a filtered call tree. This list is used to create
     * a dispatch map the SEP will use to in place of switch dispatch.
     *
     * When switch cases in crease the performance degrades massively:
     *
     * 0-20 java switch optimised very fast. 20-n performance drops off by 30%
     * when n is too large the the number of bytes in the dispatch method grows
     * causing the vm to behave as follows: bytes > approx 325 bytes the
     * dispatch method is not inlined 30% degradation bytes > approx 8k bytes
     * the dispatch method is not compiled 800% degradation, yes 8 times slower
     */
    private ArrayList<InvokerFilterTarget> filteredInvokerList;
    private ArrayList<InvokerFilterTarget> filteredInvokerListDebug;

    /**
     * A string representing the delegated event handling methods
     */
    private String debugEventHandlers;

    /**
     * determines whether separate delegate eventHandling methods are generated.
     */
    private final boolean isInlineEventHandling;

    /**
     * String representation of event dispatch
     */
    private String debugEventDispatch;

    /**
     * String representing event audit dispatch
     */
    private String eventAuditDispatch;
    
    /**
     * String representation of template method for test event dispatch
     */
    private String testDispatch;

    /**
     * String representation of public nodes as a list.
     */
    private final ArrayList<String> publicNodeIdentifierList;

    private final SimpleEventProcessorModel model;
    
    /**
     * use reflection to assign private members
     */
    private boolean assignPrivateMembers;
    
    /**
     * are any auditors registered for this SEP
     */
    private boolean auditingEvent;
    private boolean auditingInvocations;
    private String auditMethodString;

    public SepJavaSourceModelHugeFilter(SimpleEventProcessorModel model) {
        this(model, false);
    }

    public SepJavaSourceModelHugeFilter(SimpleEventProcessorModel model, boolean inlineEventHandling) {
        this(model, inlineEventHandling, false);
    }

    public SepJavaSourceModelHugeFilter(SimpleEventProcessorModel model, boolean inlineEventHandling, boolean assignPrivateMembers) {
        this(model, inlineEventHandling, assignPrivateMembers, 2);
    }
    
    public SepJavaSourceModelHugeFilter(SimpleEventProcessorModel model, boolean inlineEventHandling, boolean assignPrivateMembers, int maxFilterBranches) {
        this.model = model;
        this.eventHandlers = "";
        this.debugEventHandlers = "";
        this.isInlineEventHandling = inlineEventHandling;
        this.assignPrivateMembers = assignPrivateMembers;
        this.maxFilterBranches = maxFilterBranches;
        initialiseMethodList = new ArrayList<>();
        batchEndMethodList = new ArrayList<>();
        batchPauseMethodList = new ArrayList<>();
        eventEndMethodList = new ArrayList<>();
        tearDownMethodList = new ArrayList<>();
        nodeDeclarationList = new ArrayList<>();
        nodeMemberAssignmentList = new ArrayList<>();
        publicNodeIdentifierList = new ArrayList<>();
        filteredInvokerList = new ArrayList<>();
        filteredInvokerListDebug = new ArrayList<>();
        importList = new ArrayList<>();
    }

    public void buildSourceModel() throws Exception {
        buildMethodSource(model.getInitialiseMethods(), initialiseMethodList);
        buildMethodSource(model.getBatchPauseMethods(), batchPauseMethodList);
        buildMethodSource(model.getEventEndMethods(), eventEndMethodList);
        buildMethodSource(model.getBatchEndMethods(), batchEndMethodList);
        buildMethodSource(model.getTearDownMethods(), tearDownMethodList);
        addDefacultImports();
        buildNodeDeclarations();
        buildDirtyFlags();
        buildFilterConstantDeclarations();
        buildMemberAssignments();
        buildNodeRegistrationListeners();
        buildEventDispatch();
        buildTestDispatch();

        initialiseMethods = "";
        boolean firstLine = true;
        for (String initialiseMethod : initialiseMethodList) {
            initialiseMethods += (firstLine ? "" : "\n") + initialiseMethod;
            firstLine = false;
        }

        batchPauseMethods = "";
        firstLine = true;
        for (String initialiseMethod : batchPauseMethodList) {
            batchPauseMethods += (firstLine ? "" : "\n") + initialiseMethod;
            firstLine = false;
        }

        batchEndMethods = "";
        firstLine = true;
        for (String initialiseMethod : batchEndMethodList) {
            batchEndMethods += (firstLine ? "" : "\n") + initialiseMethod;
            firstLine = false;
        }

        eventEndMethods = "";
        firstLine = true;
        for (String initialiseMethod : eventEndMethodList) {
            eventEndMethods += (firstLine ? "" : "\n") + initialiseMethod;
            firstLine = false;
        }

        tearDownMethods = "";
        for (String initialiseMethod : tearDownMethodList) {
            tearDownMethods += initialiseMethod + "\n";
        }
        tearDownMethods = StringUtils.chomp(tearDownMethods);

        nodeMemberAssignments = "";
        final StringBuilder memberAssignments = new StringBuilder(200 * nodeMemberAssignmentList.size());
        firstLine = true;
        for (String initialiseMethod : nodeMemberAssignmentList) {
            memberAssignments.append(firstLine ? "    " : "\n    ").append(initialiseMethod);
//            nodeMemberAssignments += (firstLine ? "    " : "\n    ") + initialiseMethod;
            firstLine = false;
        }
        nodeMemberAssignments = memberAssignments.toString();
    }

    private void buildMethodSource(List<CbMethodHandle> methodList, List methodSourceList) {
        for (CbMethodHandle method : methodList) {
            final String methodString = String.format("%8s%s.%s();", "", method.variableName, method.method.getName());
            methodSourceList.add(methodString);
        }
    }

    private void buildDirtyFlags() {
        dirtyFlagDeclarations = "";
        resetDirtyFlags = "";
        final ArrayList<DirtyFlag> values = new ArrayList(model.getDirtyFieldMap().values());
        NaturalOrderComparator comparator = new NaturalOrderComparator();
        Collections.sort(values, (o1, o2) -> comparator.compare(o1.name, o2.name));
        for (DirtyFlag flag : values) {
            dirtyFlagDeclarations += String.format("%4sprivate boolean %s = false;%n", "", flag.name);
            resetDirtyFlags += String.format("%8s%s = false;%n", "", flag.name);
        }
        for (DirtyFlag flag : values) {
            if(flag.requiresInvert){
                dirtyFlagDeclarations += String.format("%4sprivate boolean not%s = false;%n", "", flag.name);
                resetDirtyFlags += String.format("%8snot%s = false;%n", "", flag.name);
            }
        }
        dirtyFlagDeclarations = StringUtils.chomp(dirtyFlagDeclarations);
        resetDirtyFlags = StringUtils.chomp(resetDirtyFlags);
    }

    private final StringBuilder nodeDecBuilder = new StringBuilder(5 * 1000 * 1000);
    private HashMap<String, String> importMap = new HashMap<>();

    private String getClassName(String clazzName) {
        clazzName = model.getMappedClass(clazzName);
        String[] split = clazzName.split("\\.");
        String ret = clazzName;
        if (split.length > 1) {
            String simpleName = split[split.length - 1];
            String fqnName = clazzName;
            String pkgName = fqnName.replace("." + simpleName, "");
            ret = simpleName;
            if(fqnName.startsWith("java.lang") 
                    || GenerationContext.SINGLETON.getPackageName().equals(pkgName)){
                //ignore java.lang
            }else if (importMap.containsKey(simpleName)) {
                if (!importMap.get(simpleName).equalsIgnoreCase(fqnName)) {
                    ret = fqnName;
                }
            } else {
                importMap.put(simpleName, fqnName);
                importList.add(fqnName);
            }
        }
        return ret;
    }

    private void buildNodeDeclarations() {
        //getClassName(Arrays.class.getName());
        nodeDeclarations = "";
        final StringBuilder declarationBuilder = new StringBuilder(2000);
        final StringBuilder fqnBuilder = new StringBuilder(500);
        boolean firstLine = true;
        if (assignPrivateMembers) {
            declarationBuilder.append(s4).append("final net.vidageek.mirror.dsl.Mirror constructor = new net.vidageek.mirror.dsl.Mirror();\n");
        }
        for (Field field : model.getTopologigcallySortedNodeFields()) {
            final String access = field.publicAccess ? "public" : "private";

            fqnBuilder.append(getClassName(field.fqn));
            boolean syntheticConstructor = true;
            try {
                field.instance.getClass().getConstructor();
                syntheticConstructor = false;
            } catch (Exception e) {
                
            }
            if (assignPrivateMembers && syntheticConstructor) {
                //new constructor.on(clazz).invoke().constructor().bypasser();
                declarationBuilder.append(s4).append(access).append(" final ").append(fqnBuilder).append(" ").append(field.name)
                        .append(" = constructor.on(").append(fqnBuilder).append(".class).invoke().constructor().bypasser();");
            } else {
                String args = "";
                List<Field.MappedField> constructorArgs = model.constructorArgs(field.instance);
                args = constructorArgs.stream().map(f -> f.value()).collect(Collectors.joining(", "));
//                args = constructorArgs.stream().map(f -> f.value()).collect(Collectors.joining(", "));
                declarationBuilder.append(s4).append(access).append(" final ").append(fqnBuilder).append(" ").append(field.name)
                        .append(" = new ").append(fqnBuilder).append("(" + args + ");");
            }

            final String declaration = declarationBuilder.toString();
            nodeDeclarationList.add(declaration);
            nodeDecBuilder.append(firstLine ? "" : "\n").append(declaration);
            firstLine = false;
            if (field.publicAccess) {
                publicNodeIdentifierList.add(field.name);
            }

            fqnBuilder.delete(0, fqnBuilder.length());
            declarationBuilder.delete(0, declarationBuilder.length());
        }

        nodeDeclarations = nodeDecBuilder.toString();
        nodeDecBuilder.delete(0, nodeDecBuilder.length());

    }

    private void buildFilterConstantDeclarations() {
        filterConstantDeclarations = "";
        boolean firstLine = true;
        ArrayList<FilterDescription> tmp = new ArrayList(model.getFilterDescriptionList());
        tmp.sort((FilterDescription o1, FilterDescription o2) -> {
            return o1.value - o2.value;
        });
        HashMap<String, Integer> filterVariableMap = new HashMap<>();
        for (FilterDescription filterDescription : tmp) {
            if (filterDescription.variableName != null) {
                int value = filterDescription.value;
                String variableName = filterDescription.variableName;
                if (filterVariableMap.containsKey(variableName)) {
                    int mappedValue = filterVariableMap.get(variableName);
                    if (mappedValue != value) {
                        throw new IllegalStateException("two mappings for the same filter"
                                + " constant '" + variableName + "'");
                    } else {
                        continue;
                    }
                }
                filterVariableMap.put(variableName, value);
                final String declaration = String.format("    "
                        + "public static final int %s = %d;",
                        variableName, value);
//            nodeDeclarationList.add(declaration);
                filterConstantDeclarations += (firstLine ? "" : "\n") + declaration;
                firstLine = false;
            }
        }
    }

    private void buildEventDispatch() {
        generateClassBasedDipsatcher(false);
        generateClassBasedDipsatcher(true);
        //eventHandlers += tmpEventHandlersAsMethods;
        //filter for debug and non-debug
        if (!isInlineEventHandling) {
            List<Class<?>> importClassList = new ArrayList<>();
            eventHandlers += JavaGenHelper.generateMapDisaptch(filteredInvokerList, importClassList);
            debugEventHandlers += JavaGenHelper.generateMapDisaptch(filteredInvokerListDebug, importClassList);
            if(importClassList.size()>0){
                //bit of a hack, need to remove
                getClassName("it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap");
            }
            importClassList.stream().map(Class::getCanonicalName).forEach(this::getClassName);
        }
        
        if(auditingEvent){
            eventHandlers += auditMethodString;
        }
    }

    /**
     * generates the implementation of the onEvent method, and writes to the
     * eventDispatch or debugEventDispatch string. This is the top level method
     * called, and dispatches methods internally. The generated method, switches
     * on class type, casts to the correct object and then invokes specific
     * event handling for that type.
     *
     * @param isDebug
     */
    private void generateClassBasedDipsatcher(boolean isDebug) {
        String dispatchString = "        switch (event.eventId()) {\n";
        String dispatchStringNoId = "        switch (event.getClass().getName()) {\n";
        boolean idDispatch = false;
        boolean noIdDispatch = false;

        Map<Class, Map<FilterDescription, List<CbMethodHandle>>> dispatchMap = model.getDispatchMap();
        Map<Class, Map<FilterDescription, List<CbMethodHandle>>> postDispatchMap = model.getPostDispatchMap();
        Set<Class> keySet = dispatchMap.keySet();
        HashSet<Class> classSet = new HashSet<>(keySet);
        classSet.addAll(postDispatchMap.keySet());
        ArrayList<Class> clazzList = new ArrayList<>(classSet);
        Collections.sort(clazzList, (Class o1, Class o2) -> o1.getName().compareTo(o2.getName()));

        for (Class eventId : clazzList) {
            String className = getClassName(eventId.getCanonicalName());
            if (hasIdField(eventId)) {
                dispatchString += String.format("%12scase (%s.ID):{%n", "", className);
                dispatchString += String.format("%16s%s typedEvent = (%s)event;%n", "", className, className);
                if (isDebug) {
                    dispatchString += String.format("%16sdebugger.eventInvocation(event);%n", "");
                }
                Map<FilterDescription, List<CbMethodHandle>> cbMap = dispatchMap.get(eventId);
                Map<FilterDescription, List<CbMethodHandle>> cbMapPostEvent = postDispatchMap.get(eventId);
                dispatchString += buildFiteredDispatch(cbMap, cbMapPostEvent, isDebug, eventId);
                dispatchString += String.format("%16sbreak;%n", "");
                dispatchString += String.format("%12s}%n", "");
                idDispatch = true;
            } else {
                dispatchStringNoId += String.format("%12scase (\"%s\"):{%n", "", eventId.getName());
                dispatchStringNoId += String.format("%16s%s typedEvent = (%s)event;%n", "", className, className);
                if (isDebug) {
                    dispatchStringNoId += String.format("%16sdebugger.eventInvocation(event);%n", "");
                }
                Map<FilterDescription, List<CbMethodHandle>> cbMap = dispatchMap.get(eventId);
                Map<FilterDescription, List<CbMethodHandle>> cbMapPostEvent = postDispatchMap.get(eventId);
                dispatchStringNoId += buildFiteredDispatch(cbMap, cbMapPostEvent, isDebug, eventId);
                dispatchStringNoId += String.format("%16sbreak;%n", "");
                dispatchStringNoId += String.format("%12s}%n", "");
                noIdDispatch = true;
            }
        }
        dispatchString += String.format("%8s}%n", "");
        dispatchStringNoId += String.format("%8s}%n", "");
        if (isInlineEventHandling) {
            dispatchString += String.format("%8safterEvent();%n", "");
            dispatchStringNoId += String.format("%8safterEvent();%n", "");
        }

        if (!idDispatch) {
            dispatchString = "";
        }
        if (!noIdDispatch) {
            dispatchStringNoId = "";
        }

        dispatchString += dispatchStringNoId;
        //build a noIddispatchString - just copy method above and only
        //process <Event>.ID free events.
        if (isDebug) {
            debugEventDispatch = "//DEBUG\n" + dispatchString;
        } else {
            eventDispatch = dispatchString + "\n";
        }
    }

    private static boolean hasIdField(Class e) {
        Set<java.lang.reflect.Field> allFields = ReflectionUtils.getFields(e, Predicates.and(
                withName("ID"),
                withType(int.class),
                withModifier(Modifier.PUBLIC),
                withModifier(Modifier.STATIC),
                withModifier(Modifier.FINAL)
        ));
        return allFields.size() > 0;
    }

    private static final String s4 = "    ";
    private static final String s8 = "        ";
    private static final String s12 = "            ";
    private static final String s16 = "                ";
    private static final String s20 = "                    ";
    private static final String s24 = "                        ";
    private final StringBuilder ct = new StringBuilder(5 * 1000 * 1000);
    private final StringBuilder switchF = new StringBuilder(5 * 1000 * 1000);

    private String buildFilteredSwitch(Map<FilterDescription, List<CbMethodHandle>> cbMap,
            Map<FilterDescription, List<CbMethodHandle>> cbMapPostEvent, Class eventClass,
            boolean isDebug, boolean intFilter, boolean noFilter) {
        Set<FilterDescription> filterIdSet = cbMap.keySet();
        //generate all the booleans for each CbMethodHandle in the cbMap and store in a map
        //&& caseCount > maxCaseCount
        //TODO produce combined list of cbMap and cbMapPostEvent
        ArrayList<FilterDescription> clazzList = new ArrayList<>(filterIdSet);
        int caseCount = Math.max(cbMap.size(), cbMapPostEvent.size());
        Collections.sort(clazzList, (FilterDescription o1, FilterDescription o2) -> {
            int ret = o1.value - o2.value;
            if (!o1.isIntFilter && !o2.isIntFilter) {
                ret = o1.stringValue.compareTo(o2.stringValue);
            } else if (o1.isIntFilter && !o2.isIntFilter) {
                ret = 1;
            } else if (!o1.isIntFilter && o2.isIntFilter) {
                ret = -1;
            }
            return ret;
        });

        String switchFilter = "";
        switchF.delete(0, switchF.length());
        for (FilterDescription filterDescription : clazzList) {

            //INVOKETARGET
            InvokerFilterTarget invokerTarget = new InvokerFilterTarget();
            invokerTarget.methodName = JavaGenHelper.generateFilteredDispatchMethodName(filterDescription);
            invokerTarget.filterDescription = filterDescription;
            invokerTarget.stringMapName = JavaGenHelper.generateFilteredDispatchMap(filterDescription);
            invokerTarget.intMapName = JavaGenHelper.generateFilteredDispatchMap(filterDescription);

            if (intFilter == filterDescription.isIntFilter || (noFilter)) {
                boolean isDefaultFilter = filterDescription == FilterDescription.DEFAULT_FILTER;
                boolean isNoFilter = filterDescription == FilterDescription.NO_FILTER || filterDescription == FilterDescription.INVERSE_FILTER;
                String filterValue = intFilter ? filterDescription.value + "" : "\"" + filterDescription.stringValue + "\"";
                String filterVariable = filterDescription.variableName;
                List<CbMethodHandle> cbList = cbMap.get(filterDescription);
                if (noFilter && isDefaultFilter) {
                    //progress without header
//                    if(filterDescription.eventClass!=null)
                    invokerTarget.filterDescription = new FilterDescription(eventClass);
                    invokerTarget.methodName = JavaGenHelper.generateFilteredDispatchMethodName(invokerTarget.filterDescription);
                    invokerTarget.stringMapName = JavaGenHelper.generateFilteredDispatchMap(invokerTarget.filterDescription);
                    invokerTarget.intMapName = JavaGenHelper.generateFilteredDispatchMap(invokerTarget.filterDescription);
//                            + filterDescription.eventClass.getSimpleName() + "_"
//                            + "(" + filterDescription.eventClass.getCanonicalName() + " typedEvent ){\n";
                } else if (noFilter || isNoFilter || isDefaultFilter) {
                    //ignore the NO_FILTER
                    continue;
                } else {
                    //build switch header
                    if (filterDescription.comment != null) {
                        //Filter comment
//                        switchFilter += String.format("%-20s%s%n", "//", filterDescription.comment);
                        switchF.append(s20).append("//").append(filterDescription.comment).append("\n");
                    }
                    if (filterVariable == null) {
//                        switchFilter += String.format("%20scase (%s):%n", "", filterValue);
                        switchF.append(s20).append("case(").append(filterValue).append("):\n");

                    } else {
//                        switchFilter += String.format("%20scase (%s):%n", "", filterVariable);
                        switchF.append(s20).append("case(").append(filterVariable).append("):\n");
                    }
                }
                //TODO check for null here on cblist
                cbList = cbList == null ? Collections.EMPTY_LIST : cbList;
                //String callTree = "";

                ct.delete(0, ct.length());
                for (CbMethodHandle method : cbList) {
//METHOD BREAK UP HERE                    ct.append("//ct callback - forward").append('\n');
                    //does cb support dirty
                    DirtyFlag dirtyFlagForUpdateCb = model.getDirtyFlagForUpdateCb(method);
                    String dirtyAssignment = "";
                    if (dirtyFlagForUpdateCb != null) {
                        if(dirtyFlagForUpdateCb.alwaysDirty){
                            dirtyAssignment =  dirtyFlagForUpdateCb.name + " = true;\n" + s24;
                        }else{
                            dirtyAssignment = dirtyFlagForUpdateCb.name + " = ";
                        }
                    }
                    //protect with guards
                    Collection<DirtyFlag> nodeGuardConditions = model.getNodeGuardConditions(method);
                    String OR = "";
                    if (nodeGuardConditions.size() > 0) {
                        //callTree += String.format("%24sif(", "");
                        OnEvent onEvent = method.method.getAnnotation(OnEvent.class);
                        String invert = "";
                        if(onEvent!=null && !onEvent.dirty()){
                            invert = " not";
                        }
                        ct.append(s24).append("if(");
                        for (DirtyFlag nodeGuardCondition : nodeGuardConditions) {
                            //callTree += OR + nodeGuardCondition.name;
                            ct.append(OR).append(invert).append(nodeGuardCondition.name);
//                            OR = " || ";
                            OR = " | ";
                        }
                        //callTree += ") {\n";
                        ct.append(") {\n");
                    }

                    //add audit
                    if(auditingInvocations){
                        ct.append(s24).append("auditInvocation(")
                                .append(method.variableName)
                                .append(", \"").append(method.variableName).append("\"")
                                .append(", \"").append(method.method.getName()).append("\"")
                                .append(", typedEvent")
                                .append(");\n");
                    }
                    //assign return if appropriate
                    if (isDebug) {
                        //callTree += String.format("%24sdebugger.nodeInvocation(%s, \"%s\");%n", "", method.variableName, method.variableName);
                        ct.append(s24).append("debugger.nodeInvocation(").append(method.variableName).append(", \"").append(method.variableName).append("\");\n");
                    }
                    if (method.parameterClass == null) {
                        //callTree += String.format("%24s%s%s.%s();\n", "", dirtyAssignment, method.variableName, method.method.getName());
                        ct.append(s24).append(dirtyAssignment).append(method.variableName).append(".").append(method.method.getName()).append("();\n");
                    } else {
                        //callTree += String.format("%24s%s%s.%s(typedEvent);%n", "", dirtyAssignment, method.variableName, method.method.getName());
                        ct.append(s24).append(dirtyAssignment).append(method.variableName).append(".").append(method.method.getName()).append("(typedEvent);\n");
                    }
                    if (dirtyFlagForUpdateCb != null && dirtyFlagForUpdateCb.requiresInvert) {
                        ct.append(s24).append("not" + dirtyFlagForUpdateCb.name + " = !" + dirtyFlagForUpdateCb.name + ";\n");
                    }
                    //child callbacks - listening to an individual parent change
                    //if guards are in operation for the parent node, conditionally invoke only on a change
                    final Map<Object, List<CbMethodHandle>> listenerMethodMap = model.getParentUpdateListenerMethodMap();
                    Object parent = method.instance;
                    String parentVar = method.variableName;

                    //guard
                    DirtyFlag parentFlag = model.getDirtyFieldMap().get(model.getFieldForInstance(parent));
                    //the parent listener map should be keyed on instance and event filter
                    //we carry out filtering here so that no propagate annotations on parents
                    //do not generate the parent callback
                    List<CbMethodHandle> updateListenerCbList = listenerMethodMap.get(parent);
                    final EventHandler handlerAnnotation = method.method.getAnnotation(EventHandler.class);
                    if (handlerAnnotation != null && (!handlerAnnotation.propagate() )) {
//                        String format = String.format("should clear!!: event[%s] updateList[%s]\n",eventClass, updateListenerCbList);
//                        ct.append("//" + format);
                    } else {
                        if (parentFlag != null && updateListenerCbList.size() > 0) {
                            //callTree += String.format("%20sif(%s) {\n", "", parentFlag.name);
                            ct.append(s20 + "if(").append(parentFlag.name).append(") {\n");
                        }
                        //child callbacks
                        boolean unguarded = false;
                        StringBuilder sbUnguarded = new StringBuilder();
                        for (CbMethodHandle cbMethod : updateListenerCbList) {
                            //callTree += String.format("%24s%s.%s(%s);%n", "", cbMethod.variableName, cbMethod.method.getName(), parentVar);
                            if(!cbMethod.method.getAnnotation(OnParentUpdate.class).guarded()){
                                unguarded = true;
                                sbUnguarded.append(s20).append(cbMethod.variableName).append(".").append(cbMethod.method.getName()).append("(").append(parentVar).append(");\n");
                            }else{
                                ct.append(s24).append(cbMethod.variableName).append(".").append(cbMethod.method.getName()).append("(").append(parentVar).append(");\n");
                            }
                        }
                        if (parentFlag != null && updateListenerCbList.size() > 0) {
                            //callTree += String.format("%20s}\n", "", parentFlag.name);
                            ct.append(s20).append("}\n");
                            if(unguarded){
                                ct.append(sbUnguarded);
                            }
                        }
                        //close guards clause
                        if (nodeGuardConditions.size() > 0) {
                            //callTree += String.format("%16s}\n", "");
                            ct.append(s16 + "}\n");
                        }
                    }
                }
                //chec for null on cbList and escape
                cbList = cbMapPostEvent.get(filterDescription);
                if (cbList == null || cbList.size() > 0) {
                    //callTree += String.format("%16s//event stack unwind callbacks\n", "");
                    ct.append(s16 + "//event stack unwind callbacks\n");
                }
                cbList = cbList == null ? Collections.EMPTY_LIST : cbList;
                for (CbMethodHandle method : cbList) {
//METHOD BREAK UP HERE                    ct.append("//ct callback - unwind").append('\n');
                    //protect with guards
                    Collection<DirtyFlag> nodeGuardConditions = model.getNodeGuardConditions(method);
                    String OR = "";
                    if (nodeGuardConditions.size() > 0) {
                        //callTree += String.format("%24sif(", "");
                        ct.append(s24 + "if(");
                        for (DirtyFlag nodeGuardCondition : nodeGuardConditions) {
                            //callTree += OR + nodeGuardCondition.name;
                            ct.append(OR).append(nodeGuardCondition.name);
//                            OR = " || ";
                            OR = " | ";
                        }
                        //callTree += ") {\n";
                        ct.append(") {\n");
                    }

                    //assign return if appropriate
                    if (isDebug) {
                        //callTree += String.format("%24sdebugger.nodeInvocation(%s, \"%s\");%n", "", method.variableName, method.variableName);
                        ct.append(s24 + "debugger.nodeInvocation(").append(method.variableName).append(", \"").append(method.variableName).append("\");");
                    }
                    if (method.parameterClass == null) {
                        //callTree += String.format("%24s%s%s.%s();\n", "", "", method.variableName, method.method.getName());
                        ct.append(s24).append(method.variableName).append(".").append(method.method.getName()).append("();\n");
                    } else {
                        //callTree += String.format("%24s%s%s.%s(typedEvent);%n", "", "", method.variableName, method.method.getName());
                        ct.append(s24).append(method.variableName).append(".").append(method.method.getName()).append("(typedEvent);\n");
                    }
                    //close guarded clause
                    if (nodeGuardConditions.size() > 0) {
                        //callTree += String.format("%16s}\n", "");
                        ct.append(s16 + "}\n");
                    }
                }
                //INVOKETARGET
//                invokerTarget.methodBody = //callTree;
                invokerTarget.methodBody = ct.toString();
                if (isDebug && caseCount > maxFilterBranches) {
                    filteredInvokerListDebug.add(invokerTarget);
                } else if (caseCount > maxFilterBranches) {
                    filteredInvokerList.add(invokerTarget);
                }

                if (!noFilter) {
                    //callTree += String.format("%24sbreak;%n", "");
//                    ct.append(s24 + "break;\n");
                    ct.append(s24 + "afterEvent();\n");
                    ct.append(s24 + "return;\n");
                }
//                switchFilter += //callTree;
//                switchFilter += ct.toString();
                switchF.append(ct);
            }
        }
        //switchFilter += "        //dispatchIntMapTestEvent.get(typedEvent.filterId()).invoke(typedEvent);\n";
//        return switchFilter.length() == 0 ? null : switchFilter;
        return switchF.length() == 0 ? null : switchF.toString();
    }

    private String buildFiteredDispatch(Map<FilterDescription, List<CbMethodHandle>> cbMap,
            Map<FilterDescription, List<CbMethodHandle>> cbMapPostEvent,
            boolean isDebug, Class eventClass) {
        String dispatchString = "";
        String eventHandlerString = "";
        String intFilterSwitch = buildFilteredSwitch(cbMap, cbMapPostEvent, eventClass, isDebug, true, false);
        String stringFilterSwitch = buildFilteredSwitch(cbMap, cbMapPostEvent, eventClass, isDebug, false, false);
        String noFilterDispatch = buildFilteredSwitch(cbMap, cbMapPostEvent, eventClass, isDebug, false, true);
        int caseCount = cbMap.size();
        if (!isInlineEventHandling && caseCount > maxFilterBranches) {
            //Handle with a dispatch map
            dispatchString += String.format("%16shandleEvent(typedEvent);%n", "");
            eventHandlerString += String.format("%n%4spublic void handleEvent(%s typedEvent) {%n", "", getClassName(eventClass.getCanonicalName()));
            eventHandlerString += eventAuditDispatch;
            //intFiltered
            String filterDec = "FilteredHandlerInvoker ";
            if (intFilterSwitch != null) {
                //INVOKER
                String mapName = JavaGenHelper.generateFilteredDispatchMap(eventClass, true);
                eventHandlerString += String.format("%8sFilteredHandlerInvoker invoker = %s.get(typedEvent.filterId());%n"
                        + "        if(invoker!=null){%n"
                        + "             invoker.invoke(typedEvent);%n"
                        + "             afterEvent();%n"
                        + "             return;%n"
                        + "        }%n", "", mapName);
                filterDec = "";
            }
            //String filtered
            if (stringFilterSwitch != null) {
                //INVOKER
                String mapName = JavaGenHelper.generateFilteredDispatchMap(eventClass, false);
                eventHandlerString += String.format("%8s%sinvoker = %s.get(typedEvent.filterString());%n"
                        + "        if(invoker!=null){%n"
                        + "             invoker.invoke(typedEvent);%n"
                        + "             afterEvent();%n"
                        + "             return;%n"
                        + "        }%n", "", filterDec, mapName);
            }
            if (noFilterDispatch != null) {
                eventHandlerString += "        //Default, no filter methods\n";
                eventHandlerString += noFilterDispatch;
            }
            eventHandlerString += "        afterEvent();\n";
            eventHandlerString += "    }\n\n";

        } else if (!isInlineEventHandling) {
            //Handle with case statements
            dispatchString += String.format("%16shandleEvent(typedEvent);%n", "");
            eventHandlerString += String.format("%n%4spublic void handleEvent(%s typedEvent) {%n", "", getClassName(eventClass.getCanonicalName()));
            eventHandlerString += eventAuditDispatch;
            //intFiltered
            if (intFilterSwitch != null) {
                eventHandlerString += String.format("%8sswitch (typedEvent.filterId()) {%n", "");
                eventHandlerString += intFilterSwitch;
                eventHandlerString += String.format("%8s}%n", "");
            }
            //String filtered
            if (stringFilterSwitch != null) {
                eventHandlerString += String.format("%8sswitch (typedEvent.filterString()) {%n", "");
                eventHandlerString += stringFilterSwitch;
                eventHandlerString += String.format("%8s}%n", "");
            }
            //TODO if both are null then 
            if (noFilterDispatch != null) {
                eventHandlerString += "        //Default, no filter methods\n";
                eventHandlerString += noFilterDispatch;
            }
            eventHandlerString += String.format("%8safterEvent();%n", "");
            eventHandlerString += String.format("%4s}%n", "");
        } else {
            dispatchString += eventAuditDispatch;
            //int filtered
            if (intFilterSwitch != null) {
                dispatchString += String.format("%16sswitch (typedEvent.filterId()) {%n", "");
                dispatchString += intFilterSwitch;
                dispatchString += String.format("%16s}%n", "");
            }
            //String filtered
            if (stringFilterSwitch != null) {
                dispatchString += String.format("%16sswitch (typedEvent.filterString()) {%n", "");
                dispatchString += stringFilterSwitch;
                dispatchString += String.format("%16s}%n", "");
            }
            //no filtered event dispatch
            if (noFilterDispatch != null) {
                dispatchString += noFilterDispatch;
            }
        }
        if (isDebug) {
            debugEventHandlers += eventHandlerString;
        } else {
            eventHandlers += eventHandlerString;
        }
        return dispatchString;
    }

    private void buildTestDispatch() {
        testDispatch = "";
        testDispatch += "    public void testNode(AssertTestEvent assertTestEvent) {\n";
        testDispatch += "        switch (assertTestEvent.nodeName) {\n";
        for (Field field : model.getNodeFields()) {
            if (field.publicAccess) {
                testDispatch += String.format("%12scase (\"%s\"): {%n", "", field.name);
                testDispatch += String.format("%16s%s node = sep.%s;%n", "", field.fqn, field.name);
                testDispatch += String.format("%16sbreak;%n", "");
                testDispatch += String.format("%12s}%n", "");
            }
        }
        testDispatch += String.format("%8s}%n", "");
        testDispatch += String.format("%4s}%n", "");
    }

    private void buildMemberAssignments() throws Exception {
        final List<Field> nodeFields = model.getNodeFields();
        if (assignPrivateMembers) {
            nodeMemberAssignmentList.add("    final net.vidageek.mirror.dsl.Mirror assigner = new net.vidageek.mirror.dsl.Mirror();");
        }
        for (Field field : nodeFields) {
//            boolean useRefelction = false;
            Object object = field.instance;
            String varName = field.name;
            
            model.beanProperties(object).stream().forEach(s -> nodeMemberAssignmentList.add(varName + "." + s + ";"));
            
//            java.lang.reflect.Field[] fields = object.getClass().getDeclaredFields();
            java.lang.reflect.Field[] fields = object.getClass().getFields();

            MirrorList<java.lang.reflect.Field> fields1 = new Mirror().on(object.getClass()).reflectAll().fields();
            fields = fields1.toArray(fields);

            for (java.lang.reflect.Field instanceField : fields) {
                boolean useRefelction = false;
                if ((instanceField.getModifiers() & (Modifier.STATIC | Modifier.TRANSIENT)) != 0) {
                    continue;
                }
                if (!assignPrivateMembers && (instanceField.getModifiers() & (Modifier.PRIVATE | Modifier.PROTECTED | Modifier.FINAL)) != 0) {
                    continue;
                }
                if (!assignPrivateMembers && (instanceField.getModifiers() == 0)) {
                    continue;
                }
                if (assignPrivateMembers && !Modifier.isPublic(instanceField.getModifiers())) {
                    useRefelction = true;
                }
                instanceField.setAccessible(true);
                if (instanceField.getType().isArray()) {
                    //array fields
                    Object array = instanceField.get(object);
                    if (array == null) {
                        continue;
                    }
                    int length = Array.getLength(array);
                    final String className = getClassName(instanceField.getType().getComponentType().getCanonicalName());
                    if (useRefelction) {
                        nodeMemberAssignmentList.add(String.format("%4s%s[] %s_%s = new %s[%d];", "", className, varName, instanceField.getName(), className, length));
                        nodeMemberAssignmentList.add(String.format("%4sassigner.on(%s).set().field(\"%s\").withValue(%s_%s);", "", varName, instanceField.getName(), varName, instanceField.getName()));
                    } else {
                        nodeMemberAssignmentList.add(String.format("%4s%s.%s = new %s[%d];", "", varName, instanceField.getName(), className, length));
                    }
                    if (instanceField.getType().getComponentType().isPrimitive()) {
                        //primitive array
                        for (int i = 0; i < length; i++) {
                            Object value = Array.get(array, i).toString();
                            String joiner = useRefelction ? "_" : ".";
                            nodeMemberAssignmentList.add(String.format("%4s%s%s%s[%d] = %s;", "", varName, joiner, instanceField.getName(), i, value));
                        }
                    } else if (instanceField.getType().getComponentType().equals(String.class)) {
                        //String array
                        for (int i = 0; i < length; i++) {
                            Object value = StringEscapeUtils.escapeJava(Array.get(array, i).toString());
                            String joiner = useRefelction ? "_" : ".";
                            nodeMemberAssignmentList.add(String.format("%4s%s%s%s[%d] = \"%s\";", "", varName, joiner, instanceField.getName(), i, value));
                        }
                    } else {
                        //object array
                        for (int i = 0; i < length; i++) {
                            Object refField = Array.get(array, i);
                            Field nodeReference = model.getFieldForInstance(refField);
                            String joiner = useRefelction ? "_" : ".";
                            if (nodeReference != null) {
                                nodeMemberAssignmentList.add(String.format("%4s%s%s%s[%d] = %s;", "", varName, joiner, instanceField.getName(), i, nodeReference.name));
                            }
                        }

                    }
                } else {
                    //scalar fields
                    if (instanceField.get(object) == null) {
                        continue;
                    }
                    Field nodeReference = model.getFieldForInstance(instanceField.get(object));
                    if (instanceField.getType().equals(Character.TYPE)) {
                        String value = instanceField.get(object).toString();
                        if (useRefelction) {
                            nodeMemberAssignmentList.add(String.format("%4sassigner.on(%s).set().field(\"%s\").withValue('%s');", "", varName, instanceField.getName(), value));
                        } else {
                            nodeMemberAssignmentList.add(String.format("%4s%s.%s = '%s';", "", varName, instanceField.getName(), value));
                        }
                    } else if (instanceField.getType().isPrimitive() ) {
                        String value = instanceField.get(object).toString();
                        value = value.equalsIgnoreCase("NaN")?"Double.NaN":value;
                        value = "(" + instanceField.getType().toString() + ")" + value;
                        if (useRefelction) {
                            nodeMemberAssignmentList.add(String.format("%4sassigner.on(%s).set().field(\"%s\").withValue(%s);", "", varName, instanceField.getName(), value));
                        } else {
                            nodeMemberAssignmentList.add(String.format("%4s%s.%s = %s;", "", varName, instanceField.getName(), value));
                        }
                    } else if (instanceField.getType().isEnum()) {
                        String value = instanceField.get(object).toString();
                        String enumClass = instanceField.getType().getCanonicalName();
                        if (useRefelction) {
                            nodeMemberAssignmentList.add(String.format("%4sassigner.on(%s).set().field(\"%s\").withValue(%s.%s);", "", varName, instanceField.getName(), enumClass, value));
                        } else {
                            nodeMemberAssignmentList.add(String.format("%4s%s.%s = %s.%s;", "", varName, instanceField.getName(), enumClass, value));
                        }
                    } else if (nodeReference != null) {
                        if (useRefelction) {
                            nodeMemberAssignmentList.add(String.format("%4sassigner.on(%s).set().field(\"%s\").withValue(%s);", "", varName, instanceField.getName(), nodeReference.name));
                        } else {
                            nodeMemberAssignmentList.add(String.format("%4s%s.%s = %s;", "", varName, instanceField.getName(), nodeReference.name));
                        }
                    } else if (instanceField.getType().equals(String.class)) {

                        String value = StringEscapeUtils.escapeJava(instanceField.get(object).toString());
                        if (useRefelction) {
                            nodeMemberAssignmentList.add(String.format("%4sassigner.on(%s).set().field(\"%s\").withValue(\"%s\");", "", varName, instanceField.getName(), value));
                        } else {
                            nodeMemberAssignmentList.add(String.format("%4s%s.%s = \"%s\";", "", varName, instanceField.getName(), value));
                        }
                    } else if (Collection.class.isAssignableFrom(instanceField.getType())) {
                        Collection list = (Collection) instanceField.get(object);
                        if (list != null) {
                            String joiner = useRefelction ? "_" : ".";
                            if (useRefelction) {
                                final String className = getClassName(Collection.class.getCanonicalName());
                                nodeMemberAssignmentList.add(String.format("%4sCollection %s_%s = (Collection)assigner.on(%s).get().field(\"%s\");", "", varName, instanceField.getName(), varName, instanceField.getName()));
                            }

                            for (Object parent : list) {
                                if (instanceField.getGenericType() instanceof ParameterizedType) {
                                    ParameterizedType integerListType = (ParameterizedType) instanceField.getGenericType();
                                    Class<?> classType = Object.class;
                                    if(integerListType.getActualTypeArguments()[0] instanceof Class){
                                            classType = (Class<?>) integerListType.getActualTypeArguments()[0];
                                    }
                                    Field nodeParentReference = model.getFieldForInstance(parent);
                                    if (nodeParentReference != null) {
                                        nodeMemberAssignmentList.add(String.format("%4s%s%s%s.add(%s);", "", varName, joiner, instanceField.getName(), nodeParentReference.name));
                                    } else if (classType == String.class) {
                                        String value = StringEscapeUtils.escapeJava(parent.toString());
                                        nodeMemberAssignmentList.add(String.format("%4s%s%s%s.add(\"%s\");", "", varName, joiner, instanceField.getName(), value));
                                    } else if (classType == Character.class) {
                                        String value = parent.toString();
                                        nodeMemberAssignmentList.add(String.format("%4s%s%s%s.add('%s');", "", varName, joiner, instanceField.getName(), parent));
                                    } else if (Number.class.isAssignableFrom(classType)) {
                                        String value = parent.toString();
                                        String cast = mapWrapperToPrimitive(classType).getSimpleName();
                                        nodeMemberAssignmentList.add(String.format("%4s%s%s%s.add((%s)%s);", "", varName, joiner, instanceField.getName(), cast, value));
                                    } else if (classType.isEnum()) {
                                        String enumClass = getClassName(classType.getCanonicalName());
                                        String value = parent.toString();
                                        nodeMemberAssignmentList.add(String.format("%4s%s%s%s.add(%s.%s);", "", varName, joiner, instanceField.getName(), enumClass, value));
                                    }
                                } else {
                                    Field nodeParentReference = model.getFieldForInstance(parent);
                                    if (nodeParentReference != null) {
                                        nodeMemberAssignmentList.add(String.format("%4s%s%s%s.add(%s);", "", varName, joiner, instanceField.getName(), nodeParentReference.name));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public ArrayList<String> getInitialiseMethodList() {
        return initialiseMethodList;
    }

    public String getInitialiseMethods() {
        return initialiseMethods;
    }

    public ArrayList<String> getBatchEndMethodList() {
        return batchEndMethodList;
    }

    public ArrayList<String> getBatchPauseMethodList() {
        return batchPauseMethodList;
    }

    public ArrayList<String> getEventEndMethodList() {
        return eventEndMethodList;
    }

    public ArrayList<String> getTearDownMethodList() {
        return tearDownMethodList;
    }

    public ArrayList<String> getNodeDeclarationList() {
        return nodeDeclarationList;
    }

    public ArrayList<String> getNodeMemberAssignmentList() {
        return nodeMemberAssignmentList;
    }

    public ArrayList<String> getPublicNodeList() {
        return publicNodeIdentifierList;
    }

    public String getTestDispatch() {
        return testDispatch;
    }

    public String getNodeMemberAssignments() {
        return nodeMemberAssignments;
    }

    public String getFilterConstantDeclarations() {
        return filterConstantDeclarations;
    }

//    public ArrayList<String> getImportList() {
//        return importList;
//    }

    /**
     * String representation of top level event dispatch
     *
     * <code> public void onEvent(com.fluxtion.api.event.Event event) </code>
     * <p>
     * <code>
     * public void handleEvent([specific event] event) {
     *      [eventHandlers]
     * }
     * </code>
     *
     * @return top level event dispatch string
     */
    public String getEventDispatch() {
        return eventDispatch;
    }

    /**
     * String representation of java code handling subclass of
     * {@link com.fluxtion.api.event.Event Event}, with support for specific
     * dispatch based upon
     * {@linkplain  com.fluxtion.runtime.event.Event#filterId() filterID}. If
     * inlining is false the following output will be produced:
     * <p>
     * <code>
     * public void handleEvent([specific event] event) {
     *      [eventHandlers]
     * }
     * </code>
     *
     * @return type specific event dispatch code
     */
    public String getEventHandlers() {
        return eventHandlers;
    }

    public String getDebugEventHandlers() {
        return debugEventHandlers;
    }

    public String getDebugEventDispatch() {
        return debugEventDispatch;
    }

    public String getNodeDeclarations() {
        return nodeDeclarations;
    }

    public String getDirtyFlagDeclarations() {
        return dirtyFlagDeclarations;
    }

    public String getResetDirtyFlags() {
        return resetDirtyFlags;
    }

    public String getBatchEndMethods() {
        return batchEndMethods;
    }

    public String getEventEndMethods() {
        return eventEndMethods;
    }

    public String getBatchPauseMethods() {
        return batchPauseMethods;
    }

    public String getTearDownMethods() {
        return tearDownMethods;
    }

    public String getImports() {
        Collections.sort(importList);
        StringBuilder sb = new StringBuilder(2048);
        importList.stream().forEach(s ->{
            sb.append("import ")
                    .append(s)
                    .append(";\n");
        });
        return sb.toString();
    }

    @Override
    public String toString() {
        return "SepJavaSourceModel{" + "\ninitialiseMethods=" + initialiseMethodList
                + ", \nbatchEndMethods=" + batchEndMethodList
                + ", \nbatchPauseMethods=" + batchPauseMethodList
                + ", \neventEndMethods=" + eventEndMethodList
                + ", \ntearDownMethods=" + tearDownMethodList
                + ", \nnodeDeclarations=" + nodeDeclarationList
                + ", \neventDispatch=" + eventDispatch
                + ", \nnodeMemberAssignments=" + nodeMemberAssignmentList
                + ", \nmodel=" + model
                + "\n}";
    }

    private void buildNodeRegistrationListeners() {
        final List<Field> nodeFields = model.getNodeFields();
        this.auditingEvent = false;
        eventAuditDispatch = "";
        List<Field> listenerFields = model.getNodeRegistrationListenerFields();
        if (listenerFields == null || listenerFields.isEmpty()) {
            return;
        }
        this.auditingEvent = true;
        this.auditingInvocations = false;
        auditMethodString = "";
        String auditEvent = "private void auditEvent(Object typedEvent){\n";
        String auditInvocation = "private void auditInvocation(Object node, String nodeName, String methodName, Object typedEvent){\n";
        String initialiseAuditor = "private void initialiseAuditor(" + getClassName(Auditor.class.getName()) + " auditor){\n"
                + "\tauditor.init();\n";
            for (Field nodeField : nodeFields) {
                String nodeName = nodeField.name;
                if(listenerFields.stream().anyMatch((t) -> t.name.equals(nodeName))){
                    continue;
                }
                initialiseAuditor += (String.format("auditor.nodeRegistered(%s, \"%s\");", nodeName, nodeName));
            }
        eventAuditDispatch = "";
        nodeMemberAssignmentList.add("\t//node auditors");
        for (Field listenerField : listenerFields) {
            String listenerName = listenerField.name;
            nodeMemberAssignmentList.add("initialiseAuditor(" + listenerField.name + ");");
            //add init
            //initialiseMethodList.add(String.format("%8s%s.init();", "", listenerName));
            //add teardwon
            tearDownMethodList.add(0, String.format("%8s%s.tearDown();", "", listenerName));
            //add event complete
            eventEndMethodList.add(String.format("%8s%s.processingComplete();", "", listenerName));
            //add event audit
            eventAuditDispatch += String.format("%8s%s.eventReceived(typedEvent);%n", "", listenerName);
            //node invocation audit
            if(((Auditor)listenerField.instance).auditInvocations()){
                auditInvocation += String.format("%8s%s.nodeInvoked(node, nodeName, methodName, typedEvent);%n", "", listenerName);
                auditingInvocations = true;
            }
        }
        auditEvent += eventAuditDispatch + "}\n";
        if(auditingInvocations){
            auditEvent += auditInvocation + "}\n";
        }
        initialiseAuditor += "}\n";
        eventAuditDispatch = "auditEvent(typedEvent);\n";
        auditMethodString += auditEvent;
        auditMethodString += initialiseAuditor;
    }

    private void addDefacultImports() {
        model.getImportClasses().stream().map(Class::getCanonicalName).sorted().forEach(this::getClassName);
    }

}
