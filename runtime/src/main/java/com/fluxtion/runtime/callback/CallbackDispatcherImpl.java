package com.fluxtion.runtime.callback;

import com.fluxtion.runtime.audit.Auditor;
import lombok.ToString;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ToString
public class CallbackDispatcherImpl implements Auditor, CallbackDispatcher {

    public Consumer<CallbackEvent<?>> processor;
    Deque<Supplier<Boolean>> myStack = new ArrayDeque<>();

    private boolean dispatching = false;
    @Override
    public void nodeRegistered(Object node, String nodeName) {
        if(CallbackImpl.class.isAssignableFrom(node.getClass())){
            CallbackImpl callbackReceiver = (CallbackImpl) node;
            //System.out.println("registering callback:" + callbackReceiver);
            callbackReceiver.registerCallbackDispatcher(this);
        }
    }

    public void dispatchQueuedCallbacks(){
        if(processor == null){
            //System.out.println("no event processor registered cannot publish callback");
        }else{
            while(!myStack.isEmpty()){
                dispatching = true;
                //do not remove this is responsibility of the callback item*
                Supplier<Boolean> callBackItem = myStack.peekFirst();
                //System.out.println("dispatching callback id:" + callBackItem);
                if(!callBackItem.get()){
                    myStack.remove(callBackItem);
                }
            }
            //System.out.println("no callbacks on stack");
        }
        dispatching = false;
    }

    @Override
    public boolean auditInvocations() {
        return false;
    }

    @Override
    public void fireCallback(int id) {
        //System.out.println("adding to callback queue id:" + id);
        MyCallBackWrapper callBackWrapper = new MyCallBackWrapper();
        callBackWrapper.setFilterId(id);
        myStack.add(callBackWrapper::dispatch);
    }

    @Override
    public <T> void fireCallback(int id, T item) {
        //System.out.println("firing callback id:" + id + " item:" + item);
        MyCallBackWrapper<T> callBackWrapper = new MyCallBackWrapper<>();
        callBackWrapper.setFilterId(id);
        callBackWrapper.setData(item);
        myStack.add(callBackWrapper::dispatch);
    }

    @Override
    public <R> void fireIteratorCallback(int callbackId, Iterator<R> dataIterator) {
        IteratingCallbackWrapper<R> callBackWrapper = new IteratingCallbackWrapper<>();
        callBackWrapper.setFilterId(callbackId);
        callBackWrapper.dataIterator = dataIterator;
        if(dispatching){
            //System.out.println("DISPATCHING adding iterator to FRONT of callback queue id:" + callbackId);
            myStack.addFirst(callBackWrapper::dispatch);
        }else{
            //System.out.println("adding iterator to BACK of callback queue id:" + callbackId);
            myStack.add(callBackWrapper::dispatch);
        }
    }

    @ToString(callSuper = true)
    private class MyCallBackWrapper<T> extends CallbackEvent<T>{

        private CallbackEvent<T> callbackEvent = new CallbackEvent<>();
        boolean dispatch(){
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
    private class IteratingCallbackWrapper<T> extends CallbackEvent<T>{
        Iterator<T> dataIterator;
        private CallbackEvent<T> callbackEvent = new CallbackEvent<>();
        boolean dispatch(){
            //System.out.println("dispatching this id:" + filterId);
            if(dataIterator.hasNext()){
                callbackEvent.setData(dataIterator.next());
                callbackEvent.setFilterId(getFilterId());
                processor.accept(callbackEvent);
                return true;
            }
            return false;
        }
    }




}
