package com.fluxtion.compiler.generation.targets;

import com.fluxtion.compiler.builder.filter.FilterDescription;
import com.fluxtion.compiler.generation.model.CbMethodHandle;
import com.fluxtion.compiler.generation.model.Field;
import com.fluxtion.compiler.generation.model.SimpleEventProcessorModel;
import com.fluxtion.compiler.generation.util.ClassUtils;
import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.EventProcessorContext;
import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.annotations.AfterTrigger;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.callback.CallbackDispatcher;
import com.fluxtion.runtime.callback.EventProcessorCallbackInternal;
import com.fluxtion.runtime.callback.InternalEventProcessor;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.input.EventFeed;
import com.fluxtion.runtime.input.SubscriptionManager;
import com.fluxtion.runtime.input.SubscriptionManagerNode;
import com.fluxtion.runtime.lifecycle.BatchHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.runtime.node.ForkedTriggerTask;
import com.fluxtion.runtime.node.MutableEventProcessorContext;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reflections.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

@Slf4j
public class InMemoryEventProcessor implements EventProcessor, StaticEventProcessor, InternalEventProcessor, Lifecycle, BatchHandler {

    private final SimpleEventProcessorModel simpleEventProcessorModel;
    private final MutableEventProcessorContext context;
    private final EventProcessorCallbackInternal callbackDispatcher;
    private final SubscriptionManagerNode subscriptionManager;
    private final BitSet dirtyBitset = new BitSet();
    private final BitSet eventOnlyBitset = new BitSet();
    private final BitSet postProcessBufferingBitset = new BitSet();
    private final BitSet forkedTaskBitset = new BitSet();
    private final List<Node> eventHandlers = new ArrayList<>();
    private final List<ForkTriggerTaskNode> forkTriggerTaskNodes = new ArrayList<>();
    private final Map<Class<?>, List<Integer>> noFilterEventHandlerToBitsetMap = new HashMap<>();
    private final Map<FilterDescription, List<Integer>> filteredEventHandlerToBitsetMap = new HashMap<>();
    private final List<Auditor> auditors = new ArrayList<>();
    private boolean buffering = false;
    private Object currentEvent;
    private boolean processing = false;
    private boolean isDefaultHandling;
    private boolean initCalled = false;

    public InMemoryEventProcessor(SimpleEventProcessorModel simpleEventProcessorModel) {
        this.simpleEventProcessorModel = simpleEventProcessorModel;
        try {
            context = getNodeById(EventProcessorContext.DEFAULT_NODE_NAME);
            callbackDispatcher = getNodeById(CallbackDispatcher.DEFAULT_NODE_NAME);
            subscriptionManager = getNodeById(SubscriptionManager.DEFAULT_NODE_NAME);
            subscriptionManager.setSubscribingEventProcessor(this);
            context.setEventProcessorCallback(this);
        } catch (Exception e) {
            throw new RuntimeException("cannot build InMemoryEventProcessor", e);
        }
    }

    @Override
    @SneakyThrows
    public void onEvent(Object event) {
        if (buffering) {
            triggerCalculation();
        }
        if (processing) {
            callbackDispatcher.processReentrantEvent(event);
        } else {
            processing = true;
            onEventInternal(event);
            callbackDispatcher.dispatchQueuedCallbacks();
            processing = false;
        }
    }

    @Override
    public void bufferEvent(Object event) {
        buffering = true;
        processEvent(event, true);
    }

    @Override
    public void triggerCalculation() {
        log.debug("dirtyBitset, after:{}", dirtyBitset);
        log.debug("======== GRAPH CYCLE START BUFFER EVENT ========");
        log.debug("======== process event ========");
        for (int i = dirtyBitset.nextSetBit(0); i >= 0; i = dirtyBitset.nextSetBit(i + 1)) {
            log.debug("event dispatch bitset index[{}] handler[{}::{}]",
                    i,
                    eventHandlers.get(i).callbackHandle.getVariableName(),
                    eventHandlers.get(i).callbackHandle.getMethod().getName()
            );
            eventHandlers.get(i).onEvent(null);
        }
        dirtyBitset.clear();
        dirtyBitset.or(postProcessBufferingBitset);
        postProcessBufferingBitset.clear();
        postEventProcessing();
        buffering = false;
    }

