package com.fluxtion.runtime.callback;

import com.fluxtion.runtime.Named;
import com.fluxtion.runtime.event.AbstractFilteredEventHandler;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import lombok.ToString;

import java.util.Iterator;

@ToString
public class CallbackImpl<R, T extends CallbackEvent<?>> extends AbstractFilteredEventHandler<CallbackEvent>
        implements TriggeredEventStream<R>, Named, Callback<R>, EventDispatcher {
    private final int callbackId;
    private CallbackDispatcher callBackDispatcher;
    private CallbackEvent<R> event;

    public CallbackImpl(int callbackId) {
        super(callbackId);
        this.callbackId = callbackId;
    }

    @Override
    public Class<CallbackEvent> eventClass() {
        return CallbackEvent.class;
    }

    @Override
    public void onEvent(CallbackEvent e) {
        event = e;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public R get() {
        return event.getData();
    }

    @Override
    public void setUpdateTriggerNode(Object updateTriggerNode) {

    }

    @Override
    public void setPublishTriggerNode(Object publishTriggerNode) {

    }

    @Override
    public void setResetTriggerNode(Object resetTriggerNode) {

    }

    public void registerCallbackDispatcher(CallbackDispatcher callBackDispatcher){
        this.callBackDispatcher = callBackDispatcher;
    }

    @Override
    public void fireCallback() {
        callBackDispatcher.fireCallback(callbackId);
    }

    @Override
    public void fireCallback(R data){
        callBackDispatcher.fireCallback(callbackId, data);
    }

    @Override
    public void fireCallback(Iterator<R> dataIterator) {
        callBackDispatcher.fireIteratorCallback(callbackId, dataIterator);
    }

    @Override
    public void processEvent(Object event) {
        callBackDispatcher.processEvent(event);
    }

    @Override
    public void processEvents(Iterable<Object> iterator) {
        callBackDispatcher.processEvents(iterator);
    }
}
