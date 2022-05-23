package com.fluxtion.runtime.stream;

import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.stream.AbstractEventStream.AbstractBinaryEventStream;

public class FilterByPropertyDynamicEventStream<T, P, A, S extends EventStream<T>, B extends EventStream<A>>
        extends AbstractBinaryEventStream<T, A, T, S, B> {

    private final SerializableFunction<T, P> propertyAccessor;
    private final SerializableBiFunction<P, A, Boolean> filterFunction;
    private transient final String auditInfo;

    public FilterByPropertyDynamicEventStream(S inputEventStream,
                                              SerializableFunction<T, P> propertyAccessor,
                                              B inputEventStream_2,
                                              SerializableBiFunction<P, A, Boolean> filterFunction) {
        super(inputEventStream, inputEventStream_2, filterFunction);
        this.propertyAccessor = propertyAccessor;
        this.filterFunction = filterFunction;
        auditInfo = filterFunction.method().getDeclaringClass().getSimpleName() + "->" + filterFunction.method().getName();
    }

    @OnTrigger
    public boolean filter() {
        boolean filter = inputStreamTriggered_1
                & (inputStreamTriggered_2)
                && (isPublishTriggered() || filterFunction.apply(propertyAccessor.apply(getInputEventStream().get()), secondArgument()));
        inputStreamTriggered_1 = false;
        boolean fireNotification = filter & fireEventUpdateNotification();
        auditLog.info("filterFunction", auditInfo);
        auditLog.info("filterPass", filter);
        auditLog.info("publishToChild", fireNotification);
        return fireNotification;
    }

    @Override
    public T get() {
        return getInputEventStream().get();
    }

    private A secondArgument() {
        return getInputEventStream_2().get();
    }
}