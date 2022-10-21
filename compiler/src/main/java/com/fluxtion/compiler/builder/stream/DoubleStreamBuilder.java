package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.EventProcessorConfigService;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiDoubleFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiDoublePredicate;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableDoubleConsumer;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableDoubleFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableDoubleToIntFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableDoubleToLongFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableDoubleUnaryOperator;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.BinaryMapEventStream;
import com.fluxtion.runtime.stream.EventStream.DoubleEventStream;
import com.fluxtion.runtime.stream.EventStream.DoubleEventSupplier;
import com.fluxtion.runtime.stream.EventStream.EventSupplierAccessor;
import com.fluxtion.runtime.stream.FilterDynamicEventStream;
import com.fluxtion.runtime.stream.FilterEventStream;
import com.fluxtion.runtime.stream.MapEventStream;
import com.fluxtion.runtime.stream.MapOnNotifyEventStream;
import com.fluxtion.runtime.stream.NotifyEventStream;
import com.fluxtion.runtime.stream.PeekEventStream;
import com.fluxtion.runtime.stream.PushEventStream;
import com.fluxtion.runtime.stream.SinkPublisher;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import com.fluxtion.runtime.stream.WrappingEventSupplier.WrappingDoubleEventSupplier;
import com.fluxtion.runtime.stream.aggregate.AggregateDoubleStream;
import com.fluxtion.runtime.stream.aggregate.DoubleAggregateFunction;
import com.fluxtion.runtime.stream.aggregate.TimedSlidingWindowStream;
import com.fluxtion.runtime.stream.aggregate.TumblingWindowStream.TumblingDoubleWindowStream;
import com.fluxtion.runtime.stream.helpers.DefaultValue;
import com.fluxtion.runtime.stream.helpers.Peekers;

public class DoubleStreamBuilder implements EventSupplierAccessor<DoubleEventSupplier> {

    final DoubleEventStream eventStream;

    DoubleStreamBuilder(DoubleEventStream eventStream) {
        EventProcessorConfigService.service().add(eventStream);
        this.eventStream = eventStream;
    }

    public DoubleEventSupplier getEventSupplier() {
        return EventProcessorConfigService.service().add(new WrappingDoubleEventSupplier(eventStream));
    }

    //TRIGGERS - START
    public DoubleStreamBuilder updateTrigger(Object updateTrigger) {
        Object source = StreamHelper.getSource(updateTrigger);
        if (eventStream instanceof TriggeredEventStream) {
            TriggeredEventStream triggeredEventStream = (TriggeredEventStream) eventStream;
            triggeredEventStream.setUpdateTriggerNode(source);
        }
        return this;
    }

    public DoubleStreamBuilder publishTrigger(Object publishTrigger) {
        Object source = StreamHelper.getSource(publishTrigger);
        if (eventStream instanceof TriggeredEventStream) {
            TriggeredEventStream triggeredEventStream = (TriggeredEventStream) eventStream;
            triggeredEventStream.setPublishTriggerNode(source);
        }
        return this;
    }

    public DoubleStreamBuilder publishTriggerOverride(Object publishTrigger) {
        Object source = StreamHelper.getSource(publishTrigger);
        if (eventStream instanceof TriggeredEventStream) {
            TriggeredEventStream triggeredEventStream = (TriggeredEventStream) eventStream;
            triggeredEventStream.setPublishTriggerOverrideNode(source);
        }
        return this;
    }

    public DoubleStreamBuilder resetTrigger(Object resetTrigger) {
        Object source = StreamHelper.getSource(resetTrigger);
        if (eventStream instanceof TriggeredEventStream) {
            TriggeredEventStream triggeredEventStream = (TriggeredEventStream) eventStream;
            triggeredEventStream.setResetTriggerNode(source);
        }
        return this;
    }

    public DoubleStreamBuilder filter(SerializableDoubleFunction<Boolean> filterFunction) {
        return new DoubleStreamBuilder(new FilterEventStream.DoubleFilterEventStream(eventStream, filterFunction));
    }

    public <S> DoubleStreamBuilder filter(
            SerializableBiDoublePredicate predicate,
            DoubleStreamBuilder secondArgument) {
        return new DoubleStreamBuilder(
                new FilterDynamicEventStream.DoubleFilterDynamicEventStream(eventStream, secondArgument.eventStream, predicate));
    }

