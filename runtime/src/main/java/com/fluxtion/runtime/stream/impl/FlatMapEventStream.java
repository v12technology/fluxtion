package com.fluxtion.runtime.stream.impl;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.callback.Callback;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.stream.EventStream;
import com.fluxtion.runtime.stream.TriggeredEventStream;

/**
 * Flatmap stream node
 *
 * @param <T> Incoming type
 * @param <R> Output type
 * @param <S> Previous {@link EventStream} type
 */
public class FlatMapEventStream<T, R, S extends EventStream<T>> extends EventLogNode implements TriggeredEventStream<R> {

    @NoTriggerReference
    private final S inputEventStream;
    @NoTriggerReference
    private final transient Object streamFunctionInstance;
    private final SerializableFunction<T, Iterable<R>> iterableFunction;
    private transient R value;
    @Inject
    public Callback<R> callback;

    public FlatMapEventStream(S inputEventStream, SerializableFunction<T, Iterable<R>> iterableFunction) {
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
        Iterable<R> iterable = iterableFunction.apply(input);
        callback.fireCallback(iterable.iterator());
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
