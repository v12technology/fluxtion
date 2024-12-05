/*
 * SPDX-FileCopyrightText: © 2024 Gregory Higgins <greg.higgins@v12technology.com>
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package com.fluxtion.runtime.callback;

import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.node.AbstractEventHandlerNode;
import com.fluxtion.runtime.node.NamedNode;
import lombok.ToString;

import java.util.Iterator;
import java.util.function.BooleanSupplier;

@ToString
public class CallbackImpl<R, T extends CallbackEvent<?>> extends AbstractEventHandlerNode<CallbackEvent>
        implements TriggeredFlowFunction<R>, NamedNode, Callback<R>, EventDispatcher {
    private final int callbackId;
    @Inject
    public DirtyStateMonitor dirtyStateMonitor;
    @Inject
    private final CallbackDispatcher callBackDispatcher;
    private CallbackEvent<R> event;
    private BooleanSupplier dirtyStateSupplier;

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
    public boolean hasChanged() {
        return dirtyStateSupplier.getAsBoolean();
    }

    @Initialise
    public void init() {
        dirtyStateSupplier = dirtyStateMonitor.dirtySupplier(this);
    }

    @Override
    public void parallel() {

    }

    @Override
    public boolean parallelCandidate() {
        return false;
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
    public void fireNewEventCycle(R data) {
        callBackDispatcher.processAsNewEventCycle(event);
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
    public void queueReentrantEvent(Object event) {
        callBackDispatcher.queueReentrantEvent(event);
    }

    @Override
    public void processAsNewEventCycle(Object event) {
        callBackDispatcher.processAsNewEventCycle(event);
    }
}
