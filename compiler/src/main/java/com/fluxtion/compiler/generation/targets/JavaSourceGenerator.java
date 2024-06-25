/*
 * Copyright (c) 2019, 2024 gregory higgins.
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
package com.fluxtion.compiler.generation.targets;

import com.fluxtion.compiler.EventProcessorConfig;
import com.fluxtion.compiler.EventProcessorConfig.DISPATCH_STRATEGY;
import com.fluxtion.compiler.NodeDispatchTable;
import com.fluxtion.compiler.builder.filter.FilterDescription;
import com.fluxtion.compiler.generation.GenerationContext;
import com.fluxtion.compiler.generation.model.*;
import com.fluxtion.compiler.generation.util.ClassUtils;
import com.fluxtion.compiler.generation.util.NaturalOrderComparator;
import com.fluxtion.runtime.EventProcessorContext;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.builder.FluxtionIgnore;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.audit.EventLogManager;
import com.fluxtion.runtime.callback.ExportFunctionAuditEvent;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.input.EventFeed;
import com.fluxtion.runtime.node.ForkedTriggerTask;
import com.fluxtion.runtime.node.MutableEventProcessorContext;
import lombok.Getter;
import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.list.dsl.MirrorList;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.fluxtion.compiler.generation.targets.JavaGenHelper.mapPrimitiveToWrapper;
import static com.fluxtion.compiler.generation.targets.JavaGenHelper.mapWrapperToPrimitive;
import static org.apache.commons.lang3.StringEscapeUtils.escapeJava;

/**
 * @author Greg Higgins
 */
public class JavaSourceGenerator {

    private static final String s4 = "    ";
    private static final String s8 = "        ";
    private static final String s12 = "            ";
    private static final String s16 = "                ";
    private static final String s20 = "                    ";
    private static final String s24 = "                        ";
    /**
     * String representation of life-cycle callback methods for initialise,
     * sorted in call order, in a list.
     */
    @Getter
    private final ArrayList<String> initialiseMethodList;
    /**
     * String representation of life-cycle callback methods for initialise,
     * sorted in call order, in a list.
     */
    private final ArrayList<String> startMethodList;
    /**
     * String representation of life-cycle callback methods for initialise,
     * sorted in call order, in a list.
     */
    private final ArrayList<String> stopMethodList;
    /**
     * String representation of life-cycle callback methods for end of batch,
     * sorted in call order.
     */
    @Getter
    private final ArrayList<String> batchEndMethodList;
    /**
     * String representation of life-cycle callback methods for after event
     * processing, sorted in call order.
     */
    @Getter
    private final ArrayList<String> eventEndMethodList;
    /**
     * String representation of life-cycle callback methods for batch pause,
     * sorted in call order.
     */
    @Getter
    private final ArrayList<String> batchPauseMethodList;
    /**
     * String representation of life-cycle callback methods for tearDown, sorted
     * in call order.
     */
    @Getter
    private final ArrayList<String> tearDownMethodList;
    /**
     * String representation of node declarations as a list.
     */
    @Getter
    private final ArrayList<String> nodeDeclarationList;
    private final ArrayList<String> importList;
    /**
     * String representation of the initial member assignments for each node
     */
    @Getter
    private final ArrayList<String> nodeMemberAssignmentList;
    /**
     * String representation of public nodes as a list.
     */
    private final ArrayList<String> publicNodeIdentifierList;
    private final SimpleEventProcessorModel model;
    /**
     * use reflection to assign private members
     */
    private final boolean assignPrivateMembers;
    private final StringBuilder nodeDecBuilder = new StringBuilder(5 * 1000 * 1000);
    private final HashMap<String, String> importMap = new HashMap<>();
    private final StringBuilder ct = new StringBuilder(5 * 1000 * 1000);
    private final StringBuilder switchF = new StringBuilder(5 * 1000 * 1000);
    /**
     * Create an if based dispatch using instanceof operator
     */
    private final boolean instanceOfDispatch;
    private final EventProcessorConfig eventProcessorConfig;
    /**
     * String representation of life-cycle callback methods for initialise,
     * sorted in call order.
     */
    @Getter
    private String initialiseMethods;
    /**
     * String representation of life-cycle callback methods for initialise,
     * sorted in call order.
     */
    @Getter
    private String startMethods;
    /**
     * String representation of life-cycle callback methods for initialise,
     * sorted in call order.
     */
    @Getter
    private String stopMethods;
    /**
     * String representation of life-cycle callback methods for end of batch,
     * sorted in call order.
     */
    @Getter
    private String batchEndMethods;
    /**
     * String representation of life-cycle callback methods for end of batch,
     * sorted in call order.
     */
    @Getter
    private String eventEndMethods;
    /**
     * String representation of life-cycle callback methods for batch pause,
     * sorted in call order.
     */
    @Getter
    private String batchPauseMethods;
    /**
     * String representation of life-cycle callback methods for tearDown, sorted
     * in call order.
     */
    @Getter
    private String tearDownMethods;
    /**
     * String representation of node declarations.
     */
    @Getter
    private String nodeDeclarations;
    /**
     * String representation of dirty flag.
     */
    @Getter
    private String dirtyFlagDeclarations;
    /**
     * String representation of looking up dirty flag by instance
     */
    @Getter
    private String dirtyFlagLookup;
    /**
     * String representation of updating dirty flag by instance
     */
    @Getter
    private String dirtyFlagUpdate;
    /**
     * String representation of all dirty flag reset to false.
     */
    @Getter
    private String resetDirtyFlags;
    /**
     *
     */
    @Getter
    private String guardCheckMethods;
    /**
     * String representation of filter constants declarations.
     */
    @Getter
    private String filterConstantDeclarations;
    /**
     * String representation of the initial member assignments for each node
     */
    @Getter
    private String nodeMemberAssignments;
    @Getter
    private String dispatchOnlyNodeMemberAssignments;
    /**
     * String representation of top level event dispatch, branches on event type
     * -- GETTER --
     * String representation of top level event dispatch
     * <code> public void onEvent(com.fluxtion.api.event.Event event) </code>
     * <p>
     * <code>
     * public void handleEvent([specific event] event)
     * {
     * [eventHandlers]
     * <p>
     * }
     * </code>
     *
     * @return top level event dispatch string
     */
    @Getter
    private String eventDispatch;
    /**
     * String representing the delegated event handling methods for a specific
     * event type, will include branching based on filterId.
     * -- GETTER --
     * String representation of java code handling subclass of
     * <p>
     * , with support for specific
     * dispatch based upon
     * <p>
     * filterID. If
     * inlining is false the following output will be produced:
     * <p>
     * <code>
     * public void handleEvent([specific event] event)
     * {
     * [eventHandlers]
     * <p>
     * }
     * </code>
     *
     * @return type specific event dispatch code
     */
    @Getter
    private String eventHandlers;
    /**
     * String representing exported events
     */
    @Getter
    private String exportedMethods;
    /**
     * determines whether separate delegate eventHandling methods are generated.
     */
    private boolean isInlineEventHandling;
    /**
     * String representing event audit dispatch
     */
    private String eventAuditDispatch;
    /**
     * String representing event audit dispatch
     */
    @Getter
    private String assignNodeField;
    /**
     * are any auditors registered for this SEP
     */
    private boolean auditingEvent;
    private boolean auditingInvocations;
    private String auditMethodString;
    private String additionalInterfaces;
    @Getter
    private String javaDocEventClassList;
    @Getter
    private String forkDeclarations;
    @Getter
    private String resetForkTasks;
    final Set<Class<?>> nonUserClass;
    private String forkedAssignments;