    public void postEventProcessing() {
        log.debug("======== eventComplete ========");
        for (int i = dirtyBitset.length(); (i = dirtyBitset.previousSetBit(i - 1)) >= 0; ) {
            log.debug("check for postprocessing index[{}]", i);
            if (eventHandlers.get(i).willInvokeEventComplete()) {
                log.debug("event dispatch bitset index[{}] handler[{}::{}]",
                        i,
                        eventHandlers.get(i).callbackHandle.getVariableName(),
                        eventHandlers.get(i).onEventCompleteMethod.getName()
                );
            }
            eventHandlers.get(i).eventComplete();
        }
        log.debug("======== GRAPH CYCLE END   EVENT:[{}] ========", currentEvent);
        auditors.stream()
                .filter(a -> Auditor.FirstAfterEvent.class.isAssignableFrom(a.getClass()))
                .forEach(Auditor::processingComplete);
        log.debug("After event");
        //call reinitialise for each element
        forkTriggerTaskNodes.forEach(ForkTriggerTaskNode::reinitialise);
        simpleEventProcessorModel.getEventEndMethods().forEach(this::invokeRunnable);
        auditors.stream()
                .filter(a -> !Auditor.FirstAfterEvent.class.isAssignableFrom(a.getClass()))
                .forEach(Auditor::processingComplete);
        dirtyBitset.clear();
        log.debug("dirtyBitset, afterClear:{}", dirtyBitset);
        forkedTaskBitset.clear();
        log.debug("forkedTaskBitset, afterClear:{}", dirtyBitset);
        currentEvent = null;
    }

    private void subclassDispatchSearch(BitSet updateBitset) {
        if (updateBitset.isEmpty()) {
            Set<Class<?>> eventClassSet = new HashSet<>();
            filteredEventHandlerToBitsetMap.keySet().stream().map(FilterDescription::getEventClass).forEach(eventClassSet::add);
            eventClassSet.addAll(noFilterEventHandlerToBitsetMap.keySet());
            List<Class<?>> sortedClasses = ClassUtils.sortClassHierarchy(eventClassSet);
            sortedClasses.stream()
                    .filter(c -> c.isInstance(currentEvent))
                    .findFirst()
                    .ifPresent(c -> {
                        if (c.isAssignableFrom(Event.class)) {
                            FilterDescription filterDescription = FilterDescription.build(currentEvent);
                            filterDescription.setEventClass((Class<? extends Event>) c);
                            filteredEventHandlerToBitsetMap.getOrDefault(filterDescription, Collections.emptyList()).forEach(updateBitset::set);
                        }
                        if (updateBitset.isEmpty()) {
                            noFilterEventHandlerToBitsetMap.getOrDefault(c, Collections.emptyList()).forEach(updateBitset::set);
                        }
                    });
        }
    }

    private void processEvent(Object event, boolean buffer) {
        currentEvent = event;
        Object defaultEvent = checkForDefaultEventHandling(event);
        log.debug("dirtyBitset, before:{}", dirtyBitset);
        auditNewEvent(event);
        final BitSet updateBitset = buffer ? eventOnlyBitset : dirtyBitset;
        filteredEventHandlerToBitsetMap.getOrDefault(FilterDescription.build(defaultEvent), Collections.emptyList()).forEach(updateBitset::set);
        if (updateBitset.isEmpty()) {
            noFilterEventHandlerToBitsetMap.getOrDefault(defaultEvent.getClass(), Collections.emptyList()).forEach(updateBitset::set);
        }
        //find a potential sub class if empty
        subclassDispatchSearch(updateBitset);
        postProcessBufferingBitset.or(updateBitset);
        //now actually dispatch
        log.debug("dirtyBitset, after:{}", updateBitset);
        log.debug("======== GRAPH CYCLE START EVENT:[{}] ========", event);
        log.debug("======== process event ========");
        for (int i = checkForForkedTask(-1, updateBitset); i >= 0; i = checkForForkedTask(i, updateBitset)) {
            log.debug("event dispatch bitset index[{}] handler[{}::{}]",
                    i,
                    eventHandlers.get(i).callbackHandle.getVariableName(),
                    eventHandlers.get(i).callbackHandle.getMethod().getName()
            );
            eventHandlers.get(i).onEvent(event);
        }
        postProcessBufferingBitset.or(updateBitset);
        if (!buffer) {
            dirtyBitset.or(updateBitset);
            log.debug("dirtyBitset, postProcessing:{}", dirtyBitset);
            postEventProcessing();
        }
        updateBitset.clear();
    }

