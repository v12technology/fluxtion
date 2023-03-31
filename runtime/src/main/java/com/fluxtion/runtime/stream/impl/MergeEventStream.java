package com.fluxtion.runtime.stream.impl;

import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.stream.EventStream;
import com.fluxtion.runtime.stream.TriggeredEventStream;

public class MergeEventStream<T, S extends EventStream<T>, R extends EventStream<? extends T>> extends EventLogNode
        implements TriggeredEventStream<T> {

    private final S inputEventStream1;
    private final R inputEventStream2;
    private T update;

    public MergeEventStream(S inputEventStream1, R inputEventStream2) {
        this.inputEventStream1 = inputEventStream1;
        this.inputEventStream2 = inputEventStream2;
    }

    @OnParentUpdate("inputEventStream1")
    public void inputStream1Updated(S inputEventStream1) {
        update = inputEventStream1.get();
    }

    @OnParentUpdate("inputEventStream2")
    public void inputStream2Updated(R inputEventStream2) {
        update = inputEventStream2.get();
    }

    @OnTrigger
    public boolean publishMerge() {
        return update != null;
    }

    @Override
    public T get() {
        return update;
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
}