    public DoubleStreamBuilder defaultValue(double defaultValue) {
        return map(new DefaultValue.DefaultDouble(defaultValue)::getOrDefault);
    }

    //PROCESSING - START
    public DoubleStreamBuilder map(SerializableDoubleUnaryOperator int2IntFunction) {
        return new DoubleStreamBuilder(new MapEventStream.MapDouble2ToDoubleEventStream(eventStream, int2IntFunction));
    }

    public DoubleStreamBuilder mapBiFunction(SerializableBiDoubleFunction int2IntFunction, DoubleStreamBuilder stream2Builder) {
        return new DoubleStreamBuilder(
                new BinaryMapEventStream.BinaryMapToDoubleEventStream<>(
                        eventStream, stream2Builder.eventStream, int2IntFunction)
        );
    }

    public <F extends DoubleAggregateFunction<F>> DoubleStreamBuilder aggregate(
            SerializableSupplier<F> aggregateFunction) {
        return new DoubleStreamBuilder(new AggregateDoubleStream<>(eventStream, aggregateFunction));
    }

    public <F extends DoubleAggregateFunction<F>> DoubleStreamBuilder tumblingAggregate(
            SerializableSupplier<F> aggregateFunction, int bucketSizeMillis) {
        return new DoubleStreamBuilder(
                new TumblingDoubleWindowStream<>(eventStream, aggregateFunction, bucketSizeMillis));
    }

    public <F extends DoubleAggregateFunction<F>> DoubleStreamBuilder slidingAggregate(
            SerializableSupplier<F> aggregateFunction, int bucketSizeMillis, int numberOfBuckets) {
        return new DoubleStreamBuilder(
                new TimedSlidingWindowStream.TimedSlidingWindowDoubleStream<>(
                        eventStream,
                        aggregateFunction,
                        bucketSizeMillis,
                        numberOfBuckets));
    }

    public <T> EventStreamBuilder<T> mapOnNotify(T target) {
        return new EventStreamBuilder<>(new MapOnNotifyEventStream<>(eventStream, target));
    }

    public EventStreamBuilder<Double> box() {
        return mapToObj(Double::valueOf);
    }

    public <R> EventStreamBuilder<R> mapToObj(SerializableDoubleFunction<R> int2IntFunction) {
        return new EventStreamBuilder<>(new MapEventStream.MapDouble2RefEventStream<>(eventStream, int2IntFunction));
    }

    public IntStreamBuilder mapToInt(SerializableDoubleToIntFunction int2IntFunction) {
        return new IntStreamBuilder(new MapEventStream.MapDouble2ToIntEventStream(eventStream, int2IntFunction));
    }

    public LongStreamBuilder mapToLong(SerializableDoubleToLongFunction int2IntFunction) {
        return new LongStreamBuilder(new MapEventStream.MapDouble2ToLongEventStream(eventStream, int2IntFunction));
    }

    //OUTPUTS - START
    public DoubleStreamBuilder notify(Object target) {
        EventProcessorConfigService.service().add(target);
        return new DoubleStreamBuilder(new NotifyEventStream.DoubleNotifyEventStream(eventStream, target));
    }

    public DoubleStreamBuilder sink(String sinkId) {
        return push(new SinkPublisher<>(sinkId)::publishDouble);
    }

    public DoubleStreamBuilder push(SerializableDoubleConsumer pushFunction) {
//        EventProcessorConfigService.service().add(pushFunction.captured()[0]);
        return new DoubleStreamBuilder(new PushEventStream.DoublePushEventStream(eventStream, pushFunction));
    }

    public DoubleStreamBuilder peek(LambdaReflection.SerializableConsumer<Double> peekFunction) {
        return new DoubleStreamBuilder(new PeekEventStream.DoublePeekEventStream(eventStream, peekFunction));
    }

    public DoubleStreamBuilder console(String in) {
        return peek(Peekers.console(in));
    }

    public DoubleStreamBuilder console() {
        return console("{}");
    }

    //META-DATA
    public DoubleStreamBuilder id(String nodeId) {
        EventProcessorConfigService.service().add(eventStream, nodeId);
        return this;
    }

}