    private int checkForForkedTask(int startIndex, BitSet updateBitset) {
        log.debug("checkForForkedTask startIndex:{}, updateBitset:{} forkedBitset:{}", startIndex, updateBitset, forkedTaskBitset);
        final int nextDirtyIndex = updateBitset.nextSetBit(startIndex + 1);
        final int endIndex = Math.max(nextDirtyIndex, updateBitset.length());
        final int nextForkIndex = forkedTaskBitset.nextSetBit(startIndex + 1);
        log.debug("checkForForkedTask endIndex:{}, nextDirtyIndex:{} nextForkIndex:{}", endIndex, nextDirtyIndex, nextForkIndex);
        if ((nextForkIndex > 0) & ((nextForkIndex <= nextDirtyIndex) | (nextDirtyIndex < 0))) {
            log.debug("joining on parent forked task bitset index[{}] handler[{}::{}]",
                    nextForkIndex,
                    eventHandlers.get(nextForkIndex).callbackHandle.getMethod().getDeclaringClass().getSimpleName(),
                    eventHandlers.get(nextForkIndex).callbackHandle.getMethod().getName()
            );
            eventHandlers.get(nextForkIndex).joinOnParentTask();
            updateBitset.set(nextForkIndex);
            return nextForkIndex;
        }
        return nextDirtyIndex;
    }

    @Override
    public void onEventInternal(Object event) {
        processEvent(event, false);
    }

    @Override
    public boolean isDirty(Object node) {
        return dirtySupplier(node).getAsBoolean();
    }

    @Override
    public BooleanSupplier dirtySupplier(Object node) {
        //TODO replace with map
        return eventHandlers.stream()
                .filter(n -> n.getCallbackHandle().getInstance() == node)
                .map(n -> (BooleanSupplier) n::isDirty)
                .findFirst()
                .orElse(() -> false);
    }

    @Override
    public void setDirty(Object node, boolean dirtyFlag) {
        if (dirtyFlag) {
            eventHandlers.stream()
                    .filter(n -> n.getCallbackHandle().getInstance() == node)
                    .forEach(Node::markDirty);
        }
    }

    private Object checkForDefaultEventHandling(Object event) {
        Object mapped = event;
        if (isDefaultHandling && !simpleEventProcessorModel.getDispatchMap().containsKey(event.getClass())) {
            mapped = new Object();
        }
        return mapped;
    }

    private void auditNewEvent(Object event) {
        if (Event.class.isAssignableFrom(event.getClass())) {
            auditors.stream()
                    .filter(a -> Auditor.FirstAfterEvent.class.isAssignableFrom(a.getClass()))
                    .forEach(a -> a.eventReceived((Event) event));
            auditors.stream()
                    .filter(a -> !Auditor.FirstAfterEvent.class.isAssignableFrom(a.getClass()))
                    .forEach(a -> a.eventReceived((Event) event));
        } else {
            auditors.stream()
                    .filter(a -> Auditor.FirstAfterEvent.class.isAssignableFrom(a.getClass()))
                    .forEach(a -> a.eventReceived(event));
            auditors.stream()
                    .filter(a -> !Auditor.FirstAfterEvent.class.isAssignableFrom(a.getClass()))
                    .forEach(a -> a.eventReceived(event));
        }
    }

