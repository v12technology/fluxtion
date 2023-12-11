package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.dataflow.IntFlowFunction;
import com.fluxtion.runtime.dataflow.IntFlowSupplier;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateIntFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.TimedSlidingWindow;
import com.fluxtion.runtime.dataflow.aggregate.function.TumblingWindow.TumblingIntWindowStream;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.AggregateIntFlowFunctionWrapper;
import com.fluxtion.runtime.dataflow.function.BinaryMapFlowFunction.BinaryMapToIntFlowFunction;
import com.fluxtion.runtime.dataflow.function.FilterDynamicFlowFunction.IntFilterDynamicFlowFunction;
import com.fluxtion.runtime.dataflow.function.FilterFlowFunction.IntFilterFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapInt2RefFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapInt2ToDoubleFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapInt2ToIntFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapInt2ToLongFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapOnNotifyFlowFunction;
import com.fluxtion.runtime.dataflow.function.NotifyFlowFunction.IntNotifyFlowFunction;
import com.fluxtion.runtime.dataflow.function.PeekFlowFunction.IntPeekFlowFunction;
import com.fluxtion.runtime.dataflow.function.PushFlowFunction.IntPushFlowFunction;
import com.fluxtion.runtime.dataflow.helpers.DefaultValue;
import com.fluxtion.runtime.dataflow.helpers.Peekers;
import com.fluxtion.runtime.output.SinkPublisher;

import static com.fluxtion.runtime.partition.LambdaReflection.*;

public class IntFlowBuilder implements FlowDataSupplier<IntFlowSupplier> {

    final IntFlowFunction eventStream;

    IntFlowBuilder(IntFlowFunction eventStream) {
        EventProcessorBuilderService.service().add(eventStream);
        this.eventStream = eventStream;
    }

    @Override
    public IntFlowSupplier flowSupplier() {
        return eventStream;
    }

    public IntFlowBuilder parallel() {
        eventStream.parallel();
        return this;
    }

    //TRIGGERS - START
    public IntFlowBuilder updateTrigger(Object updateTrigger) {
        Object source = StreamHelper.getSource(updateTrigger);
        if (eventStream instanceof TriggeredFlowFunction) {
            TriggeredFlowFunction triggeredEventStream = (TriggeredFlowFunction) eventStream;
            triggeredEventStream.setUpdateTriggerNode(source);
        }
        return this;
    }

    public IntFlowBuilder publishTrigger(Object publishTrigger) {
        Object source = StreamHelper.getSource(publishTrigger);
        if (eventStream instanceof TriggeredFlowFunction) {
            TriggeredFlowFunction triggeredEventStream = (TriggeredFlowFunction) eventStream;
            triggeredEventStream.setPublishTriggerNode(source);
        }
        return this;
    }

    public IntFlowBuilder publishTriggerOverride(Object publishTrigger) {
        Object source = StreamHelper.getSource(publishTrigger);
        if (eventStream instanceof TriggeredFlowFunction) {
            TriggeredFlowFunction triggeredEventStream = (TriggeredFlowFunction) eventStream;
            triggeredEventStream.setPublishTriggerOverrideNode(source);
        }
        return this;
    }

    public IntFlowBuilder resetTrigger(Object resetTrigger) {
        Object source = StreamHelper.getSource(resetTrigger);
        if (eventStream instanceof TriggeredFlowFunction) {
            TriggeredFlowFunction triggeredEventStream = (TriggeredFlowFunction) eventStream;
            triggeredEventStream.setResetTriggerNode(source);
        }
        return this;
    }

    public IntFlowBuilder filter(SerializableIntFunction<Boolean> filterFunction) {
        return new IntFlowBuilder(new IntFilterFlowFunction(eventStream, filterFunction));
    }

    public <S> IntFlowBuilder filter(
            SerializableBiIntPredicate predicate,
            IntFlowBuilder secondArgument) {
        return new IntFlowBuilder(
                new IntFilterDynamicFlowFunction(eventStream, secondArgument.eventStream, predicate));
    }

