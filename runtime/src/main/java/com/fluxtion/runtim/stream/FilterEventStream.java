package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableIntFunction;

public class FilterEventStream<T, S extends EventStream<T>> extends AbstractEventStream<T, T, S> {

    final SerializableFunction<T, Boolean> filterFunction;
    transient final String auditInfo;

    public FilterEventStream(S inputEventStream, SerializableFunction<T, Boolean> filterFunction) {
        super(inputEventStream);
        this.filterFunction = filterFunction;
        auditInfo = filterFunction.method().getDeclaringClass().getSimpleName() + "->" + filterFunction.method().getName();
    }

    @OnEvent
    public boolean filter() {
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


    public static class IntFilterEventStream extends AbstractEventStream<Integer, Integer, IntEventStream> implements IntEventStream {

        final SerializableIntFunction<Boolean> filterFunction;
        transient final String auditInfo;

        public IntFilterEventStream(IntEventStream inputEventStream, SerializableIntFunction<Boolean> filterFunction) {
            super(inputEventStream);
            this.filterFunction = filterFunction;
            auditInfo = filterFunction.method().getDeclaringClass().getSimpleName() + "->" + filterFunction.method().getName();
        }

        @OnEvent
        public boolean filter() {
            boolean filter = isPublishTriggered() || filterFunction.apply(getInputEventStream().get());
            boolean fireNotification = filter & fireEventUpdateNotification();
            auditLog.info("filterFunction", auditInfo);
            auditLog.info("filterPass", filter);
            auditLog.info("publishToChild", fireNotification);
            return fireNotification;
        }

        @Override
        public Integer get() {
            return getAsInt();
        }

        @Override
        public int getAsInt() {
            return getInputEventStream().getAsInt();
        }
    }
}