    @Override
    public void batchPause() {
        simpleEventProcessorModel.getBatchPauseMethods().forEach(this::invokeRunnable);
    }

    @Override
    public void batchEnd() {
        simpleEventProcessorModel.getBatchEndMethods().forEach(this::invokeRunnable);
    }

    @Override
    public void setContextParameterMap(Map<Object, Object> newContextMapping) {
        context.replaceMappings(newContextMapping);
    }

    public void addContextParameter(Object key, Object value) {
        context.addMapping(key, value);
    }

    @Override
    public void addEventFeed(EventFeed eventProcessorFeed) {
        subscriptionManager.addEventProcessorFeed(eventProcessorFeed);
    }

    @Override
    public void removeEventFeed(EventFeed eventProcessorFeed) {
        subscriptionManager.removeEventProcessorFeed(eventProcessorFeed);
    }

    @Override
    public void init() {
        initCalled = true;
        buildDispatch();
        simpleEventProcessorModel.getInitialiseMethods().forEach(this::invokeRunnable);
    }

    @Override
    public void start() {
        if (!initCalled) {
            throw new RuntimeException("init() must be called before start()");
        }
        simpleEventProcessorModel.getStartMethods().forEach(this::invokeRunnable);
    }

    @Override
    public void stop() {
        if (!initCalled) {
            throw new RuntimeException("init() must be called before start()");
        }
        simpleEventProcessorModel.getStopMethods().forEach(this::invokeRunnable);
    }

    @Override
    public void tearDown() {
        initCalled = false;
        simpleEventProcessorModel.getTearDownMethods().forEach(this::invokeRunnable);
    }


