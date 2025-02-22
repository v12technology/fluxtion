package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.generation.GenerationContext;
import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.dataflow.DoubleFlowFunction;
import com.fluxtion.runtime.dataflow.DoubleFlowSupplier;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateDoubleFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.FixSizedSlidingWindow;
import com.fluxtion.runtime.dataflow.aggregate.function.TimedSlidingWindow;
import com.fluxtion.runtime.dataflow.aggregate.function.TumblingWindow.TumblingDoubleWindowStream;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.AggregateDoubleFlowFunctionWrapper;
import com.fluxtion.runtime.dataflow.function.BinaryMapFlowFunction.BinaryMapToDoubleFlowFunction;
import com.fluxtion.runtime.dataflow.function.FilterDynamicFlowFunction.DoubleFilterDynamicFlowFunction;
import com.fluxtion.runtime.dataflow.function.FilterFlowFunction.DoubleFilterFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapDouble2RefFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapDouble2ToDoubleFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapDouble2ToIntFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapDouble2ToLongFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapOnNotifyFlowFunction;
import com.fluxtion.runtime.dataflow.function.NotifyFlowFunction.DoubleNotifyFlowFunction;
import com.fluxtion.runtime.dataflow.function.PeekFlowFunction.DoublePeekFlowFunction;
import com.fluxtion.runtime.dataflow.function.PushFlowFunction.DoublePushFlowFunction;
import com.fluxtion.runtime.dataflow.helpers.DefaultValue;
import com.fluxtion.runtime.dataflow.helpers.Peekers;
import com.fluxtion.runtime.output.SinkPublisher;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.partition.LambdaReflection.*;

public class DoubleFlowBuilder implements FlowDataSupplier<DoubleFlowSupplier> {

    final DoubleFlowFunction eventStream;

    DoubleFlowBuilder(DoubleFlowFunction eventStream) {
        GenerationContext.inLineContext();
        EventProcessorBuilderService.service().add(eventStream);
        this.eventStream = eventStream;
    }

    public DoubleFlowSupplier flowSupplier() {
        return eventStream;
    }

    public DoubleFlowBuilder parallel() {
        eventStream.parallel();
        return this;
    }

    //TRIGGERS - START
    public DoubleFlowBuilder updateTrigger(Object updateTrigger) {
        Object source = StreamHelper.getSource(updateTrigger);
        if (eventStream instanceof TriggeredFlowFunction) {
            TriggeredFlowFunction triggeredEventStream = (TriggeredFlowFunction) eventStream;
            triggeredEventStream.setUpdateTriggerNode(source);
        }
        return this;
    }

    public DoubleFlowBuilder publishTrigger(Object publishTrigger) {
        Object source = StreamHelper.getSource(publishTrigger);
        if (eventStream instanceof TriggeredFlowFunction) {
            TriggeredFlowFunction triggeredEventStream = (TriggeredFlowFunction) eventStream;
            triggeredEventStream.setPublishTriggerNode(source);
        }
        return this;
    }

    public DoubleFlowBuilder publishTriggerOverride(Object publishTrigger) {
        Object source = StreamHelper.getSource(publishTrigger);
        if (eventStream instanceof TriggeredFlowFunction) {
            TriggeredFlowFunction triggeredEventStream = (TriggeredFlowFunction) eventStream;
            triggeredEventStream.setPublishTriggerOverrideNode(source);
        }
        return this;
    }

    public DoubleFlowBuilder resetTrigger(Object resetTrigger) {
        Object source = StreamHelper.getSource(resetTrigger);
        if (eventStream instanceof TriggeredFlowFunction) {
            TriggeredFlowFunction triggeredEventStream = (TriggeredFlowFunction) eventStream;
            triggeredEventStream.setResetTriggerNode(source);
        }
        return this;
    }

    public DoubleFlowBuilder filter(SerializableDoubleFunction<Boolean> filterFunction) {
        return new DoubleFlowBuilder(new DoubleFilterFlowFunction(eventStream, filterFunction));
    }

    public <S> DoubleFlowBuilder filter(
            SerializableBiDoublePredicate predicate,
            DoubleFlowBuilder secondArgument) {
        return new DoubleFlowBuilder(
                new DoubleFilterDynamicFlowFunction(eventStream, secondArgument.eventStream, predicate));
    }

    public DoubleFlowBuilder defaultValue(double defaultValue) {
        return map(new DefaultValue.DefaultDouble(defaultValue)::getOrDefault);
    }

