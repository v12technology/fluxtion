package com.fluxtion.runtime.callback;

import com.fluxtion.runtime.audit.Auditor;
import lombok.ToString;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ToString
public class CallbackDispatcherImpl implements Auditor, CallbackDispatcher, EventDispatcher {

    public Consumer<Object> processor;
    Deque<Supplier<Boolean>> myStack = new ArrayDeque<>();

    private boolean dispatching = false;

    @Override
    public void nodeRegistered(Object node, String nodeName) {
        if (CallbackDispatcherListener.class.isAssignableFrom(node.getClass())) {
            CallbackDispatcherListener callbackReceiver = (CallbackDispatcherListener) node;
            callbackReceiver.registerCallbackDispatcher(this);
        }
    }

    public void dispatchQueuedCallbacks() {
        if (processor == null) {
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
    public boolean auditInvocations() {
        return false;
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
    public void processEvent(Object event) {
        SingleEventPublishWrapper<Object> callBackWrapper = new SingleEventPublishWrapper<>();
        callBackWrapper.data = event;
        myStack.add(callBackWrapper::dispatch);
    }

    public void processEvents(Iterable<Object> iterable) {
        IteratingEventPublishWrapper publishingWrapper = new IteratingEventPublishWrapper();
        publishingWrapper.dataIterator = iterable.iterator();
        myStack.add(publishingWrapper::dispatch);
    }

    @ToString(callSuper = true)
    private class SingleCallBackWrapper<T> extends CallbackEvent<T> {

        private final CallbackEvent<T> callbackEvent = new CallbackEvent<>();

        boolean dispatch() {
            //System.out.println("dispatching this id:" + filterId);
            callbackEvent.setData(getData());
            callbackEvent.setFilterId(getFilterId());
            processor.accept(callbackEvent);
            setData(null);
            setFilterId(Integer.MAX_VALUE);
            return false;
        }
    }

    @ToString(callSuper = true)
    private class SingleEventPublishWrapper<T> {

        T data;

        boolean dispatch() {
            processor.accept(data);
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
                processor.accept(callbackEvent);
                return true;
            }
            return false;
        }
    }

    private class IteratingEventPublishWrapper {
        Iterator<Object> dataIterator;

        boolean dispatch() {
            if (dataIterator.hasNext()) {
                processor.accept(dataIterator.next());
                return true;
            }
            return false;
        }
    }

}
