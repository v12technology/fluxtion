package com.fluxtion.runtime.stream;

import com.fluxtion.runtime.EventProcessorConfigService;
import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.callback.Callback;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;

import java.util.Arrays;

/**
 * Flatmap stream node
 *
 * @param <T> Incoming type
 * @param <R> Output type
 * @param <S> Previous {@link EventStream} type
 */
public class FlatMapArrayEventStream<T, R, S extends EventStream<T>> extends EventLogNode implements TriggeredEventStream<R> {

    @NoTriggerReference
    private final S inputEventStream;
    @NoTriggerReference
    private final transient Object streamFunctionInstance;
    private final SerializableFunction<T, R[]> iterableFunction;
    private transient R value;
    @Inject
    public Callback<R> callback;

    public FlatMapArrayEventStream(S inputEventStream, SerializableFunction<T, R[]> iterableFunction) {
        this.inputEventStream = inputEventStream;
        this.iterableFunction = iterableFunction;
        if (iterableFunction.captured().length > 0){
            streamFunctionInstance = EventProcessorConfigService.service().addOrReuse(iterableFunction.captured()[0]);
        }else{
            streamFunctionInstance = null;
        }
    }

    @OnParentUpdate("inputEventStream")
    public void inputUpdatedAndFlatMap(S inputEventStream){
        T input = inputEventStream.get();
        Iterable<R> iterable = Arrays.asList(iterableFunction.apply(input));
        callback.fireCallback(iterable.iterator());
    }

    @OnTrigger
    public void callbackReceived(){
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
