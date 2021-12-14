package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtim.SepContext;
import com.fluxtion.runtim.partition.LambdaReflection;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableBiLongFunction;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableLongConsumer;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableLongFunction;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableLongUnaryOperator;
import com.fluxtion.runtim.stream.*;
import com.fluxtion.runtim.stream.EventStream.LongEventStream;
import com.fluxtion.runtim.stream.helpers.DefaultValue;

public class LongStreamBuilder {

    final LongEventStream eventStream;

    LongStreamBuilder(LongEventStream eventStream) {
        SepContext.service().add(eventStream);
        this.eventStream = eventStream;
    }

    //TRIGGERS - START
    public LongStreamBuilder updateTrigger(Object updateTrigger){
        eventStream.setUpdateTriggerNode(StreamHelper.getSource(updateTrigger));
        return this;
    }

    public LongStreamBuilder publishTrigger(Object publishTrigger){
        eventStream.setPublishTriggerNode(StreamHelper.getSource(publishTrigger));
        return this;
    }

    public LongStreamBuilder resetTrigger(Object resetTrigger){
        eventStream.setResetTriggerNode(StreamHelper.getSource(resetTrigger));
        return this;
    }

    public LongStreamBuilder filter(SerializableLongFunction<Boolean> filterFunction){
        return new LongStreamBuilder( new FilterEventStream.LongFilterEventStream(eventStream, filterFunction));
    }

    public LongStreamBuilder defaultValue(long defaultValue){
        return map(new DefaultValue.DefaultLong(defaultValue)::getOrDefault);
    }

    //PROCESSING - START
    public LongStreamBuilder map(SerializableLongUnaryOperator int2IntFunction) {
        return new LongStreamBuilder(new MapEventStream.MapLong2ToLongEventStream(eventStream, int2IntFunction));
    }

    public LongStreamBuilder map(SerializableBiLongFunction int2IntFunction, LongStreamBuilder stream2Builder) {
        return new LongStreamBuilder(
                new BinaryMapEventStream.BinaryMapToLongEventStream<>(
                        eventStream, stream2Builder.eventStream, int2IntFunction)
        );
    }

    public <T> EventStreamBuilder<T> mapOnNotify(T target){
        return new EventStreamBuilder<>(new MapOnNotifyEventStream<>(eventStream, target));
    }

    public EventStreamBuilder<Long> box(){
        return mapToObj(Long::valueOf);
    }

    public <R> EventStreamBuilder<R> mapToObj(LambdaReflection.SerializableLongFunction<R> int2IntFunction) {
        return new EventStreamBuilder<>(new MapEventStream.MapLong2RefEventStream<>(eventStream, int2IntFunction));
    }

    public IntStreamBuilder mapToInt(LambdaReflection.SerializableLongToIntFunction int2IntFunction) {
        return new IntStreamBuilder(new MapEventStream.MapLong2ToIntEventStream(eventStream, int2IntFunction));
    }

    public DoubleStreamBuilder mapToDouble(LambdaReflection.SerializableLongToDoubleFunction int2IntFunction) {
        return new DoubleStreamBuilder(new MapEventStream.MapLong2ToDoubleEventStream(eventStream, int2IntFunction));
    }

    //OUTPUTS - START
    public LongStreamBuilder notify(Object target) {
        SepContext.service().add(target);
        return new LongStreamBuilder(new NotifyEventStream.LongNotifyEventStream(eventStream, target));
    }

    public LongStreamBuilder push(SerializableLongConsumer pushFunction) {
        SepContext.service().add(pushFunction.captured()[0]);
        return new LongStreamBuilder(new PushEventStream.LongPushEventStream(eventStream, pushFunction));
    }

    //META-DATA
    public LongStreamBuilder id(String nodeId){
        SepContext.service().add(eventStream, nodeId);
        return this;
    }
}
