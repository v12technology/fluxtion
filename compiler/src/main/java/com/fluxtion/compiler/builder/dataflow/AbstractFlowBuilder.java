package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.dataflow.function.FilterByPropertyDynamicFlowFunction;
import com.fluxtion.runtime.dataflow.function.FilterByPropertyFlowFunction;
import com.fluxtion.runtime.dataflow.function.FilterDynamicFlowFunction;
import com.fluxtion.runtime.dataflow.function.FilterFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapRef2RefFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapRef2ToDoubleFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapRef2ToIntFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapRef2ToLongFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapOnNotifyFlowFunction;
import com.fluxtion.runtime.dataflow.function.NotifyFlowFunction;
import com.fluxtion.runtime.dataflow.function.PeekFlowFunction;
import com.fluxtion.runtime.dataflow.function.PushFlowFunction;
import com.fluxtion.runtime.dataflow.helpers.InternalEventDispatcher;
import com.fluxtion.runtime.dataflow.helpers.Peekers;
import com.fluxtion.runtime.dataflow.helpers.Predicates.PredicateWrapper;
import com.fluxtion.runtime.output.SinkPublisher;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;

public abstract class AbstractFlowBuilder<T, B extends AbstractFlowBuilder<T, B>> {

    final TriggeredFlowFunction<T> eventStream;


    public AbstractFlowBuilder(TriggeredFlowFunction<T> eventStream) {
        this.eventStream = eventStream;
    }

    protected abstract B connect(TriggeredFlowFunction<T> stream);

    public B parallel() {
        eventStream.parallel();
        return identity();
    }

    protected abstract <R> AbstractFlowBuilder<R, ?> connectMap(TriggeredFlowFunction<R> stream);

    protected abstract B identity();

    //TRIGGERS - START
    public B updateTrigger(Object updateTrigger) {
        eventStream.setUpdateTriggerNode(StreamHelper.getSource(updateTrigger));
        return identity();
    }

    public B publishTrigger(Object publishTrigger) {
        eventStream.setPublishTriggerNode(StreamHelper.getSource(publishTrigger));
        return identity();
    }

    public B publishTriggerOverride(Object publishTrigger) {
        eventStream.setPublishTriggerOverrideNode(StreamHelper.getSource(publishTrigger));
        return identity();
    }

    public B resetTrigger(Object resetTrigger) {
        eventStream.setResetTriggerNode(StreamHelper.getSource(resetTrigger));
        return identity();
    }

    //FILTERS - START
    public B filter(SerializableFunction<T, Boolean> filterFunction) {
        return connect(new FilterFlowFunction<>(eventStream, filterFunction));
    }

    public B filter(SerializableSupplier<Boolean> filterFunction) {
        return filter(new PredicateWrapper(filterFunction)::test);
    }

    public <P> B filterByProperty(SerializableFunction<T, P> accessor, SerializableFunction<P, Boolean> filterFunction) {
        return connect(new FilterByPropertyFlowFunction<>(eventStream, accessor, filterFunction));
    }

    public <S> B filter(
            SerializableBiFunction<T, S, Boolean> predicate,
            FlowBuilder<S> secondArgument) {
        return connect(
                new FilterDynamicFlowFunction<>(eventStream, secondArgument.eventStream, predicate));
    }

    public <S> B filter(
            SerializableBiFunction<T, Integer, Boolean> predicate,
            IntFlowBuilder secondArgument) {
        return connect(
                new FilterDynamicFlowFunction<>(eventStream, secondArgument.eventStream, predicate));
    }

    public <S> B filter(
            SerializableBiFunction<T, Double, Boolean> predicate,
            DoubleFlowBuilder secondArgument) {
        return connect(
                new FilterDynamicFlowFunction<>(eventStream, secondArgument.eventStream, predicate));
    }

