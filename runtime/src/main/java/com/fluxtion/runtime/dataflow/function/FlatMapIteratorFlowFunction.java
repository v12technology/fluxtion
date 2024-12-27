/*
 * SPDX-FileCopyrightText: © 2024 Gregory Higgins <greg.higgins@v12technology.com>
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package com.fluxtion.runtime.dataflow.function;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.callback.Callback;
import com.fluxtion.runtime.callback.DirtyStateMonitor;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.node.BaseNode;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import lombok.Getter;
import lombok.Setter;

import java.util.Iterator;

/**
 * Flatmap stream node
 *
 * @param <T> Incoming type
 * @param <R> Output type
 * @param <S> Previous {@link FlowFunction} type
 */
public class FlatMapIteratorFlowFunction<T, R, S extends FlowFunction<T>> extends BaseNode implements TriggeredFlowFunction<R> {

    @NoTriggerReference
    private final S inputEventStream;
    @NoTriggerReference
    private final transient Object streamFunctionInstance;
    private final SerializableFunction<T, Iterator<R>> iterableFunction;
    private transient R value;
    @Inject
    public Callback<R> callback;
    @Inject
    public DirtyStateMonitor dirtyStateMonitor;
    @Getter
    @Setter
    private String flatMapCompleteSignal;

    public FlatMapIteratorFlowFunction(S inputEventStream, SerializableFunction<T, Iterator<R>> iterableFunction) {
        this.inputEventStream = inputEventStream;
        this.iterableFunction = iterableFunction;
        if (iterableFunction.captured().length > 0) {
            streamFunctionInstance = EventProcessorBuilderService.service().addOrReuse(iterableFunction.captured()[0]);
        } else {
            streamFunctionInstance = null;
        }
    }

    @OnParentUpdate("inputEventStream")
    public void inputUpdatedAndFlatMap(S inputEventStream) {
        callback.fireCallback(iterableFunction.apply(inputEventStream.get()));
        if (flatMapCompleteSignal != null) {
            getContext().getStaticEventProcessor().publishSignal(flatMapCompleteSignal, flatMapCompleteSignal);
        }
    }

    @OnTrigger
    public void callbackReceived() {
        value = callback.get();
    }

    @Override
    public void parallel() {

    }

    @Override
    public boolean parallelCandidate() {
        return false;
    }

    @Override
    public boolean hasChanged() {
        return dirtyStateMonitor.isDirty(this);
    }

    @Override
    public R get() {
        return value;
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
