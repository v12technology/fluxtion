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
    public boolean map(){
        auditLog.info("mapFunction", auditInfo);
        if(executeUpdate()){
            auditLog.info("invokeMapFunction", true);
            mapOperation();
        }else{
            auditLog.info("invokeMapFunction", false);
        }
        return fireEventUpdateNotification();
    }

    @Override
    public T get() {
        return result;
    }

    protected void mapOperation(){
        result = mapFunction.apply(getInputEventStream().get());
    }

//    public static class MapReferenceEventStream<R, T> extends MapEventStream<R, T>{
//
//        final LambdaReflection.SerializableFunction<R, T> mapFunction;
//
//        public MapReferenceEventStream(EventStream<R> inputEventStream, LambdaReflection.SerializableFunction<R, T> mapFunction) {
//            super(inputEventStream, mapFunction);
//        }
//    }
//
//    public static class MapInt2IntEventStream extends MapEventStream<Integer, Integer>{
//
//        public MapInt2IntEventStream(EventStream<Integer> inputEventStream, LambdaReflection.SerializableFunction<Integer, Integer> mapFunction) {
//            super(inputEventStream, mapFunction);
//        }
//    }

}
