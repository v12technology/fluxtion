package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.partition.LambdaReflection;

public class MapEventStream<R, T> extends AbstractEventStream<R, T> {//implements EventStream<T>{

    final LambdaReflection.SerializableFunction<R, T> mapFunction;
    private transient final String auditInfo;

    private transient T result;

    public MapEventStream(EventStream<R> inputEventStream, LambdaReflection.SerializableFunction<R, T> mapFunction) {
        super(inputEventStream);
        this.mapFunction = mapFunction;
        auditInfo = mapFunction.method().getDeclaringClass().getSimpleName() + "->" + mapFunction.method().getName();
    }

    @OnEvent
    public void map(){
        auditLog.info("mapFunction", auditInfo);
        result = mapFunction.apply(getInputEventStream().get());
    }

    @Override
    public T get() {
        return result;
    }

}