    //PROCESSING - START
    public DoubleFlowBuilder map(SerializableDoubleUnaryOperator int2IntFunction) {
        return new DoubleFlowBuilder(new MapDouble2ToDoubleFlowFunction(eventStream, int2IntFunction));
    }

    public DoubleFlowBuilder mapBiFunction(SerializableBiDoubleFunction int2IntFunction, DoubleFlowBuilder stream2Builder) {
        return new DoubleFlowBuilder(
                new BinaryMapToDoubleFlowFunction<>(
                        eventStream, stream2Builder.eventStream, int2IntFunction)
        );
    }

    public <F extends AggregateDoubleFlowFunction<F>> DoubleFlowBuilder aggregate(
            SerializableSupplier<F> aggregateFunction) {
        return new DoubleFlowBuilder(new AggregateDoubleFlowFunctionWrapper<>(eventStream, aggregateFunction));
    }

    public <F extends AggregateDoubleFlowFunction<F>> DoubleFlowBuilder tumblingAggregate(
            SerializableSupplier<F> aggregateFunction, int bucketSizeMillis) {
        return new DoubleFlowBuilder(
                new TumblingDoubleWindowStream<>(eventStream, aggregateFunction, bucketSizeMillis));
    }

    public <F extends AggregateDoubleFlowFunction<F>> DoubleFlowBuilder slidingAggregate(
            SerializableSupplier<F> aggregateFunction, int bucketSizeMillis, int numberOfBuckets) {
        return new DoubleFlowBuilder(
                new TimedSlidingWindow.TimedSlidingWindowDoubleStream<>(
                        eventStream,
                        aggregateFunction,
                        bucketSizeMillis,
                        numberOfBuckets));
    }

    public <F extends AggregateDoubleFlowFunction<F>> DoubleFlowBuilder slidingAggregateByCount(
            SerializableSupplier<F> aggregateFunction, int elementsInWindow) {
        return new DoubleFlowBuilder(
                new FixSizedSlidingWindow.FixSizedSlidingDoubleWindow<>(eventStream, aggregateFunction, elementsInWindow));
    }

    public <T> FlowBuilder<T> mapOnNotify(T target) {
        return new FlowBuilder<>(new MapOnNotifyFlowFunction<>(eventStream, target));
    }

    public FlowBuilder<Double> box() {
        return mapToObj(Double::valueOf);
    }

    public <R> FlowBuilder<R> mapToObj(SerializableDoubleFunction<R> int2IntFunction) {
        return new FlowBuilder<>(new MapDouble2RefFlowFunction<>(eventStream, int2IntFunction));
    }

    public IntFlowBuilder mapToInt(SerializableDoubleToIntFunction int2IntFunction) {
        return new IntFlowBuilder(new MapDouble2ToIntFlowFunction(eventStream, int2IntFunction));
    }

    public LongFlowBuilder mapToLong(SerializableDoubleToLongFunction int2IntFunction) {
        return new LongFlowBuilder(new MapDouble2ToLongFlowFunction(eventStream, int2IntFunction));
    }

    //OUTPUTS - START
    public DoubleFlowBuilder notify(Object target) {
        EventProcessorBuilderService.service().add(target);
        return new DoubleFlowBuilder(new DoubleNotifyFlowFunction(eventStream, target));
    }

    public DoubleFlowBuilder sink(String sinkId) {
        return push(new SinkPublisher<>(sinkId)::publishDouble);
    }

    public final DoubleFlowBuilder push(SerializableDoubleConsumer... pushFunctions) {
        DoubleFlowBuilder target = null;
        for (SerializableDoubleConsumer pushFunction : pushFunctions) {
            target = new DoubleFlowBuilder(new DoublePushFlowFunction(eventStream, pushFunction));
        }
        return target;
    }

    public DoubleFlowBuilder peek(LambdaReflection.SerializableConsumer<Double> peekFunction) {
        return new DoubleFlowBuilder(new DoublePeekFlowFunction(eventStream, peekFunction));
    }

    public DoubleFlowBuilder console(String in) {
        peek(Peekers.console(in));
        return this;
    }

    public DoubleFlowBuilder console() {
        return console("{}");
    }

    //META-DATA
    public DoubleFlowBuilder id(String nodeId) {
        EventProcessorBuilderService.service().add(eventStream, nodeId);
        return this;
    }

}
