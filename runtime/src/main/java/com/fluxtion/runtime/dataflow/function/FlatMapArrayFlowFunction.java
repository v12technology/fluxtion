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

import java.util.Arrays;

/**
 * Flatmap stream node
 *
 * @param <T> Incoming type
 * @param <R> Output type
 * @param <S> Previous {@link FlowFunction} type
 */
public class FlatMapArrayFlowFunction<T, R, S extends FlowFunction<T>> extends BaseNode implements TriggeredFlowFunction<R> {

    @NoTriggerReference
    private final S inputEventStream;
    @NoTriggerReference
    private final transient Object streamFunctionInstance;
    @Inject
    public DirtyStateMonitor dirtyStateMonitor;
    private final SerializableFunction<T, R[]> iterableFunction;
    private transient R value;
    @Inject
    public Callback<R> callback;
    @Getter
    @Setter
    private String flatMapCompleteSignal;

    public FlatMapArrayFlowFunction(S inputEventStream, SerializableFunction<T, R[]> iterableFunction) {
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
        T input = inputEventStream.get();
        Iterable<R> iterable = Arrays.asList(iterableFunction.apply(input));
        callback.fireCallback(iterable.iterator());
        if (flatMapCompleteSignal != null) {
            getContext().getStaticEventProcessor().publishSignal(flatMapCompleteSignal, flatMapCompleteSignal);
        }
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

    @OnTrigger
    public void callbackReceived() {
        value = callback.get();
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
