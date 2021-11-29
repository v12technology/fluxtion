package com.fluxtion.generator.targets;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.event.Event;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.builder.generation.FilterDescription;
import com.fluxtion.generator.model.CbMethodHandle;
import com.fluxtion.generator.model.Field;
import com.fluxtion.generator.model.SimpleEventProcessorModel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
//TODO - partial implementation. Additionally many optimisations are missing. Caching the
public class InMemoryEventProcessor implements StaticEventProcessor, Lifecycle {

    private final SimpleEventProcessorModel simpleEventProcessorModel;
    private final BitSet dirtyBitset = new BitSet();
    private final List<Node> eventHandlers = new ArrayList<>();
    private final Map<Class<?>, List<Integer>> eventHandlerToBitsetMap = new HashMap<>();

    public void onEvent(Event event) {

    }

    @Override
    public void onEvent(Object event) {
        log.debug("dirtyBitset, before:{}", dirtyBitset);
        //find index and then dispatch using bitset
        eventHandlerToBitsetMap.getOrDefault(event.getClass(), Collections.emptyList()).forEach(dirtyBitset::set);
        //now actually dispatch
        log.debug("dirtyBitset, after:{}", dirtyBitset);
        log.debug("======== GRAPH CYCLE START EVENT:[{}] ========", event);
        for (int i = dirtyBitset.nextSetBit(0); i >= 0; i = dirtyBitset.nextSetBit(i + 1)) {
            log.debug("event dispatch bitset id[{}] handler[{}::{}]",
                    i,
                    eventHandlers.get(i).callbackHandle.getMethod().getDeclaringClass().getSimpleName(),
                    eventHandlers.get(i).callbackHandle.getMethod().getName()
            );
            eventHandlers.get(i).onEvent(event);
        }
        log.debug("======== GRAPH CYCLE END   EVENT:[{}] ========", event);
        dirtyBitset.clear();
        log.debug("dirtyBitset, afterClear:{}", dirtyBitset);
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
                    parentUpdateListenerMethodMap.getOrDefault(cbMethodHandle.instance, Collections.emptyList())
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
        //calculate event handler bitset id's for an event
        simpleEventProcessorModel.getDispatchMap()
                .forEach((key, value) ->
                        eventHandlerToBitsetMap.put(
                                key,
                                value.get(FilterDescription.DEFAULT_FILTER).stream()
                                        .filter(Objects::nonNull)
                                        .filter(CbMethodHandle::isEventHandler)
                                        .map(InMemoryEventProcessor.this::nodeIndex)
                                        .collect(Collectors.toList())
                        ));
        eventHandlers.forEach(Node::init);
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

        @Override
        @SneakyThrows
        public void onEvent(Object e) {
            final Object result;
            final boolean dirty;
            if (callbackHandle.isEventHandler) {
                result = callbackHandle.method.invoke(callbackHandle.instance, e);
            } else {
                result = callbackHandle.method.invoke(callbackHandle.instance);
            }
            dirty = result == null || ((Boolean) result);
            dependents.forEach(n -> n.parentUpdated(dirty));

            for (CbMethodHandle cb : parentListeners) {
                if (cb.isGuardedParent() & dirty || (!cb.isGuardedParent() & dirty)) {
                    cb.method.invoke(cb.instance, callbackHandle.instance);
                }
            }
        }

        private void addDependent(Node dependent) {
            log.debug("addDependent:{}", dependent);
            this.dependents.add(dependent);
        }

        private void parentUpdated(boolean parentDirty){
            if(callbackHandle.isInvertedDirtyHandler){
                parentDirty = !parentDirty;
            }
            if(parentDirty){
                log.debug("mark dirty:{}", callbackHandle.getVariableName());
                dirtyBitset.set(position);
            }else{
                log.debug("mark clean:{}", callbackHandle.getVariableName());
            }
        }

        @Override
        public void init() {
            dirtyBitset.clear(position);
            callbackHandle.method.setAccessible(true);
            parentListeners.forEach(cb -> cb.method.setAccessible(true));
        }

        @Override
        public void tearDown() {
            dirtyBitset.clear(position);
        }

        boolean sameInstance(Object other) {
            return Objects.equals(callbackHandle.instance, other);
        }

        boolean sameCallback(CbMethodHandle other) {
            return Objects.equals(callbackHandle, other);
        }

        public int getPosition() {
            return position;
        }
    }
}