    private void invokeRunnable(CbMethodHandle callBackHandle) {
        callBackHandle.method.setAccessible(true);
        if (currentEvent != null) {
            auditors.stream()
                    .filter(Auditor::auditInvocations)
                    .forEachOrdered(a -> a.nodeInvoked(
                            callBackHandle.instance, callBackHandle.getVariableName(), callBackHandle.getMethod().getName(), currentEvent
                    ));
        }
        try {
            callBackHandle.method.invoke(callBackHandle.instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Field getFieldByName(String name) {
        return simpleEventProcessorModel.getFieldForName(name);
    }

    @Override
    public <T> T getNodeById(String id) throws NoSuchFieldException {
        Field fieldByName = getFieldByName(id);
        if (fieldByName == null) {
            throw new NoSuchFieldException(id);
        }
        return (T) fieldByName.instance;
    }

    /**
     * Implementation notes:
     * <ul>
     *     <li>A node class that encapsulates call back information</li>
     *     <li>An array of nodes sorted in topological order</li>
     *     <li>A Bitset that holds the dirty state of nodes during an event cycle</li>
     * </ul>
     */
    @SneakyThrows
    private void buildDispatch() {
        isDefaultHandling = simpleEventProcessorModel.getDispatchMap().containsKey(Object.class);
        List<CbMethodHandle> dispatchMapForGraph = new ArrayList<>(simpleEventProcessorModel.getDispatchMapForGraph());
        AtomicInteger counter = new AtomicInteger();
        if (log.isDebugEnabled()) {
            log.debug("======== callbacks for graph =============");
            dispatchMapForGraph.forEach(cb -> log.debug("index:{} [type:{}] {}::{}",
                    counter.getAndIncrement(),
                    cb.getMethod().getDeclaringClass().getSimpleName(),
                    cb.getVariableName(),
                    cb.getMethod().getName())
            );
            log.debug("======== callbacks for graph =============");
        }
        dirtyBitset.clear(dispatchMapForGraph.size());
        dirtyBitset.clear();
        eventOnlyBitset.clear(dispatchMapForGraph.size());
        eventOnlyBitset.or(dirtyBitset);
        forkedTaskBitset.clear(dispatchMapForGraph.size());
        forkedTaskBitset.clear();

        //set up array of all callback methods
        final Map<Object, List<CbMethodHandle>> parentUpdateListenerMethodMap = simpleEventProcessorModel.getParentUpdateListenerMethodMap();
        final Map<Object, Node> instance2NodeMap = new HashMap<>(dispatchMapForGraph.size());
        for (int i = 0; i < dispatchMapForGraph.size(); i++) {
            CbMethodHandle cbMethodHandle = dispatchMapForGraph.get(i);
            Node node;
            if (cbMethodHandle.isForkExecution()) {
                log.debug("Wrap with ForkJoinTask -> {}:{}",
                        cbMethodHandle.getVariableName(),
                        cbMethodHandle.getMethod().getName());
                node = new ForkTriggerTaskNode(
                        i,
                        cbMethodHandle,
                        new ArrayList<>(parentUpdateListenerMethodMap.getOrDefault(cbMethodHandle.instance, Collections.emptyList()))
                );
            } else {
                node = new Node(
                        i,
                        cbMethodHandle,
                        new ArrayList<>(parentUpdateListenerMethodMap.getOrDefault(cbMethodHandle.instance, Collections.emptyList()))
                );
            }
            eventHandlers.add(node);
            instance2NodeMap.put(node.callbackHandle.instance, node);
        }
        //allocate OnEvent dependents
        for (Node handler : eventHandlers) {
            simpleEventProcessorModel.getOnTriggerDependenciesForNode(handler.getCallbackHandle())
                    .stream()
                    .peek(o -> log.debug("child dependency:{}", o))
                    .map(instance2NodeMap::get)
                    .filter(Objects::nonNull)
                    .forEach(handler::addDependent);
        }
        //allocate ForkJoinNode dependencies
        for (Node handler : eventHandlers) {
            if (handler instanceof ForkTriggerTaskNode) {
                ForkTriggerTaskNode forkedTriggerTask = (ForkTriggerTaskNode) handler;
                forkTriggerTaskNodes.add(forkedTriggerTask);
                simpleEventProcessorModel.getOnTriggerDependenciesForNode(handler.getCallbackHandle())
                        .stream()
                        .peek(o -> log.debug("child dependency:{}", o))
                        .map(instance2NodeMap::get)
                        .filter(Objects::nonNull)
                        .forEach(node -> {
                            node.forkJoinParents.add(forkedTriggerTask);
                        });
            }
        }
        //calculate event handler bitset id's for an event with filtering
        simpleEventProcessorModel.getDispatchMap().forEach((eventClass, filterDescriptionListMap) ->
                filterDescriptionListMap.forEach((filterDescription, cbMethodHandles) -> {
                            if (!filterDescription.equals(FilterDescription.NO_FILTER)
                                    && !filterDescription.equals(FilterDescription.DEFAULT_FILTER)
                                    && !filterDescription.equals(FilterDescription.INVERSE_FILTER)
                            ) {
                                filteredEventHandlerToBitsetMap.put(
                                        filterDescription,
                                        cbMethodHandles.stream()
                                                .filter(CbMethodHandle::isEventHandler)
                                                .map(InMemoryEventProcessor.this::nodeIndex)
                                                .collect(Collectors.toList())
                                );
                            }
                        }
                )
        );
        //calculate event handler bitset id's for an event without filtering
        simpleEventProcessorModel.getDispatchMap().forEach((key, value) ->
                noFilterEventHandlerToBitsetMap.put(
                        key,
                        value.getOrDefault(FilterDescription.DEFAULT_FILTER, Collections.emptyList()).stream()
                                .filter(Objects::nonNull)
                                .filter(CbMethodHandle::isEventHandler)
                                .map(InMemoryEventProcessor.this::nodeIndex)
                                .collect(Collectors.toList())
                )
        );
        eventHandlers.forEach(Node::init);
        Set<Object> duplicatesOnEventComplete = new HashSet<>();
        eventHandlers.forEach(n -> n.deDuplicateOnEventComplete(duplicatesOnEventComplete));
        registerAuditors();
    }

    private void registerAuditors() {
        //register auditors
        List<Object> auditorFields = simpleEventProcessorModel.getNodeRegistrationListenerFields().stream()
                .map(Field::getInstance).collect(Collectors.toList());
        auditors.clear();
        auditors.addAll(simpleEventProcessorModel.getNodeRegistrationListenerFields().stream()
                .map(Field::getInstance)
                .map(Auditor.class::cast)
                .collect(Collectors.toList())
        );
        auditors.forEach(Auditor::init);
        simpleEventProcessorModel.getNodeFields().stream()
                .filter(f -> !auditorFields.contains(f.getInstance()))
                .forEach(f -> auditors.forEach(a -> a.nodeRegistered(f.getInstance(), f.getName())));
        forkTriggerTaskNodes.stream()
                .forEach(f -> auditors.forEach(a -> a.nodeRegistered(f.forkedTriggerTask, f.callbackHandle.forkVariableName())));
    }

    private int nodeIndex(CbMethodHandle nodeInstance) {
        return eventHandlers.stream()
                .filter(n -> n.sameCallback(nodeInstance))
                .findFirst()
                .map(Node::getPosition).orElse(-1);
    }

    public class ForkTriggerTaskNode extends Node {
        private final ForkedTriggerTask forkedTriggerTask;
        private Object triggerEvent;

        public ForkTriggerTaskNode(int position, CbMethodHandle callbackHandle, List<CbMethodHandle> parentListeners) {
            super(position, callbackHandle, parentListeners);
            forkedTriggerTask = new ForkedTriggerTask(
                    () -> {
                        try {
                            return (Boolean) callbackHandle.method.invoke(callbackHandle.instance);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    callbackHandle.variableName
            );
        }

        @Override
        @SneakyThrows
        public void onEvent(Object e) {
            dependents.forEach(Node::markJoinOnParentTask);
            triggerEvent = e;
            dirty = false;
            auditors.stream()
                    .filter(Auditor::auditInvocations)
                    .forEachOrdered(a -> a.nodeInvoked(
                            forkedTriggerTask, callbackHandle.getVariableName(), callbackHandle.getMethod().getName(), e
                    ));
            log.debug("forking task:{}", callbackHandle.getVariableName());
            forkedTriggerTask.onTrigger();
        }

        public boolean willInvokeEventComplete() {
            afterEvent();
            return dirty && onEventCompleteMethod != null;
        }

        @SneakyThrows
        public void afterEvent() {
            if (triggerEvent != null) {
                log.debug("afterEvent processing:{}", callbackHandle.getVariableName());
                dirty = forkedTriggerTask.afterEvent();
                dependents.forEach(n -> n.parentUpdated(dirty));
                for (CbMethodHandle cb : parentListeners) {
                    if ((cb.isGuardedParent() & dirty) || !cb.isGuardedParent()) {
                        //audit
                        auditors.stream()
                                .filter(Auditor::auditInvocations)
                                .forEachOrdered(a -> a.nodeInvoked(
                                        cb.instance, cb.getVariableName(), cb.getMethod().getName(), triggerEvent
                                ));
                        cb.method.invoke(cb.instance, callbackHandle.instance);
                    }
                }
            } else {
                log.debug("afterEvent NOT processing:{}", callbackHandle.getVariableName());
            }
            triggerEvent = null;
        }

        public void reinitialise() {
            log.debug("reinitialise:{}", callbackHandle.getVariableName());
            forkedTriggerTask.reinitialize();
        }
    }

    @Data
    private class Node implements StaticEventProcessor, Lifecycle {

        final int position;
        final CbMethodHandle callbackHandle;
        final List<CbMethodHandle> parentListeners;
        final List<Node> dependents = new ArrayList<>();
        final List<ForkTriggerTaskNode> forkJoinParents = new ArrayList<>();
        Method onEventCompleteMethod;
        boolean dirty;

        @Override
        @SneakyThrows
        public void onEvent(Object e) {
            final Object result;
            dirty = false;
            auditors.stream()
                    .filter(Auditor::auditInvocations)
                    .forEachOrdered(a -> a.nodeInvoked(
                            callbackHandle.instance, callbackHandle.getVariableName(), callbackHandle.getMethod().getName(), e
                    ));

            if (callbackHandle.isEventHandler) {
                result = callbackHandle.method.invoke(callbackHandle.instance, e);
            } else {
                result = callbackHandle.method.invoke(callbackHandle.instance);
            }
            dirty = result == null || ((Boolean) result);
            dependents.forEach(n -> n.parentUpdated(dirty));

            for (CbMethodHandle cb : parentListeners) {
                if ((cb.isGuardedParent() & dirty) || !cb.isGuardedParent()) {
                    //audit
                    auditors.stream()
                            .filter(Auditor::auditInvocations)
                            .forEachOrdered(a -> a.nodeInvoked(
                                    cb.instance, cb.getVariableName(), cb.getMethod().getName(), e
                            ));
                    cb.method.invoke(cb.instance, callbackHandle.instance);
                }
            }
        }

        private void markDirty() {
            dirty = true;
            dependents.forEach(n -> n.parentUpdated(dirty));
            //TODO - decide whether to callback into the parentupdated listener - must be consistent with the generated version
        }

        public boolean willInvokeEventComplete() {
            return dirty && onEventCompleteMethod != null;
        }

        @SneakyThrows
        public void eventComplete() {
            if (willInvokeEventComplete()) {
                auditors.stream()
                        .filter(Auditor::auditInvocations)
                        .forEachOrdered(a -> a.nodeInvoked(
                                callbackHandle.instance, callbackHandle.getVariableName(), onEventCompleteMethod.getName(), currentEvent
                        ));
                onEventCompleteMethod.invoke(callbackHandle.getInstance());
            }
            dirty = false;
        }

        private void addDependent(Node dependent) {
            log.debug("addDependent:{}", dependent);
            this.dependents.add(dependent);
        }

        private void parentUpdated(boolean parentDirty) {
            if (callbackHandle.isInvertedDirtyHandler) {
                parentDirty = !parentDirty;
            }
            if (parentDirty) {
                log.debug("mark dirty:{}", callbackHandle.getVariableName());
                dirtyBitset.set(position);
            } else {
                log.debug("mark clean:{}", callbackHandle.getVariableName());
            }
        }

        protected void markJoinOnParentTask() {
            //mark this node in a bit set as join on a parent task
            forkedTaskBitset.set(position);
        }

        protected void joinOnParentTask() {
            forkJoinParents.forEach(ForkTriggerTaskNode::afterEvent);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void init() {
            dirtyBitset.clear(position);
            dirty = false;
            callbackHandle.method.setAccessible(true);
            if (callbackHandle.isNoPropagateEventHandler()) {
                parentListeners.clear();
            } else {
                parentListeners.forEach(cb -> cb.method.setAccessible(true));
            }
            onEventCompleteMethod = ReflectionUtils.getAllMethods(
                    callbackHandle.getInstance().getClass(),
                    ReflectionUtils.withAnnotation(AfterTrigger.class)
            ).stream().findFirst().orElse(null);
            if (onEventCompleteMethod != null) {
                onEventCompleteMethod.setAccessible(true);
            }
        }

        private void deDuplicateOnEventComplete(Set<Object> onCompleteCallbackSet) {
            if (onCompleteCallbackSet.contains(callbackHandle.getInstance())) {
                onEventCompleteMethod = null;
            } else {
                onCompleteCallbackSet.add(callbackHandle.getInstance());
            }

        }

        @Override
        public void tearDown() {
            dirtyBitset.clear(position);
            processing = false;
        }

        boolean sameCallback(CbMethodHandle other) {
            return Objects.equals(callbackHandle, other);
        }

        public int getPosition() {
            return position;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "callbackHandle=" + callbackHandle +
                    '}';
        }
    }
}
