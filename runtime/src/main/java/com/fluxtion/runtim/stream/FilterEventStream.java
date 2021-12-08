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
        auditLog.info("filterFunction", auditInfo);
        return filterFunction.apply(getInputEventStream().get());
    }

    @Override
    public T get() {
        return getInputEventStream().get();
    }

}
