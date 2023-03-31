package com.fluxtion.runtime.dataflow.function;

import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;

public class FilterByPropertyFlowFunction<T, P, S extends FlowFunction<T>> extends AbstractFlowFunction<T, T, S> {

    final SerializableFunction<P, Boolean> filterFunction;

    final SerializableFunction<T, P> propertyAccessor;

    transient final String auditInfo;

    public FilterByPropertyFlowFunction(
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