    public JavaSourceGenerator(
            SimpleEventProcessorModel model, EventProcessorConfig eventProcessorConfig) {
        this.isInlineEventHandling = eventProcessorConfig.isInlineEventHandling();
        this.assignPrivateMembers = eventProcessorConfig.isAssignPrivateMembers();
        this.instanceOfDispatch = eventProcessorConfig.getDispatchStrategy() == DISPATCH_STRATEGY.INSTANCE_OF;
        this.model = model;
        this.eventProcessorConfig = eventProcessorConfig;
        this.eventHandlers = "";
        initialiseMethodList = new ArrayList<>();
        startMethodList = new ArrayList<>();
        stopMethodList = new ArrayList<>();
        batchEndMethodList = new ArrayList<>();
        batchPauseMethodList = new ArrayList<>();
        eventEndMethodList = new ArrayList<>();
        tearDownMethodList = new ArrayList<>();
        nodeDeclarationList = new ArrayList<>();
        nodeMemberAssignmentList = new ArrayList<>();
        publicNodeIdentifierList = new ArrayList<>();
        importList = new ArrayList<>();
        nonUserClass = new HashSet<>();
        nonUserClass.add(EventLogManager.class);
    }

    private static boolean hasIdField(Class e) {
        return false;
    }

    public void buildSourceModel() throws Exception {
        checkAuditInvocations();
        buildMethodSource(model.getInitialiseMethods(), initialiseMethodList);
        buildMethodSource(model.getStartMethods(), startMethodList);
        buildMethodSource(model.getStopMethods(), stopMethodList);
        buildMethodSource(model.getBatchPauseMethods(), batchPauseMethodList);
        buildForkAwareMethodSource(model.getEventEndMethods(), eventEndMethodList);
        buildMethodSource(model.getBatchEndMethods(), batchEndMethodList);
        buildMethodSource(model.getTearDownMethods(), tearDownMethodList);
        addDefaultImports();
        buildNodeDeclarations();
        buildDirtyFlags();
        buildForkDeclarations();
        buildFilterConstantDeclarations();
        buildMemberAssignments();
        buildNodeRegistrationListeners();
        buildNodeFieldAssigner();
        buildEventDispatch();

        initialiseMethods = "";
        boolean firstLine = true;
        for (String initialiseMethod : initialiseMethodList) {
            initialiseMethods += (firstLine ? "" : "\n") + initialiseMethod;
            firstLine = false;
        }

        startMethods = String.join("\n", startMethodList);
        stopMethods = String.join("\n", stopMethodList);

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
        dispatchOnlyNodeMemberAssignments = "";
        final StringBuilder memberAssignments = new StringBuilder(200 * nodeMemberAssignmentList.size());
        firstLine = true;
        for (String initialiseMethod : nodeMemberAssignmentList) {
            memberAssignments.append(firstLine ? "    " : "\n    ").append(initialiseMethod);
            firstLine = false;
        }

        if (model.isDispatchOnlyVersion()) {
            dispatchOnlyNodeMemberAssignments = "    @Override\n" +
                    "   public final void assignMembers(Map<String, Object> memberMap) {\n" +
                    "        memberMap.forEach((k, v) -> setField(k, v));\n" +
                    "        " + forkedAssignments +
                    "        " + memberAssignments +
                    "        if(subscriptionManager != null){\n" +
                    "            subscriptionManager.setSubscribingEventProcessor(this);\n" +
                    "        }\n" +
                    "        if(context != null) {\n" +
                    "            context.setEventProcessorCallback(this);\n" +
                    "        }\n" +
                    "  }\n\n";

            dispatchOnlyNodeMemberAssignments += "    @Override\n" +
                    "   public final void assignMembers(Map<String, Object> memberMap, Map<Object, Object> contextMap) {\n" +
                    "        memberMap.forEach((k, v) -> setField(k, v));\n" +
                    "        if(context != null) {\n" +
                    "            context.replaceMappings(contextMap);\n" +
                    "        }\n" +
                    "        " + forkedAssignments +
                    "        " + memberAssignments +
                    "        if(subscriptionManager != null){\n" +
                    "            subscriptionManager.setSubscribingEventProcessor(this);\n" +
                    "        }\n" +
                    "        if(context != null) {\n" +
                    "            context.setEventProcessorCallback(this);\n" +
                    "        }\n" +
                    "  }\n";
        } else {
            nodeMemberAssignments = memberAssignments.toString();
        }
    }

    private void checkAuditInvocations() {
        List<Field> listenerFields = model.getNodeRegistrationListenerFields();
        auditingInvocations = listenerFields.stream()
                .map(Field::getInstance)
                .filter(Auditor.class::isInstance)
                .map(Auditor.class::cast)
                .anyMatch(Auditor::auditInvocations);
    }

