package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.output.SinkPublisher;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiLongFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiLongPredicate;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableLongConsumer;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableLongFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableLongUnaryOperator;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.EventStream.LongEventStream;
import com.fluxtion.runtime.stream.LongAggregateFunction;
import com.fluxtion.runtime.stream.LongFlowSupplier;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import com.fluxtion.runtime.stream.aggregate.AggregateLongStream;
import com.fluxtion.runtime.stream.aggregate.TimedSlidingWindowStream;
import com.fluxtion.runtime.stream.aggregate.TumblingWindowStream.TumblingLongWindowStream;
import com.fluxtion.runtime.stream.helpers.DefaultValue;
import com.fluxtion.runtime.stream.helpers.Peekers;
import com.fluxtion.runtime.stream.impl.BinaryMapEventStream;
import com.fluxtion.runtime.stream.impl.FilterDynamicEventStream;
import com.fluxtion.runtime.stream.impl.FilterEventStream;
import com.fluxtion.runtime.stream.impl.MapEventStream;
import com.fluxtion.runtime.stream.impl.MapOnNotifyEventStream;
import com.fluxtion.runtime.stream.impl.NotifyEventStream;
import com.fluxtion.runtime.stream.impl.PeekEventStream;
import com.fluxtion.runtime.stream.impl.PushEventStream;
import com.fluxtion.runtime.stream.impl.WrappingEventSupplier.WrappingLongEventSupplier;

public class LongStreamBuilder implements EventSupplierAccessor<LongFlowSupplier> {

    final LongEventStream eventStream;

    LongStreamBuilder(LongEventStream eventStream) {
        EventProcessorBuilderService.service().add(eventStream);
        this.eventStream = eventStream;
    }

    public LongFlowSupplier runtimeSupplier() {
        return EventProcessorBuilderService.service().add(new WrappingLongEventSupplier(eventStream));
    }

    //TRIGGERS - START
    public LongStreamBuilder updateTrigger(Object updateTrigger) {
        Object source = StreamHelper.getSource(updateTrigger);
        if (eventStream instanceof TriggeredEventStream) {
            TriggeredEventStream triggeredEventStream = (TriggeredEventStream) eventStream;
            triggeredEventStream.setUpdateTriggerNode(source);
        }
        return this;
    }

    public LongStreamBuilder publishTrigger(Object publishTrigger) {
        Object source = StreamHelper.getSource(publishTrigger);
        if (eventStream instanceof TriggeredEventStream) {
            TriggeredEventStream triggeredEventStream = (TriggeredEventStream) eventStream;
            triggeredEventStream.setPublishTriggerNode(source);
        }
        return this;
    }

    public LongStreamBuilder publishTriggerOverride(Object publishTrigger) {
        Object source = StreamHelper.getSource(publishTrigger);
        if (eventStream instanceof TriggeredEventStream) {
            TriggeredEventStream triggeredEventStream = (TriggeredEventStream) eventStream;
            triggeredEventStream.setPublishTriggerOverrideNode(source);
        }
        return this;
    }

    public LongStreamBuilder resetTrigger(Object resetTrigger) {
        Object source = StreamHelper.getSource(resetTrigger);
        if (eventStream instanceof TriggeredEventStream) {
            TriggeredEventStream triggeredEventStream = (TriggeredEventStream) eventStream;
            triggeredEventStream.setResetTriggerNode(source);
        }
        return this;
    }

    public LongStreamBuilder filter(SerializableLongFunction<Boolean> filterFunction) {
        return new LongStreamBuilder(new FilterEventStream.LongFilterEventStream(eventStream, filterFunction));
    }

    public <S> LongStreamBuilder filter(
            SerializableBiLongPredicate predicate,
            LongStreamBuilder secondArgument) {
        return new LongStreamBuilder(
                new FilterDynamicEventStream.LongFilterDynamicEventStream(eventStream, secondArgument.eventStream, predicate));
    }

    public LongStreamBuilder defaultValue(long defaultValue) {
        return map(new DefaultValue.DefaultLong(defaultValue)::getOrDefault);
    }

    //PROCESSING - START
    public LongStreamBuilder map(SerializableLongUnaryOperator int2IntFunction) {
        return new LongStreamBuilder(new MapEventStream.MapLong2ToLongEventStream(eventStream, int2IntFunction));
    }

    public LongStreamBuilder mapBiFunction(SerializableBiLongFunction int2IntFunction, LongStreamBuilder stream2Builder) {
        return new LongStreamBuilder(
                new BinaryMapEventStream.BinaryMapToLongEventStream<>(
                        eventStream, stream2Builder.eventStream, int2IntFunction)
        );
    }

    public <F extends LongAggregateFunction<F>> LongStreamBuilder aggregate(
            SerializableSupplier<F> aggregateFunction) {
        return new LongStreamBuilder(new AggregateLongStream<>(eventStream, aggregateFunction));
    }

    public <F extends LongAggregateFunction<F>> LongStreamBuilder tumblingAggregate(
            SerializableSupplier<F> aggregateFunction, int bucketSizeMillis) {
        return new LongStreamBuilder(
                new TumblingLongWindowStream<>(eventStream, aggregateFunction, bucketSizeMillis));
    }

    public <F extends LongAggregateFunction<F>> LongStreamBuilder slidingAggregate(
            SerializableSupplier<F> aggregateFunction, int bucketSizeMillis, int numberOfBuckets) {
        return new LongStreamBuilder(
                new TimedSlidingWindowStream.TimedSlidingWindowLongStream<>(
                        eventStream,
                        aggregateFunction,
                        bucketSizeMillis,
                        numberOfBuckets));
    }

    public <T> EventStreamBuilder<T> mapOnNotify(T target) {
        return new EventStreamBuilder<>(new MapOnNotifyEventStream<>(eventStream, target));
    }

    public EventStreamBuilder<Long> box() {
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
        EventProcessorBuilderService.service().add(target);
        return new LongStreamBuilder(new NotifyEventStream.LongNotifyEventStream(eventStream, target));
    }

    public LongStreamBuilder sink(String sinkId) {
        return push(new SinkPublisher<>(sinkId)::publishLong);
    }

    public LongStreamBuilder push(SerializableLongConsumer pushFunction) {
        EventProcessorBuilderService.service().add(pushFunction.captured()[0]);
        return new LongStreamBuilder(new PushEventStream.LongPushEventStream(eventStream, pushFunction));
    }

    public LongStreamBuilder peek(LambdaReflection.SerializableConsumer<Long> peekFunction) {
        return new LongStreamBuilder(new PeekEventStream.LongPeekEventStream(eventStream, peekFunction));
    }

    public LongStreamBuilder console(String in) {
        peek(Peekers.console(in));
        return this;
    }

    public LongStreamBuilder console() {
        return console("{}");
    }

    //META-DATA
    public LongStreamBuilder id(String nodeId) {
        EventProcessorBuilderService.service().add(eventStream, nodeId);
        return this;
    }
}
