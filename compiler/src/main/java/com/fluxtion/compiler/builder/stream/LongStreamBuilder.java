package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.SepContext;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiLongFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableLongConsumer;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableLongFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableLongUnaryOperator;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.BinaryMapEventStream;
import com.fluxtion.runtime.stream.EventStream.LongEventStream;
import com.fluxtion.runtime.stream.FilterEventStream;
import com.fluxtion.runtime.stream.MapEventStream;
import com.fluxtion.runtime.stream.MapOnNotifyEventStream;
import com.fluxtion.runtime.stream.NotifyEventStream;
import com.fluxtion.runtime.stream.PeekEventStream;
import com.fluxtion.runtime.stream.PushEventStream;
import com.fluxtion.runtime.stream.aggregate.AggregateLongStream;
import com.fluxtion.runtime.stream.aggregate.BaseLongSlidingWindowFunction;
import com.fluxtion.runtime.stream.helpers.DefaultValue;
import com.fluxtion.runtime.stream.helpers.Peekers;

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

    public <F extends BaseLongSlidingWindowFunction<F>> LongStreamBuilder aggregate(
            SerializableSupplier<F> aggregateFunction){
        return new LongStreamBuilder( new AggregateLongStream<>(eventStream, aggregateFunction));
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

    public LongStreamBuilder peek(LambdaReflection.SerializableConsumer<Long> peekFunction) {
        return new LongStreamBuilder(new PeekEventStream.LongPeekEventStream(eventStream, peekFunction));
    }

    public LongStreamBuilder console(String in){
        return peek(Peekers.console(in));
    }

    //META-DATA
    public LongStreamBuilder id(String nodeId){
        SepContext.service().add(eventStream, nodeId);
        return this;
    }
}
