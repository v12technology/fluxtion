package com.fluxtion.runtime.callback;

import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.node.AbstractEventHandlerNode;
import com.fluxtion.runtime.node.NamedNode;
import lombok.ToString;

import java.util.Iterator;

@ToString
public class CallbackImpl<R, T extends CallbackEvent<?>> extends AbstractEventHandlerNode<CallbackEvent>
        implements TriggeredFlowFunction<R>, NamedNode, Callback<R>, EventDispatcher {
    private final int callbackId;
    @Inject
    private final CallbackDispatcher callBackDispatcher;
    private CallbackEvent<R> event;

    public CallbackImpl(int callbackId, CallbackDispatcher callBackDispatcher) {
        super(callbackId);
        this.callbackId = callbackId;
        this.callBackDispatcher = callBackDispatcher;
    }

    public CallbackImpl(int callbackId) {
        this(callbackId, null);
    }

    @Override
    public Class<CallbackEvent> eventClass() {
        return CallbackEvent.class;
    }

    @Override
    public boolean onEvent(CallbackEvent e) {
        event = e;
        return true;
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

    @Override
    public void setPublishTriggerOverrideNode(Object publishTriggerOverrideNode) {
    }

    @Override
    public void fireCallback() {
        callBackDispatcher.fireCallback(callbackId);
    }

    @Override
    public void fireCallback(R data) {
        callBackDispatcher.fireCallback(callbackId, data);
    }

    @Override
    public void fireCallback(Iterator<R> dataIterator) {
        callBackDispatcher.fireIteratorCallback(callbackId, dataIterator);
    }

    @Override
    public void processReentrantEvent(Object event) {
        callBackDispatcher.processReentrantEvent(event);
    }

    @Override
    public void processReentrantEvents(Iterable<Object> iterator) {
        callBackDispatcher.processReentrantEvents(iterator);
    }

    @Override
    public void processAsNewEventCycle(Object event) {
        callBackDispatcher.processAsNewEventCycle(event);
    }
}
