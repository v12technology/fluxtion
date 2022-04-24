package com.fluxtion.compiler.generation.targets;

import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.annotations.AfterTrigger;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.lifecycle.BatchHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.compiler.builder.generation.FilterDescription;
import com.fluxtion.compiler.generation.model.CbMethodHandle;
import com.fluxtion.compiler.generation.model.Field;
import com.fluxtion.compiler.generation.model.SimpleEventProcessorModel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reflections.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class InMemoryEventProcessor implements EventProcessor, StaticEventProcessor, Lifecycle, BatchHandler {

    private final SimpleEventProcessorModel simpleEventProcessorModel;
    private final BitSet dirtyBitset = new BitSet();
    private final List<Node> eventHandlers = new ArrayList<>();
    private final Map<Class<?>, List<Integer>> noFilterEventHandlerToBitsetMap = new HashMap<>();
    private final Map<FilterDescription, List<Integer>> filteredEventHandlerToBitsetMap = new HashMap<>();
    private final List<Auditor> auditors = new ArrayList<>();
    private Object currentEvent;

    @Override
    @SneakyThrows
    public void onEvent(Object event) {
        currentEvent = event;
        log.debug("dirtyBitset, before:{}", dirtyBitset);
        auditNewEvent(event);
        filteredEventHandlerToBitsetMap.getOrDefault(FilterDescription.build(event), Collections.emptyList()).forEach(dirtyBitset::set);
        if(dirtyBitset.isEmpty()){
            noFilterEventHandlerToBitsetMap.getOrDefault(event.getClass(), Collections.emptyList()).forEach(dirtyBitset::set);
        }

        //now actually dispatch
        log.debug("dirtyBitset, after:{}", dirtyBitset);
        log.debug("======== GRAPH CYCLE START EVENT:[{}] ========", event);
        log.debug("======== process event ========");
        for (int i = dirtyBitset.nextSetBit(0); i >= 0; i = dirtyBitset.nextSetBit(i + 1)) {
            log.debug("event dispatch bitset id[{}] handler[{}::{}]",
                    i,
                    eventHandlers.get(i).callbackHandle.getMethod().getDeclaringClass().getSimpleName(),
                    eventHandlers.get(i).callbackHandle.getMethod().getName()
            );
            eventHandlers.get(i).onEvent(event);
        }
        log.debug("======== eventComplete ========");
        for (int i = dirtyBitset.length(); (i = dirtyBitset.previousSetBit(i-1)) >= 0; ) {
                if(eventHandlers.get(i).willInvokeEventComplet()){
                log.debug("event dispatch bitset id[{}] handler[{}::{}]",
                        i,
                        eventHandlers.get(i).callbackHandle.getMethod().getDeclaringClass().getSimpleName(),
                        eventHandlers.get(i).onEventCompleteMethod.getName()
                );
            }
            eventHandlers.get(i).eventComplete();
        }
        log.debug("======== GRAPH CYCLE END   EVENT:[{}] ========", event);
        auditors.stream()
                .filter(a -> Auditor.FirstAfterEvent.class.isAssignableFrom(a.getClass()))
                .forEach(Auditor::processingComplete);
        log.debug("After event");
        simpleEventProcessorModel.getEventEndMethods().forEach(this::invokeRunnable);
        auditors.stream()
                .filter(a -> !Auditor.FirstAfterEvent.class.isAssignableFrom(a.getClass()))
                .forEach(Auditor::processingComplete);
        dirtyBitset.clear();
        log.debug("dirtyBitset, afterClear:{}", dirtyBitset);
        currentEvent = null;
    }

    private void auditNewEvent(Object event){
            if(Event.class.isAssignableFrom(event.getClass())){
                auditors.stream()
                        .filter(a -> Auditor.FirstAfterEvent.class.isAssignableFrom(a.getClass()))
                        .forEach(a -> a.eventReceived((Event) event));
                auditors.stream()
                        .filter(a -> !Auditor.FirstAfterEvent.class.isAssignableFrom(a.getClass()))
                        .forEach(a -> a.eventReceived((Event) event));
            }else{
                auditors.stream()
                        .filter(a -> Auditor.FirstAfterEvent.class.isAssignableFrom(a.getClass()))
                        .forEach(a -> a.eventReceived( event));
                auditors.stream()
                        .filter(a -> !Auditor.FirstAfterEvent.class.isAssignableFrom(a.getClass()))
                        .forEach(a -> a.eventReceived( event));
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
    public void init() {
        buildDispatch();
        simpleEventProcessorModel.getInitialiseMethods().forEach(this::invokeRunnable);
    }

    @Override
    public void tearDown() {
        simpleEventProcessorModel.getTearDownMethods().forEach(this::invokeRunnable);
    }

    @SneakyThrows
    private void invokeRunnable(CbMethodHandle callBackHandle) {
        callBackHandle.method.setAccessible(true);
        if(currentEvent!=null) {
            auditors.stream()
                    .filter(Auditor::auditInvocations)
                    .forEachOrdered(a -> a.nodeInvoked(
                            callBackHandle.instance, callBackHandle.getVariableName(), callBackHandle.getMethod().getName(), currentEvent
                    ));
        }

        callBackHandle.method.invoke(callBackHandle.instance);
    }

    public Field getFieldByName(String name) {
        return simpleEventProcessorModel.getFieldForName(name);
    }

    /**
     * Implementation notes:
     * <ul>
     *     <li>A node class that encapsulates call back information</li>
     *     <li>An array of nodes sorted in topological order</li>
     *     <li>A Bitset that holds the dirty state of nodes during an event cycle</li>
     * </ul>
     */
    private void buildDispatch() {
        List<CbMethodHandle> dispatchMapForGraph = new ArrayList<>(simpleEventProcessorModel.getDispatchMapForGraph());
        if (log.isDebugEnabled()) {
            log.debug("======== callbacks for graph =============");
            dispatchMapForGraph.forEach(cb -> log.debug("{}::{}",
                    cb.getMethod().getDeclaringClass().getSimpleName(),
                    cb.getMethod().getName())
            );
            log.debug("======== callbacks for graph =============");
        }
        dirtyBitset.clear(dispatchMapForGraph.size());
        dirtyBitset.clear();

        //set up array of all callback methods
        final Map<Object, List<CbMethodHandle>> parentUpdateListenerMethodMap = simpleEventProcessorModel.getParentUpdateListenerMethodMap();
        final Map<Object, Node> instance2NodeMap = new HashMap<>(dispatchMapForGraph.size());
        for (int i = 0; i < dispatchMapForGraph.size(); i++) {
            CbMethodHandle cbMethodHandle = dispatchMapForGraph.get(i);
            Node node = new Node(
                    i,
                    cbMethodHandle,
                    new ArrayList<>(parentUpdateListenerMethodMap.getOrDefault(cbMethodHandle.instance, Collections.emptyList()))
            );
            eventHandlers.add(node);
            instance2NodeMap.put(node.callbackHandle.instance, node);
        }
        //allocate OnEvent dependents
        for (Node handler : eventHandlers) {
            simpleEventProcessorModel.getOnEventDependenciesForNode(handler.getCallbackHandle())
                    .stream()
                    .peek(o -> log.debug("child dependency:{}", o))
                    .map(instance2NodeMap::get)
                    .filter(Objects::nonNull)
                    .forEach(handler::addDependent);
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

    private void registerAuditors(){
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
    }

    private int nodeIndex(CbMethodHandle nodeInstance) {
        return eventHandlers.stream()
                .filter(n -> n.sameCallback(nodeInstance))
                .findFirst()
                .map(Node::getPosition).orElse(-1);
    }

    @Data
    private class Node implements StaticEventProcessor, Lifecycle {

        final int position;
        final CbMethodHandle callbackHandle;
        final List<CbMethodHandle> parentListeners;
        final List<Node> dependents = new ArrayList<>();
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
                if ((cb.isGuardedParent() & dirty) || !cb.isGuardedParent() ) {
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

        public boolean willInvokeEventComplet(){
            return dirty && onEventCompleteMethod!=null;
        }
        @SneakyThrows
        public void eventComplete(){
            if(willInvokeEventComplet()){
                auditors.stream()
                        .filter(Auditor::auditInvocations)
                        .forEachOrdered(a -> a.nodeInvoked(
                                callbackHandle.instance, callbackHandle.getVariableName(), onEventCompleteMethod.getName(), currentEvent
                        ));
                onEventCompleteMethod.invoke(callbackHandle.getInstance());
            }
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

        @Override
        @SuppressWarnings("unchecked")
        public void init() {
            dirtyBitset.clear(position);
            dirty = false;
            callbackHandle.method.setAccessible(true);
            if(callbackHandle.isNoPropagateEventHandler()){
                parentListeners.clear();
            }else{
                parentListeners.forEach(cb -> cb.method.setAccessible(true));
            }
            onEventCompleteMethod = ReflectionUtils.getAllMethods(
                    callbackHandle.getInstance().getClass(),
                    ReflectionUtils.withAnnotation(AfterTrigger.class)
            ).stream().findFirst().orElse(null);
            if(onEventCompleteMethod!=null){
                onEventCompleteMethod.setAccessible(true);
            }
        }

        private void deDuplicateOnEventComplete(Set<Object> onCompleteCallbackSet){
            if(onCompleteCallbackSet.contains(callbackHandle.getInstance())){
                onEventCompleteMethod = null;
            }else{
                onCompleteCallbackSet.add(callbackHandle.getInstance());
            }

        }

        @Override
        public void tearDown() {
            dirtyBitset.clear(position);
        }

        boolean sameCallback(CbMethodHandle other) {
            return Objects.equals(callbackHandle, other);
        }

        public int getPosition() {
            return position;
        }
    }
}
