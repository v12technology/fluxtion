package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.SepContext;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.stream.BinaryMapEventStream;
import com.fluxtion.runtime.stream.EventStream.IntEventStream;
import com.fluxtion.runtime.stream.FilterEventStream;
import com.fluxtion.runtime.stream.MapEventStream;
import com.fluxtion.runtime.stream.MapOnNotifyEventStream;
import com.fluxtion.runtime.stream.NotifyEventStream;
import com.fluxtion.runtime.stream.PeekEventStream;
import com.fluxtion.runtime.stream.PushEventStream;
import com.fluxtion.runtime.stream.aggregate.AggregateIntStream;
import com.fluxtion.runtime.stream.aggregate.AggregateIntStream.TumblingIntWindowStream;
import com.fluxtion.runtime.stream.aggregate.BaseIntSlidingWindowFunction;
import com.fluxtion.runtime.stream.aggregate.BucketedSlidingWindowedFunction;
import com.fluxtion.runtime.stream.aggregate.TimedSlidingWindowStream;
import com.fluxtion.runtime.stream.helpers.DefaultValue;
import com.fluxtion.runtime.stream.helpers.Peekers;

import static com.fluxtion.runtime.partition.LambdaReflection.*;

public class IntStreamBuilder {

    final IntEventStream eventStream;

    IntStreamBuilder(IntEventStream eventStream) {
        SepContext.service().add(eventStream);
        this.eventStream = eventStream;
    }

    //TRIGGERS - START
    public IntStreamBuilder updateTrigger(Object updateTrigger) {
        eventStream.setUpdateTriggerNode(StreamHelper.getSource(updateTrigger));
        return this;
    }

    public IntStreamBuilder publishTrigger(Object publishTrigger) {
        eventStream.setPublishTriggerNode(StreamHelper.getSource(publishTrigger));
        return this;
    }

    public IntStreamBuilder resetTrigger(Object resetTrigger) {
        eventStream.setResetTriggerNode(StreamHelper.getSource(resetTrigger));
        return this;
    }

    public IntStreamBuilder filter(SerializableIntFunction<Boolean> filterFunction) {
        return new IntStreamBuilder(new FilterEventStream.IntFilterEventStream(eventStream, filterFunction));
    }

    public IntStreamBuilder defaultValue(int defaultValue) {
        return map(new DefaultValue.DefaultInt(defaultValue)::getOrDefault);
    }

    //PROCESSING - START
    public IntStreamBuilder map(SerializableIntUnaryOperator int2IntFunction) {
        return new IntStreamBuilder(new MapEventStream.MapInt2ToIntEventStream(eventStream, int2IntFunction));
    }

    public IntStreamBuilder map(SerializableBiIntFunction int2IntFunction, IntStreamBuilder stream2Builder) {
        return new IntStreamBuilder(
                new BinaryMapEventStream.BinaryMapToIntEventStream<>(
                        eventStream, stream2Builder.eventStream, int2IntFunction)
        );
    }

    public <F extends BaseIntSlidingWindowFunction<F>> IntStreamBuilder aggregate(
            SerializableSupplier<F> aggregateFunction){
        return new IntStreamBuilder( new AggregateIntStream<>(eventStream, aggregateFunction));
    }

    public <F extends BaseIntSlidingWindowFunction<F>> IntStreamBuilder tumblingAggregate(
            SerializableSupplier<F> aggregateFunction, int bucketSizeMillis){
        return new IntStreamBuilder(
                new TumblingIntWindowStream<>(eventStream, aggregateFunction, bucketSizeMillis));
    }

    public <F extends BaseIntSlidingWindowFunction<F>> IntStreamBuilder slidingAggregate(
            SerializableSupplier<F> aggregateFunction, int bucketSizeMillis, int numberOfBuckets){
        return new IntStreamBuilder(
                new TimedSlidingWindowStream.TimedSlidingWindowIntStream<>(
                        eventStream,
                        aggregateFunction,
                        bucketSizeMillis,
                        numberOfBuckets));
    }

    public <T> EventStreamBuilder<T> mapOnNotify(T target){
        return new EventStreamBuilder<>(new MapOnNotifyEventStream<>(eventStream, target));
    }

    public EventStreamBuilder<Integer> box(){
        return mapToObj(Integer::valueOf);
    }

    public <R> EventStreamBuilder<R> mapToObj(SerializableIntFunction<R> int2IntFunction) {
        return new EventStreamBuilder<>(new MapEventStream.MapInt2RefEventStream<>(eventStream, int2IntFunction));
    }

    public DoubleStreamBuilder mapToDouble(SerializableIntToDoubleFunction int2IntFunction) {
        return new DoubleStreamBuilder(new MapEventStream.MapInt2ToDoubleEventStream(eventStream, int2IntFunction));
    }

    public LongStreamBuilder mapToLong(SerializableIntToLongFunction int2IntFunction) {
        return new LongStreamBuilder(new MapEventStream.MapInt2ToLongEventStream(eventStream, int2IntFunction));
    }

    //OUTPUTS - START
    public IntStreamBuilder notify(Object target) {
        SepContext.service().add(target);
        return new IntStreamBuilder(new NotifyEventStream.IntNotifyEventStream(eventStream, target));
    }

    public IntStreamBuilder push(SerializableIntConsumer pushFunction) {
        if (pushFunction.captured().length > 0) {
            SepContext.service().add(pushFunction.captured()[0]);
        }
        return new IntStreamBuilder(new PushEventStream.IntPushEventStream(eventStream, pushFunction));
    }

    public IntStreamBuilder peek(SerializableConsumer<Integer> peekFunction) {
        return new IntStreamBuilder(new PeekEventStream.IntPeekEventStream(eventStream, peekFunction));
    }

    public IntStreamBuilder console(String in){
        return peek(Peekers.console(in));
    }

    //META-DATA
    public IntStreamBuilder id(String nodeId){
        SepContext.service().add(eventStream, nodeId);
         return this;
    }

}