    private void buildForkDeclarations() {
        String forkWrapperClass = getClassTypeName(ForkedTriggerTask.class);
        forkDeclarations = "";
        forkedAssignments = "";
        if (model.getTriggerOnlyCallBacks().isEmpty()) {
            return;
        }
        if (model.isDispatchOnlyVersion()) {
            forkDeclarations = model.getTriggerOnlyCallBacks().stream()
                    .filter(CbMethodHandle::isForkExecution)
                    .map(c -> "private " + forkWrapperClass + " " + c.forkVariableName() + ";")
                    .collect(Collectors.joining("\n", "//Forked declarations\n", "\n"));
            forkedAssignments = model.getTriggerOnlyCallBacks().stream()
                    .filter(CbMethodHandle::isForkExecution)
                    .map(c -> c.forkVariableName()
                            + " = new " + forkWrapperClass + "(" + c.invokeLambdaString()
                            + ", \"" + c.variableName + "\" );")
                    .collect(Collectors.joining("\n", "\n//Forked assignment\n", "\n"));
        } else {
            forkDeclarations = model.getTriggerOnlyCallBacks().stream()
                    .filter(CbMethodHandle::isForkExecution)
                    .map(c -> "private final " + forkWrapperClass + " " + c.forkVariableName()
                            + " = new " + forkWrapperClass + "(" + c.invokeLambdaString()
                            + ", \"" + c.variableName + "\" );")
                    .collect(Collectors.joining("\n", "\n//Forked declarations\n", "\n"));
        }

        resetForkTasks = model.getTriggerOnlyCallBacks().stream()
                .filter(CbMethodHandle::isForkExecution)
                .map(c -> c.forkVariableName() + ".reinitialize();")
                .collect(Collectors.joining("\n"));
    }

    private void buildMethodSource(List<CbMethodHandle> methodList, List<String> methodSourceList) {
        for (CbMethodHandle method : methodList) {
            final String methodString = String.format("%8s%s.%s();", "", method.variableName, method.method.getName());
            methodSourceList.add(methodString);
        }
    }

    private void buildForkAwareMethodSource(List<CbMethodHandle> methodList, List<String> methodSourceList) {
        Set<Object> forkedTriggers = model.getForkedTriggerInstances();
        resetForkTasks = model.getTriggerOnlyCallBacks().stream()
                .filter(CbMethodHandle::isForkExecution)
                .map(c -> c.forkVariableName() + ".reinitialize();")
                .collect(Collectors.joining("\n"));
        for (CbMethodHandle method : methodList) {
            String waitForJoin = "";
            if (forkedTriggers.contains(method.instance)) {
                waitForJoin = method.forkVariableName() + ".afterEvent();\n";
            }
            final String methodString = String.format("%8s%s.%s();", "", method.variableName, method.method.getName());
            methodSourceList.add(waitForJoin + methodString);
        }
        methodSourceList.add(resetForkTasks);
    }


