package com.fluxtion.runtime.dataflow.function;

import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.partition.LambdaReflection;

/**
 * Lookup a value from a memeber variable on the source
 *
 * @param <R> Type of input stream
 * @param <T> Output type of this stream
 * @param <S> The type of {@link FlowFunction} that wraps R
 */
public class LookupFlowFunction<R, T, S extends FlowFunction<R>, I, L> extends AbstractFlowFunction<R, T, S> {

    private final LambdaReflection.SerializableBiFunction<R, L, T> mapFunction;
    private final LambdaReflection.SerializableFunction<I, L> lookupFunction;
    private final LambdaReflection.SerializableFunction<R, I> lookupKeyFunction;
    private T streamOutputValue;

    public LookupFlowFunction(S inputEventStream,
                              @AssignToField("lookupKeyFunction")
                              LambdaReflection.SerializableFunction<R, I> lookupKeyFunction,
                              @AssignToField("lookupFunction")
                              LambdaReflection.SerializableFunction<I, L> lookupFunction,
                              @AssignToField("mapFunction")
                              LambdaReflection.SerializableBiFunction<R, L, T> methodReferenceReflection
    ) {
        super(inputEventStream, methodReferenceReflection);
        this.mapFunction = methodReferenceReflection;
        this.lookupKeyFunction = lookupKeyFunction;
        this.lookupFunction = lookupFunction;
    }

    @OnTrigger
    public boolean applyLookup() {
        R streamValue = getInputEventStream().get();
        I lookupKey = lookupKeyFunction.apply(streamValue);
        if (lookupKey != null) {
            L lookupValue = lookupFunction.apply(lookupKey);
            streamOutputValue = mapFunction.apply(streamValue, lookupValue);
        }
        boolean filter = isPublishTriggered() || lookupKey != null;
        boolean fireNotification = filter & fireEventUpdateNotification();
//        auditLog.info("filterFunction", auditInfo);
        auditLog.info("foundLookupValue", filter);
        auditLog.info("publishToChild", fireNotification);
        return fireNotification;
    }

    @Override
    public T get() {
        return streamOutputValue;
    }
}
