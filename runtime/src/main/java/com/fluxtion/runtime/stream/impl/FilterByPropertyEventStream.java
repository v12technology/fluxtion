package com.fluxtion.runtime.stream.impl;

import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.stream.EventStream;

public class FilterByPropertyEventStream<T, P, S extends EventStream<T>> extends AbstractEventStream<T, T, S> {

    final SerializableFunction<P, Boolean> filterFunction;

    final SerializableFunction<T, P> propertyAccessor;

    transient final String auditInfo;

    public FilterByPropertyEventStream(
            S inputEventStream,
            @AssignToField("propertyAccessor") SerializableFunction<T, P> propertyAccessor,
            @AssignToField("filterFunction") SerializableFunction<P, Boolean> filterFunction) {
        super(inputEventStream, filterFunction);
        this.propertyAccessor = propertyAccessor;
        this.filterFunction = filterFunction;
        auditInfo = filterFunction.method().getDeclaringClass().getSimpleName() + "->" + filterFunction.method().getName();
    }

    @OnTrigger
    public boolean filter() {
        boolean filter = isPublishTriggered() || filterFunction.apply(propertyAccessor.apply(getInputEventStream().get()));
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
}