    private void buildDirtyFlags() {
        dirtyFlagDeclarations = "";
        resetDirtyFlags = "";
        dirtyFlagLookup = "";
        dirtyFlagUpdate = "";
        guardCheckMethods = "";
        final ArrayList<DirtyFlag> values = new ArrayList<>(model.getDirtyFieldMap().values());
        NaturalOrderComparator<DirtyFlag> comparator = new NaturalOrderComparator<>();
        values.sort((o1, o2) -> comparator.compare(o1.name, o2.name));
        for (DirtyFlag flag : values) {
            dirtyFlagDeclarations += String.format("%4sprivate boolean %s = false;%n", "", flag.name);
            resetDirtyFlags += String.format("%8s%s = false;%n", "", flag.name);
        }
        for (DirtyFlag flag : values) {
            if (flag.requiresInvert) {
                dirtyFlagDeclarations += String.format("%4sprivate boolean not%s = false;%n", "", flag.name);
                resetDirtyFlags += String.format("%8snot%s = false;%n", "", flag.name);
            }
        }
        List<Entry<Field, DirtyFlag>> sortedDirtyFlags = model.getDirtyFieldMap().entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getKey().getName()))
                .collect(Collectors.toList());
        sortedDirtyFlags
                .forEach(e -> {
                    dirtyFlagLookup += String.format("%12sdirtyFlagSupplierMap.put(%s, () -> %s);", "",
                            e.getKey().getName(), e.getValue().name);
                });
        sortedDirtyFlags
                .forEach(e -> {
                    dirtyFlagUpdate += String.format("%12sdirtyFlagUpdateMap.put(%s, (b) -> %s = b);", "",
                            e.getKey().getName(), e.getValue().name);
                });
        //build guard methods
        Set<Object> forkedTriggers = model.getForkedTriggerInstances();
        model.getNodeFields().forEach(field -> {
                    String guardConditions = model.getNodeGuardConditions(field.instance).stream()
                            .map(d -> (d.isRequiresInvert() ? "not" : "") + d.getName())
                            .collect(Collectors.joining(" |\n"));

                    String forkedReturn = model.getNodeGuardConditions(field.instance).stream()
                            .filter(f -> {
                                return forkedTriggers.contains(f.node.getInstance());
                            })
                            .map(d -> {
                                List<CbMethodHandle> updateListenerCbList = model.getParentUpdateListenerMethodMap().get(d.getNode().getInstance());
                                //child callbacks
                                boolean unguarded = false;
                                StringBuilder sbUnguarded = new StringBuilder();
                                StringBuilder guarded = new StringBuilder();
                                String parentVar = d.getNode().getName();
                                for (CbMethodHandle cbMethod : updateListenerCbList) {
                                    if (!cbMethod.method.getAnnotation(OnParentUpdate.class).guarded()) {
                                        if (auditingInvocations) {
                                            sbUnguarded.append(s24).append("auditInvocation(")
                                                    .append(cbMethod.variableName)
                                                    .append(", \"").append(cbMethod.variableName).append("\"")
                                                    .append(", \"").append(cbMethod.method.getName()).append("\"")
                                                    .append(", \"\"")
                                                    .append(");\n");
                                        }
                                        sbUnguarded.append(s20).append(cbMethod.variableName).append(".").append(cbMethod.method.getName()).append("(").append(parentVar).append(");\n");
                                    } else {
                                        if (auditingInvocations) {
                                            guarded.append(s24).append("auditInvocation(")
                                                    .append(cbMethod.variableName)
                                                    .append(", \"").append(cbMethod.variableName).append("\"")
                                                    .append(", \"").append(cbMethod.method.getName()).append("\"")
                                                    .append(", \"\"")
                                                    .append(");\n");
                                        }
                                        guarded.append(s24).append(cbMethod.variableName).append(".").append(cbMethod.method.getName()).append("(").append(parentVar).append(");\n");
                                    }
                                }

                                return d.getName() + " = " + d.getForkedName() + ".afterEvent();\n"
                                        + "if(" + d.getName() + "){\n"
                                        + guarded + "\n"
                                        + "}"
                                        + sbUnguarded;
                            })
                            .collect(Collectors.joining("\n"));

                    if (!model.getNodeGuardConditions(field.instance).isEmpty()) {
                        guardCheckMethods += String.format("%1$4sprivate boolean guardCheck_%2$s() {%n" +
                                forkedReturn +
                                "%1$8sreturn %3$s;" +
                                "}%n", "", field.getName(), guardConditions);
                    }
                }
        );
        dirtyFlagDeclarations = StringUtils.chomp(dirtyFlagDeclarations);
        resetDirtyFlags = StringUtils.chomp(resetDirtyFlags);
    }

    private String getClassTypeName(Class<?> clazzName) {
        return getClassName(clazzName.getCanonicalName());
    }

    private String getClassName(String clazzName) {
        clazzName = model.getMappedClass(clazzName);
        String[] split = clazzName.split("\\.");
        String ret = clazzName;
        if (split.length > 1) {
            String simpleName = split[split.length - 1];
            String pkgName = clazzName.replace("." + simpleName, "");
            ret = simpleName;
            if (clazzName.startsWith("java.lang")
                    || GenerationContext.SINGLETON.getPackageName().equals(pkgName)) {
                //ignore java.lang
            } else if (importMap.containsKey(simpleName)) {
                if (!importMap.get(simpleName).equalsIgnoreCase(clazzName)) {
                    ret = clazzName;
                }
            } else {
                importMap.put(simpleName, clazzName);
                importList.add(clazzName);
            }
        }
        return ret;
    }

    private void buildNodeDeclarations() {
        nodeDeclarations = "";
        final StringBuilder declarationBuilder = new StringBuilder(2000);
        final StringBuilder fqnBuilder = new StringBuilder(500);
        boolean firstLine = true;
        if (assignPrivateMembers) {
            declarationBuilder.append(s4).append("final net.vidageek.mirror.dsl.Mirror constructor = new net.vidageek.mirror.dsl.Mirror();\n");
        }
        for (Field field : model.getTopologicallySortedNodeFields()) {
            Class<?> fieldClass = field.instance.getClass();
            boolean dispatchOnlyVersion = model.isDispatchOnlyVersion();
            boolean isUserClass = !nonUserClass.contains(fieldClass);
            final String access = (field.publicAccess | (dispatchOnlyVersion & isUserClass)) ? "public" : "private";

            fqnBuilder.append(getClassName(field.fqn));
            boolean syntheticConstructor = false;
            try {
                fieldClass.getConstructor();
            } catch (Exception e) {
                syntheticConstructor = true;
            }
            StringBuilder declarationRoot = declarationBuilder.append(s4).append(access).append(" ");
            if (!dispatchOnlyVersion) {
                declarationRoot.append(" final ");
            }
            declarationRoot.append(fqnBuilder)
                    .append(model.getFieldSerializer().buildTypeDeclaration(field, this::getClassTypeName))
                    .append(" ")
                    .append(field.name);
            if (assignPrivateMembers && syntheticConstructor) {
                //new constructor.on(clazz).invoke().constructor().bypasser();
                declarationRoot
                        .append(" = constructor.on(").append(fqnBuilder).append(".class).invoke().constructor().bypasser();");
            } else {
                List<Field.MappedField> constructorArgs = model.constructorArgs(field.instance);
                if (String.class.isAssignableFrom(fieldClass)) {
                    declarationRoot
                            .append(" = ").append("\"")
                            .append(escapeJava((String) field.instance))
                            .append("\";");
                } else if (Integer.class.isAssignableFrom(fieldClass)) {
                    declarationRoot.append(" = ").append(field.instance).append(";");
                } else if (Float.class.isAssignableFrom(fieldClass)) {
                    declarationRoot.append(" = ").append(field.instance).append("f;");
                } else if (Double.class.isAssignableFrom(fieldClass)) {
                    if (!Double.isNaN((double) field.instance)) {
                        declarationRoot.append(" = ").append(field.instance).append("d;");
                    } else {
                        declarationRoot.append(" = Double.NaN;");
                    }
                } else if (Long.class.isAssignableFrom(fieldClass)) {
                    declarationRoot.append(" = ").append(field.instance).append("L;");
                } else if (Short.class.isAssignableFrom(fieldClass)) {
                    declarationRoot.append(" = (short)").append(field.instance).append(";");
                } else if (Byte.class.isAssignableFrom(fieldClass)) {
                    declarationRoot.append(" = (byte)").append(field.instance).append(";");
                } else if (Character.class.isAssignableFrom(fieldClass)) {
                    declarationRoot.append(" = '").append(field.instance).append("';");
                } else if (dispatchOnlyVersion && isUserClass) {
                    declarationRoot.append(" = null;");
                } else {
                    String generic = field.isGeneric() ? "<>" : "";
                    String args = constructorArgs.stream().map(Field.MappedField::value).collect(Collectors.joining(", "));
                    declarationRoot.append(" = new ").append(fqnBuilder).append(generic + "(" + args + ");");
                }
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
        ArrayList<FilterDescription> tmp = new ArrayList<>(model.getFilterDescriptionList());
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
                filterConstantDeclarations += (firstLine ? "" : "\n") + declaration;
                firstLine = false;
            }
        }
    }

    private void buildEventDispatch() {
        generateClassBasedDispatcher();
        generateExportMethodDispatcher();
        generateEventBufferedDispatcher();
        addEventAsJavaDoc();
        if (auditingEvent) {
            eventHandlers += auditMethodString;
        }
    }

    private void addEventAsJavaDoc() {
        javaDocEventClassList = ClassUtils.sortClassHierarchy(model.getHandlerOnlyDispatchMap().keySet()).stream()
                .map(Class::getCanonicalName)
                .collect(Collectors.joining(
                        "</li>\n*   <li>",
                        "*   <li>",
                        "</li>"
                ));
    }

    private void generateEventBufferedDispatcher() {
        if (!eventProcessorConfig.isSupportBufferAndTrigger()) {
            eventHandlers += "\n    public void bufferEvent(Object event) {" +
                    "        throw new UnsupportedOperationException(\"bufferEvent not supported\");\n" +
                    "    }\n";
            eventHandlers += "\n    public void triggerCalculation() {" +
                    "        throw new UnsupportedOperationException(\"triggerCalculation not supported\");\n" +
                    "    }\n";
            return;
        }

        boolean patternSwitch = eventProcessorConfig.getDispatchStrategy() == DISPATCH_STRATEGY.PATTERN_MATCH;
        StringBuilder noTriggerDispatch = new StringBuilder("\n    public void bufferEvent(Object event){\n" +
                "        buffering = true;\n");
        //build buffer event method
        String bufferEvents = "";
        if (instanceOfDispatch) {
            bufferEvents = "";
        } else if (!patternSwitch) {
            noTriggerDispatch.append("        switch (event.getClass().getName()) {\n");
        } else if (patternSwitch) {
            noTriggerDispatch.append("        switch (event) {\n");
        }
        Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> handlerOnlyDispatchMap = model.getHandlerOnlyDispatchMap();
        //sort class so repeatable
        boolean prev = isInlineEventHandling;
        isInlineEventHandling = true;
        //sort the classes and then loop through the sorted list
        List<Class<?>> sortedClasses = ClassUtils.sortClassHierarchy(handlerOnlyDispatchMap.keySet());
        sortedClasses.remove(ExportFunctionMarker.class);
        String elsePrefix = "if";
        for (Class<?> eventId : sortedClasses) {
            Map<FilterDescription, List<CbMethodHandle>> m = handlerOnlyDispatchMap.get(eventId);
            String className = getClassName(eventId.getCanonicalName());
            if (instanceOfDispatch) {
                String eventClassName = mapPrimitiveToWrapper(eventId).getName().replace("$", ".");
                noTriggerDispatch.append(String.format("%12s (event instanceof %s) {%n", elsePrefix, eventClassName));
                elsePrefix = "else if";
                noTriggerDispatch.append(String.format("%16s%s typedEvent = (%s)event;%n", "", className, className));
            } else if (patternSwitch) {
                noTriggerDispatch.append(String.format("%12scase %s typedEvent -> {", "", className));
            } else {
                noTriggerDispatch.append(String.format("%12scase (\"%s\"):{%n", "", eventId.getName()));
                noTriggerDispatch.append(String.format("%16s%s typedEvent = (%s)event;%n", "", className, className));
            }
            noTriggerDispatch.append(buildFilteredDispatch(m, Collections.emptyMap(), eventId));
            if (instanceOfDispatch) {
                noTriggerDispatch.append(String.format("%12s}%n", ""));
            } else if (patternSwitch) {
                noTriggerDispatch.append(String.format("%12s}%n", ""));
            } else {
                noTriggerDispatch.append(String.format("%16sbreak;%n", ""));
                noTriggerDispatch.append(String.format("%12s}%n", ""));
            }
        }
        if (!instanceOfDispatch) {
            if (!handlerOnlyDispatchMap.keySet().contains(Object.class) && patternSwitch) {
                noTriggerDispatch.append("default -> {}");
            }
            noTriggerDispatch.append(String.format("%8s}%n", ""));
        }
        noTriggerDispatch.append(String.format("%4s}%n", ""));

        bufferEvents += noTriggerDispatch.toString();
        //build buffered dispatch trigger
        Map<FilterDescription, List<CbMethodHandle>> cbMap = new HashMap<>();
        List<CbMethodHandle> triggerOnlyCallBacks = model.getTriggerOnlyCallBacks();
        if (!triggerOnlyCallBacks.isEmpty()) {
            cbMap.put(FilterDescription.DEFAULT_FILTER, triggerOnlyCallBacks);
        }
        Map<FilterDescription, List<CbMethodHandle>> cbMapPostDispatch = new HashMap<>();
        cbMapPostDispatch.put(FilterDescription.DEFAULT_FILTER, model.getAllPostEventCallBacks());
        String dispatchString = buildFilteredSwitch(cbMap, cbMapPostDispatch, Object.class, false, true);
        String bufferedTrigger = "\n    public void triggerCalculation(){\n" +
                "        buffering = false;\n" +
                "        String typedEvent = \"No event information - buffered dispatch\";\n" +
                (dispatchString == null ? "" : dispatchString) +
                "        afterEvent();\n" +
                "\n    }\n";
        isInlineEventHandling = prev;
        eventHandlers += bufferEvents;
        eventHandlers += bufferedTrigger;
    }

    /**
     * generates the implementation of the onEvent method, and writes to the
     * eventDispatch or debugEventDispatch string. This is the top level method
     * called, and dispatches methods internally. The generated method, switches
     * on class type, casts to the correct object and then invokes specific
     * event handling for that type.
     */
    private void generateClassBasedDispatcher() {
        boolean patternSwitch = eventProcessorConfig.getDispatchStrategy() == DISPATCH_STRATEGY.PATTERN_MATCH;
        String dispatchStringNoId = "        switch (event.getClass().getName()) {\n";
        if (instanceOfDispatch) {
            dispatchStringNoId = "";
        } else if (patternSwitch) {
            dispatchStringNoId = "        switch (event) {\n";
        }
        Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> dispatchMap = model.getDispatchMap();
        Map<Class<?>, Map<FilterDescription, List<CbMethodHandle>>> postDispatchMap = model.getPostDispatchMap();
        Set<Class<?>> keySet = dispatchMap.keySet();
        HashSet<Class<?>> classSet = new HashSet<>(keySet);
        classSet.addAll(postDispatchMap.keySet());
        classSet.remove(ExportFunctionMarker.class);
        List<Class<?>> clazzList = ClassUtils.sortClassHierarchy(classSet);
        String elsePrefix = "if";
        for (Class<?> eventId : clazzList) {
            String className = getClassName(eventId.getCanonicalName());
            if (instanceOfDispatch) {
                String eventClassName = mapPrimitiveToWrapper(eventId).getName().replace("$", ".");
                dispatchStringNoId += String.format("%12s (event instanceof %s) {%n", elsePrefix, eventClassName);
                elsePrefix = "else if";
                dispatchStringNoId += String.format("%16s%s typedEvent = (%s)event;%n", "", className, className);
            } else if (patternSwitch) {
                dispatchStringNoId += String.format("%12scase %s typedEvent -> ", "", className);
            } else {
                dispatchStringNoId += String.format("%12scase (\"%s\"):{%n", "", eventId.getName());
                dispatchStringNoId += String.format("%16s%s typedEvent = (%s)event;%n", "", className, className);
            }
            Map<FilterDescription, List<CbMethodHandle>> cbMap = dispatchMap.get(eventId);
            Map<FilterDescription, List<CbMethodHandle>> cbMapPostEvent = postDispatchMap.get(eventId);
            dispatchStringNoId += buildFilteredDispatch(cbMap, cbMapPostEvent, eventId);
            if (instanceOfDispatch) {
                dispatchStringNoId += String.format("%12s}%n", "");
            } else if (patternSwitch) {
//                dispatchStringNoId += String.format("%n");
            } else {
                dispatchStringNoId += String.format("%16sbreak;%n", "");
                dispatchStringNoId += String.format("%12s}%n", "");
            }
        }
        //default handling
        if (!instanceOfDispatch) {
            if (keySet.contains(Object.class) && !patternSwitch) {
                dispatchStringNoId += String.format("%12sdefault :{%n", "");
                dispatchStringNoId += String.format("%16shandleEvent(event);%n", "");
                dispatchStringNoId += String.format("%12s}%n", "");
            } else if (!keySet.contains(Object.class) && patternSwitch) {
                dispatchStringNoId += "default -> {unKnownEventHandler(event);}";
            }
            dispatchStringNoId += String.format("%8s}%n", "");
        } else if (!keySet.contains(Object.class)) {
            dispatchStringNoId += String.format("%12selse{ %n", "");
            dispatchStringNoId += String.format("%12sunKnownEventHandler(event); %n", "");
            dispatchStringNoId += String.format("%12s}%n", "");
        }
        if (isInlineEventHandling) {
            dispatchStringNoId += String.format("%8safterEvent();%n", "");
        }
        //build a noIddispatchString - just copy method above and only
        //process <Event>.ID free events.
        eventDispatch = dispatchStringNoId + "\n";
        eventHandlers += "  //EVENT DISPATCH - END\n";
    }

    private void generateExportMethodDispatcher() {
        Map<FilterDescription, List<CbMethodHandle>> eventDispatch = model.getDispatchMap().get(ExportFunctionMarker.class);
        Map<FilterDescription, List<CbMethodHandle>> postDispatch = model.getPostDispatchMap().get(ExportFunctionMarker.class);
        final String audit;
        List<Field> listenerFields = model.getNodeRegistrationListenerFields();
        if (listenerFields != null && !listenerFields.isEmpty()) {
            audit = "beforeServiceCall(\"&&FUNC&&\");\n" +
                    "ExportFunctionAuditEvent typedEvent = functionAudit;\n";
        } else {
            audit = "beforeServiceCall(\"&&FUNC&&\");\n";
        }
        if (eventDispatch != null) {
            eventHandlers += "\n//EXPORTED SERVICE FUNCTIONS - START\n";
            List<FilterDescription> list = new ArrayList<>(eventDispatch.keySet());
            list.sort(Comparator.comparing(FilterDescription::getStringValue));
            list.forEach(f -> {
                String exportAudit = "";
                if (f.getExportFunction() != null) {
                    exportAudit = f.getExportFunction().toGenericString();
                } else {
                    return;
                }
                StringBuilder sb = new StringBuilder(f.getStringValue() + "{\n")
                        .append(audit.replace("&&FUNC&&", exportAudit));
                buildDispatchForCbMethodHandles(eventDispatch.get(f), sb);
                buildPostDispatchForCbMethodHandles(postDispatch.get(f), sb);
                sb.append("afterServiceCall();\n");
                sb.append(f.getStringValue().contains("void") ? "}\n" : "return true;}\n");
                eventHandlers += sb.toString();
            });
            eventHandlers += "//EXPORTED SERVICE FUNCTIONS - END\n";
        }
    }

    private String buildFilteredSwitch(Map<FilterDescription, List<CbMethodHandle>> cbMap,
                                       Map<FilterDescription, List<CbMethodHandle>> cbMapPostEvent, Class eventClass,
                                       boolean intFilter, boolean noFilter) {
        Set<FilterDescription> filterIdSet = new HashSet<>(cbMap.keySet());
        filterIdSet.addAll(cbMapPostEvent.keySet());
        ArrayList<FilterDescription> clazzList = new ArrayList<>(filterIdSet);
        clazzList.sort((FilterDescription o1, FilterDescription o2) -> {
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

        switchF.delete(0, switchF.length());
        for (FilterDescription filterDescription : clazzList) {

            //INVOKE TARGET
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
                    invokerTarget.filterDescription = new FilterDescription(eventClass);
                    invokerTarget.methodName = JavaGenHelper.generateFilteredDispatchMethodName(invokerTarget.filterDescription);
                    invokerTarget.stringMapName = JavaGenHelper.generateFilteredDispatchMap(invokerTarget.filterDescription);
                    invokerTarget.intMapName = JavaGenHelper.generateFilteredDispatchMap(invokerTarget.filterDescription);
                } else if (noFilter || isNoFilter || isDefaultFilter) {
                    //ignore the NO_FILTER
                    continue;
                } else {
                    //build switch header
                    if (filterDescription.comment != null) {
                        switchF.append(s20).append("//").append(filterDescription.comment).append("\n");
                    }
                    if (filterVariable == null) {
                        switchF.append(s20).append("case(").append(filterValue).append("):\n");

                    } else {
                        switchF.append(s20).append("case(").append(filterVariable).append("):\n");
                    }
                }
                ct.delete(0, ct.length());
                buildDispatchForCbMethodHandles(cbList, ct);
                buildPostDispatchForCbMethodHandles(cbMapPostEvent.get(filterDescription), ct);
                invokerTarget.methodBody = ct.toString();
                if (!noFilter) {
                    ct.append(s24 + "afterEvent();\n");
                    ct.append(s24 + "return;\n");
                }
                switchF.append(ct);
            }
        }
        return switchF.length() == 0 ? null : switchF.toString();
    }

    private void buildDispatchForCbMethodHandles(List<CbMethodHandle> cbList, StringBuilder stringBuilder) {
        if (cbList == null || cbList.isEmpty()) {
            return;
        }
        for (CbMethodHandle method : cbList) {
            DirtyFlag dirtyFlagForUpdateCb = model.getDirtyFlagForUpdateCb(method);
            String dirtyAssignment = "";
            if (dirtyFlagForUpdateCb != null) {
                if (dirtyFlagForUpdateCb.alwaysDirty) {
                    dirtyAssignment = dirtyFlagForUpdateCb.name + " = true;\n" + s24;
                } else {
                    dirtyAssignment = dirtyFlagForUpdateCb.name + " = ";
                }
            }
            //protect with guards
            Collection<DirtyFlag> nodeGuardConditions = model.getNodeGuardConditions(method);
            //HERE TO JOIN THINGS
            String OR = "";
            if (nodeGuardConditions.size() > 0) {
                stringBuilder.append(s24).append("if(guardCheck_" + method.getVariableName() + "()) {\n");
            }

            //add audit
            if (auditingInvocations) {
                if (method.isForkExecution()) {
                    stringBuilder.append(s24).append("auditInvocation(")
                            .append(method.forkVariableName())
                            .append(", \"").append(method.variableName).append("\"")
                            .append(", \"").append(method.method.getName()).append("\"")
                            .append(", typedEvent")
                            .append(");\n");
                } else {
                    stringBuilder.append(s24).append("auditInvocation(")
                            .append(method.variableName)
                            .append(", \"").append(method.variableName).append("\"")
                            .append(", \"").append(method.method.getName()).append("\"")
                            .append(", typedEvent")
                            .append(");\n");
                }
            }
            //assign return if appropriate
            if (method.parameterClass == null) {//triggers
                if (method.isForkExecution()) {
                    stringBuilder.append(method.forkVariableName() + ".onTrigger();\n");
                } else {
                    stringBuilder.append(s24).append(dirtyAssignment).append(method.getMethodTarget()).append(".").append(method.method.getName()).append("();\n");
                }
            } else if (method.isExportedHandler()) {
                StringJoiner sjInvoker = new StringJoiner(", ", "(", ");\n\t");
                for (int i = 0; i < method.getMethod().getParameterCount(); i++) {
                    sjInvoker.add("arg" + i);
                }
                if (dirtyFlagForUpdateCb != null) {
                    if (method.getMethod().getReturnType() == boolean.class) {
                        dirtyAssignment = dirtyFlagForUpdateCb.name + " = ";
                    } else {
                        //dirtyAssignment = dirtyFlagForUpdateCb.name + " = true;\n" + s24;
                    }
                }
                stringBuilder.append(s24).append(dirtyAssignment).append(method.getMethodTarget()).append(".").append(method.method.getName()).append(sjInvoker);
            } else {
                stringBuilder.append(s24).append(dirtyAssignment).append(method.getMethodTarget()).append(".").append(method.method.getName()).append("(typedEvent);\n");
            }
            if (dirtyFlagForUpdateCb != null && dirtyFlagForUpdateCb.requiresInvert) {
                stringBuilder.append(s24).append("not" + dirtyFlagForUpdateCb.name + " = !" + dirtyFlagForUpdateCb.name + ";\n");
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
            final OnEventHandler handlerAnnotation = method.method.getAnnotation(OnEventHandler.class);
            if (handlerAnnotation != null && (!handlerAnnotation.propagate())) {
            } else if (model.getForkedTriggerInstances().contains(parent)) {
                //close guards clause
                if (nodeGuardConditions.size() > 0) {
                    //callTree += String.format("%16s}\n", "");
                    stringBuilder.append(s16 + "}\n");
                }
            } else {
                if (parentFlag != null && updateListenerCbList.size() > 0) {
                    //callTree += String.format("%20sif(%s) {\n", "", parentFlag.name);
                    stringBuilder.append(s20 + "if(").append(parentFlag.name).append(") {\n");
                }
                //child callbacks
                boolean unguarded = false;
                StringBuilder sbUnguarded = new StringBuilder();
                for (CbMethodHandle cbMethod : updateListenerCbList) {
                    //callTree += String.format("%24s%s.%s(%s);%n", "", cbMethod.variableName, cbMethod.method.getName(), parentVar);
                    if (!cbMethod.method.getAnnotation(OnParentUpdate.class).guarded()) {
                        unguarded = true;
                        sbUnguarded.append(s20).append(cbMethod.variableName).append(".").append(cbMethod.method.getName()).append("(").append(parentVar).append(");\n");
                    } else {
                        stringBuilder.append(s24).append(cbMethod.variableName).append(".").append(cbMethod.method.getName()).append("(").append(parentVar).append(");\n");
                    }
                }
                if (parentFlag != null && updateListenerCbList.size() > 0) {
                    //callTree += String.format("%20s}\n", "", parentFlag.name);
                    stringBuilder.append(s20).append("}\n");
                    if (unguarded) {
                        stringBuilder.append(sbUnguarded);
                    }
                }
                //close guards clause
                if (nodeGuardConditions.size() > 0) {
                    //callTree += String.format("%16s}\n", "");
                    stringBuilder.append(s16 + "}\n");
                }
            }
        }
    }

    private void buildPostDispatchForCbMethodHandles(List<CbMethodHandle> cbList, StringBuilder stringBuilder) {
        if (cbList == null || cbList.isEmpty()) {
            return;
        }
        stringBuilder.append(s16 + "//event stack unwind callbacks\n");
        for (CbMethodHandle method : cbList) {
            //protect with guards
            Collection<DirtyFlag> nodeGuardConditions = model.getNodeGuardConditions(method);
            String OR = "";
            if (!nodeGuardConditions.isEmpty()) {
                Set<Object> forkedTriggers = model.getForkedTriggerInstances();
                if (forkedTriggers.contains(method.getInstance())) {
                    stringBuilder.append(method.forkVariableName()).append(".afterEvent();\n");
                }
                stringBuilder.append(s24 + "if(");
                for (DirtyFlag nodeGuardCondition : nodeGuardConditions) {
                    stringBuilder.append(OR).append(nodeGuardCondition.name);
                    OR = " | ";
                }
                stringBuilder.append(") {\n");
            }
            stringBuilder.append(s24).append(method.variableName).append(".").append(method.method.getName()).append("();\n");
            //close guarded clause
            if (!nodeGuardConditions.isEmpty()) {
                stringBuilder.append(s16 + "}\n");
            }
        }
    }

    private String buildFilteredDispatch(Map<FilterDescription, List<CbMethodHandle>> cbMap,
                                         Map<FilterDescription, List<CbMethodHandle>> cbMapPostEvent,
                                         Class eventClass) {
        String dispatchString = "";
        String eventHandlerString = "";
        String intFilterSwitch = buildFilteredSwitch(cbMap, cbMapPostEvent, eventClass, true, false);
        String stringFilterSwitch = buildFilteredSwitch(cbMap, cbMapPostEvent, eventClass, false, false);
        String noFilterDispatch = buildFilteredSwitch(cbMap, cbMapPostEvent, eventClass, false, true);
        if (!isInlineEventHandling) {
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
        eventHandlers += eventHandlerString;
        return dispatchString;
    }

    private void buildMemberAssignments() throws Exception {
        final List<Field> nodeFields = model.getNodeFields();
        if (assignPrivateMembers) {
            nodeMemberAssignmentList.add("    final net.vidageek.mirror.dsl.Mirror assigner = new net.vidageek.mirror.dsl.Mirror();");
        }
        boolean dispatchOnlyVersion = model.isDispatchOnlyVersion();
        for (Field field : nodeFields) {
            Object object = field.instance;
            String varName = field.name;
            Class<?> fieldClass = object.getClass();
            boolean isUserClass = !nonUserClass.contains(fieldClass);
            if (isUserClass && dispatchOnlyVersion) {
                continue;
            }
            model.beanProperties(object).stream().forEach(s -> nodeMemberAssignmentList.add(varName + "." + s + ";"));
            java.lang.reflect.Field[] fields = object.getClass().getFields();
            MirrorList<java.lang.reflect.Field> fields1 = new Mirror().on(object.getClass()).reflectAll().fields();
            fields = fields1.toArray(fields);
            for (java.lang.reflect.Field instanceField : fields) {
                boolean useRefelction = false;
                if ((instanceField.getModifiers() & (Modifier.STATIC | Modifier.TRANSIENT)) != 0) {
                    continue;
                }
                if (instanceField.getAnnotation(FluxtionIgnore.class) != null) {
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
                    } else if (instanceField.getType().isPrimitive()) {
                        String value = instanceField.get(object).toString();
                        value = value.equalsIgnoreCase("NaN") ? "Double.NaN" : value;
                        value = "(" + instanceField.getType() + ")" + value;
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
                                    if (integerListType.getActualTypeArguments()[0] instanceof Class) {
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

    public ArrayList<String> getPublicNodeList() {
        return publicNodeIdentifierList;
    }

    public String getAdditionalInterfaces() {
        return additionalInterfaces == null ? "" : additionalInterfaces;
    }

    public int getDirtyFlagCount() {
        return model.getDirtyFieldMap() == null ? 32 : model.getDirtyFieldMap().size();
    }

    public String getImports() {
        List<String> dedupeList = new ArrayList<>(new HashSet<>(importList));
        Collections.sort(dedupeList);
        StringBuilder sb = new StringBuilder(2048);
        dedupeList.stream().forEach(s -> {
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
        String eventClassName = getClassTypeName(Event.class);
        importList.add(Event.class.getCanonicalName());
        importList.add(EventProcessorContext.class.getCanonicalName());
        importList.add(MutableEventProcessorContext.class.getCanonicalName());
        importList.add(Map.class.getCanonicalName());
        importList.add(EventFeed.class.getCanonicalName());
        importList.add(EventLogManager.class.getCanonicalName());
        importList.add(ExportFunctionAuditEvent.class.getCanonicalName());
        auditMethodString = "";
        String auditObjet = "private void auditEvent(Object typedEvent){\n";
        String auditEvent = String.format("private void auditEvent(%s typedEvent){\n", eventClassName);
        String auditInvocation = "private void auditInvocation(Object node, String nodeName, String methodName, Object typedEvent){\n";
        String initialiseAuditor = "private void initialiseAuditor(" + getClassName(Auditor.class.getName()) + " auditor){\n"
                + "\tauditor.init();\n";
        for (Field nodeField : nodeFields) {
            String nodeName = nodeField.name;
            if (listenerFields.stream().anyMatch((t) -> t.name.equals(nodeName))) {
                continue;
            }
            initialiseAuditor += (String.format("auditor.nodeRegistered(%s, \"%s\");", nodeName, nodeName));
        }
        initialiseAuditor += model.getTriggerOnlyCallBacks().stream()
                .filter(CbMethodHandle::isForkExecution)
                .map(CbMethodHandle::forkVariableName)
                .map(s -> String.format("auditor.nodeRegistered(%s, \"%s\");", s, s))
                .collect(Collectors.joining());


        eventAuditDispatch = "";
        nodeMemberAssignmentList.add("\t//node auditors");
        for (Field listenerField : listenerFields) {
            String listenerName = listenerField.name;
            nodeMemberAssignmentList.add("initialiseAuditor(" + listenerField.name + ");");
            //add init
            //initialiseMethodList.add(String.format("%8s%s.init();", "", listenerName));
            //add tear down
            tearDownMethodList.add(0, String.format("%8s%s.tearDown();", "", listenerName));
            //add event complete
            eventEndMethodList.add(String.format("%8s%s.processingComplete();", "", listenerName));
            //add event audit
            eventAuditDispatch += String.format("%8s%s.eventReceived(typedEvent);%n", "", listenerName);
            //node invocation audit
            if (((Auditor) listenerField.instance).auditInvocations()) {
                auditInvocation += String.format("%8s%s.nodeInvoked(node, nodeName, methodName, typedEvent);%n", "", listenerName);
                auditingInvocations = true;
            }
        }
        auditEvent += eventAuditDispatch + "}\n";
        auditObjet += eventAuditDispatch + "}\n";
        if (auditingInvocations) {
            auditEvent += auditInvocation + "}\n";
        }
        initialiseAuditor += "}\n";
        eventAuditDispatch = "auditEvent(typedEvent);\n";
        auditMethodString += auditObjet;
        auditMethodString += auditEvent;
        auditMethodString += initialiseAuditor;
    }

    private void buildNodeFieldAssigner() {
        boolean dispatchOnlyVersion = model.isDispatchOnlyVersion();
        if (!dispatchOnlyVersion) {
            assignNodeField = "";
            return;
        }

        String prefix = "  public <T> void setField(String fieldName, T field){\n" +
                "    switch (fieldName){\n";
        String suffix = "      default: {}\n" +
                "    }\n" +
                "  }\n";

        final List<Field> nodeFields = model.getNodeFields();
        StringBuilder switchString = new StringBuilder(prefix);
        for (Field nodeField : nodeFields) {
            String nodeName = nodeField.name;
            Class<?> nodeClass = nodeField.getInstance().getClass();
            if (nonUserClass.contains(nodeClass)) {
                continue;
            }
            String className = getClassTypeName(nodeClass);//.getCanonicalName();
            switchString.append(
                    String.format("%1$7s case \"%2$s\" :{\n%1$9s %2$s = (%3$s) field;\n%1$7s break;}\n", "", nodeName, className)
            );
        }

        switchString.append(suffix);
        assignNodeField = switchString.toString();
    }

    private void addDefaultImports() {
        model.getImportClasses().stream()
                .map(Class::getCanonicalName)
                .peek(Objects::toString)
                .sorted()
                .forEach(this::getClassName);
    }

    public void additionalInterfacesToImplement(Set<Class<?>> interfacesToImplement) {
        additionalInterfaces = "";
        if (!interfacesToImplement.isEmpty()) {
            additionalInterfaces = interfacesToImplement.stream()
                    .map(this::getClassTypeName)
                    .sorted()
                    .collect(Collectors.joining(", ", "\n /*--- @ExportService start ---*/\n ", ",\n/*--- @ExportService end ---*/\n"));
        }
        if (model.isDispatchOnlyVersion()) {
            additionalInterfaces += getClassTypeName(NodeDispatchTable.class) + ",\n";
        }
    }
}
