package com.fluxtion.generator.targets;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.event.Event;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.builder.generation.FilterDescription;
import com.fluxtion.generator.model.CbMethodHandle;
import com.fluxtion.generator.model.Field;
import com.fluxtion.generator.model.SimpleEventProcessorModel;
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

    public void onEvent(Event event){

    }

    @Override
    public void onEvent(Object event) {
        //find index and then dispatch using bitset
        simpleEventProcessorModel.getDispatchMap()
                .get(event.getClass())
                .getOrDefault(FilterDescription.DEFAULT_FILTER, Collections.emptyList())
                .stream()
                .filter(CbMethodHandle::isEventHandler)
                .map(CbMethodHandle::getInstance)
                .map(this::nodeIndex)
                .forEach(dirtyBitset::set);
        //now actually dispatch
        for (int i = dirtyBitset.nextSetBit(0); i >= 0; i = dirtyBitset.nextSetBit(i+1)) {
            eventHandlers.get(i).onEvent(event);
        }
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
    private void invokeRunnable(CbMethodHandle callBackHandle){
        callBackHandle.method.setAccessible(true);
        callBackHandle.method.invoke(callBackHandle.instance);
    }

    public Field getFieldByName(String name){
        return simpleEventProcessorModel.getFieldForName(name);
    }

    /**
     * Implementation notes:
     * <ul>
     *     <li>A node class that encapsulates call back information</li>
     *     <li>An array of nodes sorted in topological order</li>
     *     <li>A Bitset that holds the dirty state of nodes during an event cycle</li>
     *     <li></li>
     * </ul>
     */
    private void buildDispatch(){
        List<CbMethodHandle> dispatchMapForGraph = new ArrayList<>(simpleEventProcessorModel.getDispatchMapForGraph());
        log.warn("callbacks for graph:{}", dispatchMapForGraph);
        dirtyBitset.clear(dispatchMapForGraph.size());
        Map<Object, List<CbMethodHandle>> parentUpdateListenerMethodMap = simpleEventProcessorModel.getParentUpdateListenerMethodMap();
        Map<Object, Node> instance2NodeMap = new HashMap<>(dispatchMapForGraph.size());
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
        //allocate dependents
        for (Node handler : eventHandlers) {
            handler.setDependents(
                    simpleEventProcessorModel.getDirectChildrenListeningForEvent(handler.callbackHandle.getInstance()).stream()
                    .map(instance2NodeMap::get)
                    .collect(Collectors.toList())
            );
            handler.init();
        }
    }

    private int nodeIndex(Object nodeInstance){
        return eventHandlers.stream()
                .filter(n -> n.sameInstance(nodeInstance))
                .findFirst()
                .map(Node::getPosition).orElse(-1);
    }

    @RequiredArgsConstructor
    private class Node implements StaticEventProcessor, Lifecycle{

        final int position;
        final CbMethodHandle callbackHandle;
        final List<CbMethodHandle> parentListeners;
        final List<Node> dependents = new ArrayList<>();

        @Override
        @SneakyThrows
        public void onEvent(Object e) {
            //dispatch to callbackHandle
            //if: dirty
            //then: dispatch to parentListeners,
            if(callbackHandle.isEventHandler){
                callbackHandle.method.invoke(callbackHandle.instance, e);
            }else{
                callbackHandle.method.invoke(callbackHandle.instance);
            }
            dependents.forEach(Node::markDirty);
            for (CbMethodHandle cb : parentListeners) {
                cb.method.invoke(cb.instance, callbackHandle.instance);
            }
        }

        private void setDependents(List<Node> dependents){
            this.dependents.clear();
            this.dependents.addAll(dependents);
        }

        private void markDirty(){
            dirtyBitset.set(position);
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

        boolean sameInstance(Object other){
            return Objects.equals(callbackHandle.instance, other);
        }

        public int getPosition() {
            return position;
        }
    }
}