    public IntFlowBuilder defaultValue(int defaultValue) {
        return map(new DefaultValue.DefaultInt(defaultValue)::getOrDefault);
    }

    //PROCESSING - START
    public IntFlowBuilder map(SerializableIntUnaryOperator int2IntFunction) {
        return new IntFlowBuilder(new MapInt2ToIntFlowFunction(eventStream, int2IntFunction));
    }

    public IntFlowBuilder map(SerializableBiIntFunction int2IntFunction, IntFlowBuilder stream2Builder) {
        return new IntFlowBuilder(
                new BinaryMapToIntFlowFunction<>(
                        eventStream, stream2Builder.eventStream, int2IntFunction)
        );
    }

    public <F extends AggregateIntFlowFunction<F>> IntFlowBuilder aggregate(
            SerializableSupplier<F> aggregateFunction) {
        return new IntFlowBuilder(new AggregateIntFlowFunctionWrapper<>(eventStream, aggregateFunction));
    }

    public <F extends AggregateIntFlowFunction<F>> IntFlowBuilder tumblingAggregate(
            SerializableSupplier<F> aggregateFunction, int bucketSizeMillis) {
        return new IntFlowBuilder(
                new TumblingIntWindowStream<>(eventStream, aggregateFunction, bucketSizeMillis));
    }

    public <F extends AggregateIntFlowFunction<F>> IntFlowBuilder slidingAggregate(
            SerializableSupplier<F> aggregateFunction, int bucketSizeMillis, int numberOfBuckets) {
        return new IntFlowBuilder(
                new TimedSlidingWindow.TimedSlidingWindowIntStream<>(
                        eventStream,
                        aggregateFunction,
                        bucketSizeMillis,
                        numberOfBuckets));
    }

    public <T> FlowBuilder<T> mapOnNotify(T target) {
        return new FlowBuilder<>(new MapOnNotifyFlowFunction<>(eventStream, target));
    }

    public FlowBuilder<Integer> box() {
        return mapToObj(Integer::valueOf);
    }

    public <R> FlowBuilder<R> mapToObj(SerializableIntFunction<R> int2IntFunction) {
        return new FlowBuilder<>(new MapInt2RefFlowFunction<>(eventStream, int2IntFunction));
    }

    public DoubleFlowBuilder mapToDouble(SerializableIntToDoubleFunction int2IntFunction) {
        return new DoubleFlowBuilder(new MapInt2ToDoubleFlowFunction(eventStream, int2IntFunction));
    }

    public LongFlowBuilder mapToLong(SerializableIntToLongFunction int2IntFunction) {
        return new LongFlowBuilder(new MapInt2ToLongFlowFunction(eventStream, int2IntFunction));
    }

    //OUTPUTS - START
    public IntFlowBuilder notify(Object target) {
        EventProcessorBuilderService.service().add(target);
        return new IntFlowBuilder(new IntNotifyFlowFunction(eventStream, target));
    }

    public IntFlowBuilder sink(String sinkId) {
        return push(new SinkPublisher<>(sinkId)::publishInt);
    }

    public IntFlowBuilder push(SerializableIntConsumer pushFunction) {
        if (pushFunction.captured().length > 0) {
            EventProcessorBuilderService.service().add(pushFunction.captured()[0]);
        }
        return new IntFlowBuilder(new IntPushFlowFunction(eventStream, pushFunction));
    }

    public IntFlowBuilder peek(SerializableConsumer<Integer> peekFunction) {
        return new IntFlowBuilder(new IntPeekFlowFunction(eventStream, peekFunction));
    }

    public IntFlowBuilder console(String in) {
        peek(Peekers.console(in));
        return this;
    }

    public IntFlowBuilder console() {
        return console("{}");
    }

    //META-DATA
    public IntFlowBuilder id(String nodeId) {
        EventProcessorBuilderService.service().add(eventStream, nodeId);
        return this;
    }

}
