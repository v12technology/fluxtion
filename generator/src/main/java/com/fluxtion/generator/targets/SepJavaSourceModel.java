package com.fluxtion.generator.targets;

import com.google.common.base.Predicates;
import com.fluxtion.api.generation.FilterDescription;
import com.fluxtion.generator.model.CbMethodHandle;
import com.fluxtion.generator.model.DirtyFlag;
import com.fluxtion.generator.model.Field;
import com.fluxtion.generator.model.SimpleEventProcessorModel;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.reflections.ReflectionUtils;
import static org.reflections.ReflectionUtils.withModifier;
import static org.reflections.ReflectionUtils.withName;
import static org.reflections.ReflectionUtils.withType;

/**
 *
 * @author Greg Higgins
 */
public class SepJavaSourceModel {

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
     * String representation of template method for test event dispatch
     */
    private String testDispatch;

    /**
     * String representation of public nodes as a list.
     */
    private final ArrayList<String> publicNodeIdentifierList;

    private final SimpleEventProcessorModel model;

    public SepJavaSourceModel(SimpleEventProcessorModel model) {
        this(model, false);
    }

    public SepJavaSourceModel(SimpleEventProcessorModel model, boolean inlineEventHandling) {
        this.model = model;
        this.eventHandlers = "";
        this.debugEventHandlers = "";
        this.isInlineEventHandling = inlineEventHandling;
        initialiseMethodList = new ArrayList<>();
        batchEndMethodList = new ArrayList<>();
        batchPauseMethodList = new ArrayList<>();
        eventEndMethodList = new ArrayList<>();
        tearDownMethodList = new ArrayList<>();
        nodeDeclarationList = new ArrayList<>();
        nodeMemberAssignmentList = new ArrayList<>();
        publicNodeIdentifierList = new ArrayList<>();
    }

    public void buildSourceModel() throws Exception {
        buildMethodSource(model.getInitialiseMethods(), initialiseMethodList);
        buildMethodSource(model.getBatchPauseMethods(), batchPauseMethodList);
        buildMethodSource(model.getEventEndMethods(), eventEndMethodList);
        buildMethodSource(model.getBatchEndMethods(), batchEndMethodList);
        buildMethodSource(model.getTearDownMethods(), tearDownMethodList);
        buildNodeDeclarations();
        buildDirtyFlags();
        buildFilterConstantDeclarations();
        buildMemberAssignments();
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
        firstLine = true;
        for (String initialiseMethod : nodeMemberAssignmentList) {
            nodeMemberAssignments += (firstLine ? "    " : "\n    ") + initialiseMethod;
            firstLine = false;
        }
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
        for (DirtyFlag flag : model.getDirtyFieldMap().values()) {
            dirtyFlagDeclarations += String.format("%4sprivate boolean %s = false;%n", "", flag.name);
            resetDirtyFlags += String.format("%8s%s = false;%n", "", flag.name);
        }
        dirtyFlagDeclarations = StringUtils.chomp(dirtyFlagDeclarations);
        resetDirtyFlags = StringUtils.chomp(resetDirtyFlags);
    }

    private void buildNodeDeclarations() {
        nodeDeclarations = "";
        boolean firstLine = true;
        for (Field field : model.getNodeFields()) {
            final String access = field.publicAccess ? "public" : "private";
            //perhaps generate generic info for the declarations - complicated, needs to be recursive
            //not sure on the implementation - because of erasure it makes no difference to runtime, but would 
            //provide extra compile time checking on the statemachine.
//            String types = "";
//            if (field.instance instanceof EventHandler) {
//                //generate generics as we know the supertype
//                Class<?>[] genericTypes = TypeResolver.resolveRawArguments(EventHandler.class, field.instance.getClass());
//                if (genericTypes != null && genericTypes.length > 0) {
//                    types = "<";
//                    for (Class<?> genericType : genericTypes) {
//                        types += genericType.getCanonicalName() + ",";
//                    }
//                    types = types.substring(0, types.length()-1) +  ">";
//                }
//            }

            final String declaration = String.format("%4s%s final %s %s = new %s();", "", access, field.fqn.replace("$", "."), field.name, field.fqn.replace("$", "."));
            nodeDeclarationList.add(declaration);
            nodeDeclarations += (firstLine ? "" : "\n") + declaration;
            firstLine = false;
            if (field.publicAccess) {
                publicNodeIdentifierList.add(field.name);
            }
        }
    }