    public <S> B filter(
            SerializableBiFunction<T, Long, Boolean> predicate,
            LongFlowBuilder secondArgument) {
        return connect(
                new FilterDynamicFlowFunction<>(eventStream, secondArgument.eventStream, predicate));
    }

    public <P, S> B filterByProperty(
            SerializableBiFunction<P, S, Boolean> predicate,
            SerializableFunction<T, P> accessor,
            FlowBuilder<S> secondArgument) {
        return connect(
                new FilterByPropertyDynamicFlowFunction<>(eventStream, accessor, secondArgument.eventStream, predicate));
    }

    public <P, S> B filterByProperty(
            SerializableBiFunction<P, Integer, Boolean> predicate,
            SerializableFunction<T, P> accessor,
            IntFlowBuilder secondArgument) {
        return connect(
                new FilterByPropertyDynamicFlowFunction<>(eventStream, accessor, secondArgument.eventStream, predicate));
    }

    public <P, S> B filterByProperty(
            SerializableBiFunction<P, Double, Boolean> predicate,
            SerializableFunction<T, P> accessor,
            DoubleFlowBuilder secondArgument) {
        return connect(
                new FilterByPropertyDynamicFlowFunction<>(eventStream, accessor, secondArgument.eventStream, predicate));
    }

    public <P, S> B filterByProperty(
            SerializableBiFunction<P, Long, Boolean> predicate,
            SerializableFunction<T, P> accessor,
            LongFlowBuilder secondArgument) {
        return connect(
                new FilterByPropertyDynamicFlowFunction<>(eventStream, accessor, secondArgument.eventStream, predicate));
    }


    //MAPPING
    protected <R, E> E mapOnNotifyBase(R target) {
        return (E) connectMap(new MapOnNotifyFlowFunction<>(eventStream, target));
    }

    protected <R, E> E mapBase(SerializableFunction<T, R> mapFunction) {
        return (E) connectMap(new MapRef2RefFlowFunction<>(eventStream, mapFunction));
    }


    //MAP TO PRIMITIVES

    //OUTPUTS - START
    public B push(SerializableConsumer<T> pushFunction) {
//        EventProcessorConfigService.service().add(pushFunction.captured()[0]);
        return connect(new PushFlowFunction<>(eventStream, pushFunction));
    }

    public B sink(String sinkId) {
        return push(new SinkPublisher<>(sinkId)::publish);
    }

    public B notify(Object target) {
        EventProcessorBuilderService.service().add(target);
        return connect(new NotifyFlowFunction<>(eventStream, target));
    }

    public B processAsNewGraphEvent() {
        return connect(new PeekFlowFunction<>(eventStream, new InternalEventDispatcher()::dispatchToGraph));
    }

    public B peek(SerializableConsumer<T> peekFunction) {
        return connect(new PeekFlowFunction<>(eventStream, peekFunction));
    }

    public <R> B console(String in, SerializableFunction<T, R> transformFunction) {
        peek(Peekers.console(in, transformFunction));
        return identity();
    }

    public B console(String in) {
        return console(in, null);
    }

    public B console() {
        return console("{}");
    }

    public IntFlowBuilder mapToInt(LambdaReflection.SerializableToIntFunction<T> mapFunction) {
        return new IntFlowBuilder(new MapRef2ToIntFlowFunction<>(eventStream, mapFunction));
    }

    public DoubleFlowBuilder mapToDouble(LambdaReflection.SerializableToDoubleFunction<T> mapFunction) {
        return new DoubleFlowBuilder(new MapRef2ToDoubleFlowFunction<>(eventStream, mapFunction));
    }

    public LongFlowBuilder mapToLong(LambdaReflection.SerializableToLongFunction<T> mapFunction) {
        return new LongFlowBuilder(new MapRef2ToLongFlowFunction<>(eventStream, mapFunction));
    }

    //META-DATA
    public B id(String nodeId) {
        EventProcessorBuilderService.service().add(eventStream, nodeId);
        return identity();
    }
}
