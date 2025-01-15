package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.dataflow.LongFlowFunction;
import com.fluxtion.runtime.dataflow.LongFlowSupplier;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateLongFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.FixSizedSlidingWindow;
import com.fluxtion.runtime.dataflow.aggregate.function.TimedSlidingWindow;
import com.fluxtion.runtime.dataflow.aggregate.function.TumblingWindow.TumblingLongWindowStream;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.AggregateLongFlowFunctionWrapper;
import com.fluxtion.runtime.dataflow.function.BinaryMapFlowFunction.BinaryMapToLongFlowFunction;
import com.fluxtion.runtime.dataflow.function.FilterDynamicFlowFunction.LongFilterDynamicFlowFunction;
import com.fluxtion.runtime.dataflow.function.FilterFlowFunction.LongFilterFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapLong2RefFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapLong2ToDoubleFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapLong2ToIntFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapLong2ToLongFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapOnNotifyFlowFunction;
import com.fluxtion.runtime.dataflow.function.NotifyFlowFunction.LongNotifyFlowFunction;
import com.fluxtion.runtime.dataflow.function.PeekFlowFunction.LongPeekFlowFunction;
import com.fluxtion.runtime.dataflow.function.PushFlowFunction.LongPushFlowFunction;
import com.fluxtion.runtime.dataflow.helpers.DefaultValue;
import com.fluxtion.runtime.dataflow.helpers.Peekers;
import com.fluxtion.runtime.output.SinkPublisher;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.partition.LambdaReflection.*;

public class LongFlowBuilder implements FlowDataSupplier<LongFlowSupplier> {

    final LongFlowFunction eventStream;

    LongFlowBuilder(LongFlowFunction eventStream) {
        EventProcessorBuilderService.service().add(eventStream);
        this.eventStream = eventStream;
    }

    public LongFlowSupplier flowSupplier() {
        return eventStream;
    }

    public LongFlowBuilder parallel() {
        eventStream.parallel();
        return this;
    }

    //TRIGGERS - START
    public LongFlowBuilder updateTrigger(Object updateTrigger) {
        Object source = StreamHelper.getSource(updateTrigger);
        if (eventStream instanceof TriggeredFlowFunction) {
            TriggeredFlowFunction triggeredEventStream = (TriggeredFlowFunction) eventStream;
            triggeredEventStream.setUpdateTriggerNode(source);
        }
        return this;
    }

    public LongFlowBuilder publishTrigger(Object publishTrigger) {
        Object source = StreamHelper.getSource(publishTrigger);
        if (eventStream instanceof TriggeredFlowFunction) {
            TriggeredFlowFunction triggeredEventStream = (TriggeredFlowFunction) eventStream;
            triggeredEventStream.setPublishTriggerNode(source);
        }
        return this;
    }

    public LongFlowBuilder publishTriggerOverride(Object publishTrigger) {
        Object source = StreamHelper.getSource(publishTrigger);
        if (eventStream instanceof TriggeredFlowFunction) {
            TriggeredFlowFunction triggeredEventStream = (TriggeredFlowFunction) eventStream;
            triggeredEventStream.setPublishTriggerOverrideNode(source);
        }
        return this;
    }

    public LongFlowBuilder resetTrigger(Object resetTrigger) {
        Object source = StreamHelper.getSource(resetTrigger);
        if (eventStream instanceof TriggeredFlowFunction) {
            TriggeredFlowFunction triggeredEventStream = (TriggeredFlowFunction) eventStream;
            triggeredEventStream.setResetTriggerNode(source);
        }
        return this;
    }

    public LongFlowBuilder filter(SerializableLongFunction<Boolean> filterFunction) {
        return new LongFlowBuilder(new LongFilterFlowFunction(eventStream, filterFunction));
    }

    public <S> LongFlowBuilder filter(
            SerializableBiLongPredicate predicate,
            LongFlowBuilder secondArgument) {
        return new LongFlowBuilder(
                new LongFilterDynamicFlowFunction(eventStream, secondArgument.eventStream, predicate));
    }

    public LongFlowBuilder defaultValue(long defaultValue) {
        return map(new DefaultValue.DefaultLong(defaultValue)::getOrDefault);
    }