    private void buildFilterConstantDeclarations() {
        filterConstantDeclarations = "";
        boolean firstLine = true;
        ArrayList<FilterDescription> tmp = new ArrayList(model.getFilterDescriptionList());
        tmp.sort((FilterDescription o1, FilterDescription o2) -> {
            return o1.value - o2.value;
        });
        for (FilterDescription filterDescription : tmp) {
            if (filterDescription.variableName != null) {
                final String declaration = String.format("    "
                        + "public static final int %s = %d;",
                        filterDescription.variableName, filterDescription.value);
//            nodeDeclarationList.add(declaration);
                filterConstantDeclarations += (firstLine ? "" : "\n") + declaration;
                firstLine = false;
            }
        }
    }

    private void buildEventDispatch() {
        buildEventDispatch(false);
        buildEventDispatch(true);
    }

    private void buildEventDispatch(boolean isDebug) {
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
            if (hasIdField(eventId)) {
                dispatchString += String.format("%12scase (%s.ID):{%n", "", eventId.getCanonicalName());
                dispatchString += String.format("%16s%s typedEvent = (%s)event;%n", "", eventId.getCanonicalName(), eventId.getCanonicalName());
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
                dispatchStringNoId += String.format("%16s%s typedEvent = (%s)event;%n", "", eventId.getCanonicalName(), eventId.getCanonicalName());
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
            eventDispatch = dispatchString;
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

    private String buildFilteredSwitch(Map<FilterDescription, List<CbMethodHandle>> cbMap,
            Map<FilterDescription, List<CbMethodHandle>> cbMapPostEvent,
            boolean isDebug, boolean intFilter, boolean noFilter) {
        Set<FilterDescription> filterIdSet = cbMap.keySet();
        //generate all the booleans for each CbMethodHandle in the cbMap and store in a map
        //
        //TODO produce combined list of cbMap and cbMapPostEvent
        ArrayList<FilterDescription> clazzList = new ArrayList<>(filterIdSet);

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
        for (FilterDescription filterDescription : clazzList) {
            if (intFilter == filterDescription.isIntFilter || (noFilter)) {
                boolean noFilterDescription = filterDescription == FilterDescription.NO_FILTER;
                String filterValue = intFilter ? filterDescription.value + "" : "\"" + filterDescription.stringValue + "\"";
                String filterVariable = filterDescription.variableName;
                List<CbMethodHandle> cbList = cbMap.get(filterDescription);
                if (noFilter && noFilterDescription) {
                    //progress without header
                } else if (noFilter || noFilterDescription) {
                    //ignore the NO_FILTER
                    continue;
                } else {
                    //build switch header
                    if (filterDescription.comment != null) {
                        //Filter comment
                        switchFilter += String.format("%-20s%s%n", "//", filterDescription.comment);
                    }
                    if (filterVariable == null) {
                        switchFilter += String.format("%20scase (%s):%n", "", filterValue);
                    } else {
                        switchFilter += String.format("%20scase (%s):%n", "", filterVariable);
                    }
                }
                //TODO check for null here on cblist
                cbList = cbList == null ? Collections.EMPTY_LIST : cbList;
                for (CbMethodHandle method : cbList) {
                    //does cb support dirty
                    DirtyFlag dirtyFlagForUpdateCb = model.getDirtyFlagForUpdateCb(method);
                    String dirtyAssignment = "";
                    if (dirtyFlagForUpdateCb != null) {
                        dirtyAssignment = dirtyFlagForUpdateCb.name + " = ";
                    }
                    //protect with guards
                    Collection<DirtyFlag> nodeGuardConditions = model.getNodeGuardConditions(method);
                    String OR = "";
                    if (nodeGuardConditions.size() > 0) {
                        switchFilter += String.format("%24sif(", "");
                        for (DirtyFlag nodeGuardCondition : nodeGuardConditions) {
                            switchFilter += OR + nodeGuardCondition.name;
                            OR = " || ";
                        }
                        switchFilter += ") {\n";
                    }

                    //assign return if appropriate
                    if (isDebug) {
                        switchFilter += String.format("%24sdebugger.nodeInvocation(%s, \"%s\");%n", "", method.variableName, method.variableName);
                    }
                    if (method.parameterClass == null) {
                        switchFilter += String.format("%24s%s%s.%s();\n", "", dirtyAssignment, method.variableName, method.method.getName());
                    } else {
                        switchFilter += String.format("%24s%s%s.%s(typedEvent);%n", "", dirtyAssignment, method.variableName, method.method.getName());
                    }
                    //child callbacks - listening to an individual parent change
                    //if guards are in operation for the parent node, conditionally invoke only on a change
                    final Map<Object, List<CbMethodHandle>> listenerMethodMap = model.getParentUpdateListenerMethodMap();
                    Object parent = method.instance;
                    String parentVar = method.variableName;

                    //guard
                    DirtyFlag parentFlag = model.getDirtyFieldMap().get(model.getFieldForInstance(parent));
                    List<CbMethodHandle> updateListenerCbList = listenerMethodMap.get(parent);
                    if (parentFlag != null && updateListenerCbList.size() > 0) {
                        switchFilter += String.format("%20sif(%s) {\n", "", parentFlag.name);
                    }
                    //child callbacks
                    for (CbMethodHandle cbMethod : updateListenerCbList) {
                        switchFilter += String.format("%24s%s.%s(%s);%n", "",
                                cbMethod.variableName, cbMethod.method.getName(), parentVar);
                    }
                    if (parentFlag != null && updateListenerCbList.size() > 0) {
                        switchFilter += String.format("%20s}\n", "", parentFlag.name);
                    }
                    //close guards clause
                    if (nodeGuardConditions.size() > 0) {
                        switchFilter += String.format("%16s}\n", "");
                    }
                }
                //chec for null on cbList and escape
                cbList = cbMapPostEvent.get(filterDescription);
                if (cbList == null || cbList.size() > 0) {
                    switchFilter += String.format("%16s//event stack unwind callbacks\n", "");
                }
                cbList = cbList == null ? Collections.EMPTY_LIST : cbList;
                for (CbMethodHandle method : cbList) {
                    //protect with guards
                    Collection<DirtyFlag> nodeGuardConditions = model.getNodeGuardConditions(method);
                    String OR = "";
                    if (nodeGuardConditions.size() > 0) {
                        switchFilter += String.format("%24sif(", "");
                        for (DirtyFlag nodeGuardCondition : nodeGuardConditions) {
                            switchFilter += OR + nodeGuardCondition.name;
                            OR = " || ";
                        }
                        switchFilter += ") {\n";
                    }

                    //assign return if appropriate
                    if (isDebug) {
                        switchFilter += String.format("%24sdebugger.nodeInvocation(%s, \"%s\");%n", "", method.variableName, method.variableName);
                    }
                    if (method.parameterClass == null) {
                        switchFilter += String.format("%24s%s%s.%s();\n", "", "", method.variableName, method.method.getName());
                    } else {
                        switchFilter += String.format("%24s%s%s.%s(typedEvent);%n", "", "", method.variableName, method.method.getName());
                    }
                    //close guards clause
                    if (nodeGuardConditions.size() > 0) {
                        switchFilter += String.format("%16s}\n", "");
                    }
                }
                if (!noFilter) {
                    switchFilter += String.format("%24sbreak;%n", "");
                }
            }
        }
        return switchFilter.length() == 0 ? null : switchFilter;
    }

    private String buildFiteredDispatch(Map<FilterDescription, List<CbMethodHandle>> cbMap,
            Map<FilterDescription, List<CbMethodHandle>> cbMapPostEvent,
            boolean isDebug, Class eventClass) {
        String dispatchString = "";
        String eventHandlerString = "";
        String intFilterSwitch = buildFilteredSwitch(cbMap, cbMapPostEvent, isDebug, true, false);
        String stringFilterSwitch = buildFilteredSwitch(cbMap, cbMapPostEvent, isDebug, false, false);
        String noFilterDispatch = buildFilteredSwitch(cbMap, cbMapPostEvent, isDebug, false, true);
        if (!isInlineEventHandling) {
            dispatchString += String.format("%16shandleEvent(typedEvent);%n", "");
            eventHandlerString += String.format("%n%4spublic void handleEvent(%s typedEvent) {%n", "", eventClass.getCanonicalName());
            //no filtered event dispatch
//                if(noFilterDispatch!=null){
//                    eventHandlerString += noFilterDispatch;
//                }
            //intFiltered
            if (intFilterSwitch != null) {
                eventHandlerString += String.format("%8sswitch (typedEvent.filterId()) {%n", "");
                eventHandlerString += intFilterSwitch;
                if (noFilterDispatch != null) {
                    eventHandlerString += String.format("//No filter match%n%12sdefault:%n%s%12sbreak;%n", "", noFilterDispatch, "");
                }
                eventHandlerString += String.format("%8s}%n", "");
            }
            //String filtered
            if (stringFilterSwitch != null) {
                eventHandlerString += String.format("%8sswitch (typedEvent.filterString()) {%n", "");
                eventHandlerString += stringFilterSwitch;
                if (noFilterDispatch != null) {
                    eventHandlerString += String.format("//No filter match%n%12sdefault:%n%s%12sbreak;%n", "", noFilterDispatch, "");
                }
                eventHandlerString += String.format("%8s}%n", "");
            }
            //TODO if both are null then 
            eventHandlerString += String.format("%8safterEvent();%n", "");
            eventHandlerString += String.format("%4s}%n", "");
        } else {
            //no filtered event dispatch
            if (noFilterDispatch != null) {
                dispatchString += noFilterDispatch;
            }
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
        for (Field field : nodeFields) {

            Object object = field.instance;
            String varName = field.name;
//            java.lang.reflect.Field[] fields = object.getClass().getDeclaredFields();
            java.lang.reflect.Field[] fields = object.getClass().getFields();
            nodeMemberAssignmentList.add(String.format("%4s//%s", "", varName));
            for (java.lang.reflect.Field instanceField : fields) {
                if ((instanceField.getModifiers() & (Modifier.STATIC | Modifier.TRANSIENT | Modifier.FINAL)) != 0) {
                    continue;
                }
                if ((instanceField.getModifiers() & (Modifier.PRIVATE | Modifier.PROTECTED)) != 0) {
                    continue;
                }
                instanceField.setAccessible(true);
                if (instanceField.getType().isArray()) {
                    //array fields
                    Object array = instanceField.get(object);
                    if (array == null) {
                        continue;
                    }
                    int length = Array.getLength(array);
                    final String className = instanceField.getType().getComponentType().getName();
                    nodeMemberAssignmentList.add(String.format("%4s%s.%s = new %s[%d];", "", varName, instanceField.getName(), className, length));
                    if (instanceField.getType().getComponentType().isPrimitive()) {
                        //primitive array
                        for (int i = 0; i < length; i++) {
                            Object value = Array.get(array, i).toString();
                            nodeMemberAssignmentList.add(String.format("%4s%s.%s[%d] = %s;", "", varName, instanceField.getName(), i, value));
                        }
                    } else {
                        //object array
                        for (int i = 0; i < length; i++) {
                            Object refField = Array.get(array, i);
                            Field nodeReference = model.getFieldForInstance(refField);
                            if (nodeReference != null) {
                                nodeMemberAssignmentList.add(String.format("%4s%s.%s[%d] = %s;", "", varName, instanceField.getName(), i, nodeReference.name));
                            }
                        }

                    }
                } else {
                    //scalar fields
                    Field nodeReference = model.getFieldForInstance(instanceField.get(object));
                    if (instanceField.getType().isPrimitive()) {
                        String value = instanceField.get(object).toString();
                        if (instanceField.getType().equals(Character.TYPE)) {
                            nodeMemberAssignmentList.add(String.format("%4s%s.%s = '%s';", "", varName, instanceField.getName(), value));
                        } else {
                            nodeMemberAssignmentList.add(String.format("%4s%s.%s = %s;", "", varName, instanceField.getName(), value));
                        }
                    } else if (instanceField.isEnumConstant()) {
                        String value = instanceField.get(object).toString();
                        nodeMemberAssignmentList.add(String.format("//ENUM %4s%s.%s = \"%s\";", "", varName, instanceField.getName(), value));
                    } else if (nodeReference != null) {
                        nodeMemberAssignmentList.add(String.format("%4s%s.%s = %s;", "", varName, instanceField.getName(), nodeReference.name));
                    } else if (instanceField.getType().equals(String.class)) {
                        String value = instanceField.get(object).toString();
                        nodeMemberAssignmentList.add(String.format("%4s%s.%s = \"%s\";", "", varName, instanceField.getName(), value));
                    } else if (Collection.class.isAssignableFrom(instanceField.getType())) {
                        Collection list = (Collection) instanceField.get(object);
                        if (list != null) {
                            for (Object parent : list) {
                                Field nodeParentReference = model.getFieldForInstance(parent);
                                String joiner = ".";
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

    /**
     * String representation of top level event dispatch
     *
     * <code> public void onEvent(Event event) </code>
     * <p>
     * <code>
     * <pre>
     * public void handleEvent([specific event] event) {
     *      [eventHandlers]
     * }
     * </pre>
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
     * {@linkplain  com.fluxtion.api.event.Event#filterId() filterID}. If
     * inlining is false the following output will be produced:
     * <p>
     * <code>
     * <pre>
     * public void handleEvent([specific event] event) {
     *      [eventHandlers]
     * }
     * </pre>
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

}
