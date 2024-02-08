package com.fluxtion.runtime.callback;

import com.fluxtion.runtime.node.NamedNode;
import lombok.ToString;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@ToString
public class CallbackDispatcherImpl implements EventProcessorCallbackInternal, NamedNode, DirtyStateMonitor {

    public InternalEventProcessor eventProcessor;
    Deque<Supplier<Boolean>> myStack = new ArrayDeque<>();
    private boolean dispatching = false;

    @Override
    public void dispatchQueuedCallbacks() {
        if (eventProcessor == null) {
            //System.out.println("no event processor registered cannot publish callback");
        } else {
            while (!myStack.isEmpty()) {
                dispatching = true;
                Supplier<Boolean> callBackItem = myStack.peekFirst();
                if (!callBackItem.get()) {
                    myStack.remove(callBackItem);
                }
            }
        }
        dispatching = false;
    }

    @Override
    public void fireCallback(int id) {
        SingleCallBackWrapper<Object> callBackWrapper = new SingleCallBackWrapper<>();
        callBackWrapper.setFilterId(id);
        myStack.add(callBackWrapper::dispatch);
    }

    @Override
    public <T> void fireCallback(int id, T item) {
        //System.out.println("firing callback id:" + id + " item:" + item);
        SingleCallBackWrapper<T> callBackWrapper = new SingleCallBackWrapper<>();
        callBackWrapper.setFilterId(id);
        callBackWrapper.setData(item);
        myStack.add(callBackWrapper::dispatch);
    }

    @Override
    public <R> void fireIteratorCallback(int callbackId, Iterator<R> dataIterator) {
        IteratingCallbackWrapper<R> callBackWrapper = new IteratingCallbackWrapper<>();
        callBackWrapper.setFilterId(callbackId);
        callBackWrapper.dataIterator = dataIterator;
        if (dispatching) {
            //System.out.println("DISPATCHING adding iterator to FRONT of callback queue id:" + callbackId);
            myStack.addFirst(callBackWrapper::dispatch);
        } else {
            //System.out.println("adding iterator to BACK of callback queue id:" + callbackId);
            myStack.add(callBackWrapper::dispatch);
        }
    }

    @Override
    public void processReentrantEvent(Object event) {
        SingleEventPublishWrapper<Object> callBackWrapper = new SingleEventPublishWrapper<>();
        callBackWrapper.data = event;
        myStack.add(callBackWrapper::dispatch);
    }

    @Override
    public void processReentrantEvents(Iterable<Object> iterable) {
        IteratingEventPublishWrapper publishingWrapper = new IteratingEventPublishWrapper();
        publishingWrapper.dataIterator = iterable.iterator();
        myStack.add(publishingWrapper::dispatch);
    }

    @Override
    public void processAsNewEventCycle(Object event) {
        eventProcessor.onEvent(event);
    }

    @Override
    public <T> T getService() {
        return eventProcessor.getService();
    }

    @Override
    public String getName() {
        return CallbackDispatcher.DEFAULT_NODE_NAME;
    }

    @Override
    public boolean isDirty(Object node) {
        return node != null && eventProcessor.isDirty(node);
    }

    @Override
    public BooleanSupplier dirtySupplier(Object node) {
        return eventProcessor.dirtySupplier(node);
    }

    @Override
    public void markDirty(Object node) {
        eventProcessor.setDirty(node, true);
    }

    //    @Override
    public <T> T getNodeById(String id) throws NoSuchFieldException {
        return eventProcessor.getNodeById(id);
    }

    @Override
    public void setEventProcessor(InternalEventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    @ToString(callSuper = true)
    private class SingleCallBackWrapper<T> extends CallbackEvent<T> {

        private final CallbackEvent<T> callbackEvent = new CallbackEvent<>();

        boolean dispatch() {
            //System.out.println("dispatching this id:" + filterId);
            callbackEvent.setData(getData());
            callbackEvent.setFilterId(getFilterId());
            eventProcessor.onEventInternal(callbackEvent);
            setData(null);
            setFilterId(Integer.MAX_VALUE);
            return false;
        }
    }

    @ToString(callSuper = true)
    private class SingleEventPublishWrapper<T> {

        T data;

        boolean dispatch() {
            eventProcessor.onEventInternal(data);
            return false;
        }
    }

    @ToString(callSuper = true)
    private class IteratingCallbackWrapper<T> extends CallbackEvent<T> {
        Iterator<T> dataIterator;
        private final CallbackEvent<T> callbackEvent = new CallbackEvent<>();

        boolean dispatch() {
            //System.out.println("dispatching this id:" + filterId);
            if (dataIterator.hasNext()) {
                callbackEvent.setData(dataIterator.next());
                callbackEvent.setFilterId(getFilterId());
                eventProcessor.onEventInternal(callbackEvent);
                return true;
            }
            return false;
        }
    }

    private class IteratingEventPublishWrapper {
        Iterator<Object> dataIterator;

        boolean dispatch() {
            if (dataIterator.hasNext()) {
                eventProcessor.onEventInternal(dataIterator.next());
                return true;
            }
            return false;
        }
    }

}