    //PROCESSING - START
    public LongFlowBuilder map(SerializableLongUnaryOperator int2IntFunction) {
        return new LongFlowBuilder(new MapLong2ToLongFlowFunction(eventStream, int2IntFunction));
    }

    public LongFlowBuilder mapBiFunction(SerializableBiLongFunction int2IntFunction, LongFlowBuilder stream2Builder) {
        return new LongFlowBuilder(
                new BinaryMapToLongFlowFunction<>(
                        eventStream, stream2Builder.eventStream, int2IntFunction)
        );
    }

    public <F extends AggregateLongFlowFunction<F>> LongFlowBuilder aggregate(
            SerializableSupplier<F> aggregateFunction) {
        return new LongFlowBuilder(new AggregateLongFlowFunctionWrapper<>(eventStream, aggregateFunction));
    }

    public <F extends AggregateLongFlowFunction<F>> LongFlowBuilder tumblingAggregate(
            SerializableSupplier<F> aggregateFunction, int bucketSizeMillis) {
        return new LongFlowBuilder(
                new TumblingLongWindowStream<>(eventStream, aggregateFunction, bucketSizeMillis));
    }

    public <F extends AggregateLongFlowFunction<F>> LongFlowBuilder slidingAggregate(
            SerializableSupplier<F> aggregateFunction, int bucketSizeMillis, int numberOfBuckets) {
        return new LongFlowBuilder(
                new TimedSlidingWindow.TimedSlidingWindowLongStream<>(
                        eventStream,
                        aggregateFunction,
                        bucketSizeMillis,
                        numberOfBuckets));
    }

    public <F extends AggregateLongFlowFunction<F>> LongFlowBuilder slidingAggregateByCount(
            SerializableSupplier<F> aggregateFunction, int elementsInWindow) {
        return new LongFlowBuilder(
                new FixSizedSlidingWindow.FixSizedSlidingLongWindow<>(eventStream, aggregateFunction, elementsInWindow));
    }

    public <T> FlowBuilder<T> mapOnNotify(T target) {
        return new FlowBuilder<>(new MapOnNotifyFlowFunction<>(eventStream, target));
    }

    public FlowBuilder<Long> box() {
        return mapToObj(Long::valueOf);
    }

    public <R> FlowBuilder<R> mapToObj(LambdaReflection.SerializableLongFunction<R> int2IntFunction) {
        return new FlowBuilder<>(new MapLong2RefFlowFunction<>(eventStream, int2IntFunction));
    }

    public IntFlowBuilder mapToInt(LambdaReflection.SerializableLongToIntFunction int2IntFunction) {
        return new IntFlowBuilder(new MapLong2ToIntFlowFunction(eventStream, int2IntFunction));
    }

    public DoubleFlowBuilder mapToDouble(LambdaReflection.SerializableLongToDoubleFunction int2IntFunction) {
        return new DoubleFlowBuilder(new MapLong2ToDoubleFlowFunction(eventStream, int2IntFunction));
    }

    //OUTPUTS - START
    public LongFlowBuilder notify(Object target) {
        EventProcessorBuilderService.service().add(target);
        return new LongFlowBuilder(new LongNotifyFlowFunction(eventStream, target));
    }

    public LongFlowBuilder sink(String sinkId) {
        return push(new SinkPublisher<>(sinkId)::publishLong);
    }

    public final LongFlowBuilder push(SerializableLongConsumer... pushFunctions) {
        LongFlowBuilder target = null;
        for (SerializableLongConsumer pushFunction : pushFunctions) {
            target = new LongFlowBuilder(new LongPushFlowFunction(eventStream, pushFunction));
        }
        return target;
    }

    public LongFlowBuilder peek(LambdaReflection.SerializableConsumer<Long> peekFunction) {
        return new LongFlowBuilder(new LongPeekFlowFunction(eventStream, peekFunction));
    }

    public LongFlowBuilder console(String in) {
        peek(Peekers.console(in));
        return this;
    }

    public LongFlowBuilder console() {
        return console("{}");
    }

    //META-DATA
    public LongFlowBuilder id(String nodeId) {
        EventProcessorBuilderService.service().add(eventStream, nodeId);
        return this;
    }
}
