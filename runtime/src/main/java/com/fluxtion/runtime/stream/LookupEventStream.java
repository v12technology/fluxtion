package com.fluxtion.runtime.stream;

import com.fluxtion.runtime.annotations.OnEvent;
import com.fluxtion.runtime.partition.LambdaReflection;

/**
 * Lookup a value from a memeber variable on the source
 * @param <R> Type of input stream
 * @param <T> Output type of this stream
 * @param <S> The type of {@link EventStream} that wraps R
 */
public class LookupEventStream <R, T, S extends EventStream<R>, I, L> extends AbstractEventStream<R, T, S>{

    private final LambdaReflection.SerializableBiFunction<R, L, T> mapFunction;
    private final LambdaReflection.SerializableFunction<I, L> lookupFunction;
    private final LambdaReflection.SerializableFunction<R, I> lookupKeyFunction;
    private T streamOutputValue;

    public LookupEventStream(S inputEventStream,
                             LambdaReflection.SerializableFunction<R, I> lookupKeyFunction,
                             LambdaReflection.SerializableFunction<I, L> lookupFunction,
                             LambdaReflection.SerializableBiFunction<R, L, T> methodReferenceReflection
                             ) {
        super(inputEventStream, methodReferenceReflection);
        this.mapFunction = methodReferenceReflection;
        this.lookupKeyFunction = lookupKeyFunction;
        this.lookupFunction = lookupFunction;
    }

    @OnEvent
    public boolean applyLookup() {
        R streamValue = getInputEventStream().get();
        I lookupKey = lookupKeyFunction.apply(streamValue);
        boolean filter = isPublishTriggered() || lookupKey !=null;
        boolean fireNotification = filter & fireEventUpdateNotification();
//        auditLog.info("filterFunction", auditInfo);
        auditLog.info("foundLookupValue", filter);
        auditLog.info("publishToChild", fireNotification);
        if(lookupKey !=null){
            L lookupValue = lookupFunction.apply(lookupKey);
            streamOutputValue = mapFunction.apply(streamValue, lookupValue);
        }
        return fireNotification;
    }

    @Override
    public T get() {
        return streamOutputValue;
    }
}
