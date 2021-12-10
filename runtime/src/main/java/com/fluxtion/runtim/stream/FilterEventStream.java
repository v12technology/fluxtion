package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.partition.LambdaReflection;

public class FilterEventStream<T> extends AbstractEventStream<T, T> {

    final LambdaReflection.SerializableFunction<T, Boolean> filterFunction;
    transient final String auditInfo;

    public FilterEventStream(EventStream<T> inputEventStream, LambdaReflection.SerializableFunction<T, Boolean> filterFunction) {
        super(inputEventStream);
        this.filterFunction = filterFunction;
        auditInfo = filterFunction.method().getDeclaringClass().getSimpleName() + "->" + filterFunction.method().getName();
    }

    @OnEvent
    public boolean filter(){
        boolean filter = isPublishTriggered() || filterFunction.apply(getInputEventStream().get());
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
