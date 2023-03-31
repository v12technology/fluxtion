package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.output.SinkPublisher;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import com.fluxtion.runtime.stream.helpers.Peekers;
import com.fluxtion.runtime.stream.impl.FilterByPropertyDynamicEventStream;
import com.fluxtion.runtime.stream.impl.FilterByPropertyEventStream;
import com.fluxtion.runtime.stream.impl.FilterDynamicEventStream;
import com.fluxtion.runtime.stream.impl.FilterEventStream;
import com.fluxtion.runtime.stream.impl.InternalEventDispatcher;
import com.fluxtion.runtime.stream.impl.MapEventStream;
import com.fluxtion.runtime.stream.impl.MapOnNotifyEventStream;
import com.fluxtion.runtime.stream.impl.NotifyEventStream;
import com.fluxtion.runtime.stream.impl.PeekEventStream;
import com.fluxtion.runtime.stream.impl.PushEventStream;

public abstract class AbstractEventStreamBuilder<T, B extends AbstractEventStreamBuilder<T, B>> {

    final TriggeredEventStream<T> eventStream;


    public AbstractEventStreamBuilder(TriggeredEventStream<T> eventStream) {
        this.eventStream = eventStream;
    }

    protected abstract B connect(TriggeredEventStream<T> stream);


    protected abstract <R> AbstractEventStreamBuilder<R, ?> connectMap(TriggeredEventStream<R> stream);

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
        return connect(new FilterEventStream<>(eventStream, filterFunction));
    }

    public <P> B filterByProperty(SerializableFunction<T, P> accessor, SerializableFunction<P, Boolean> filterFunction) {
        return connect(new FilterByPropertyEventStream<>(eventStream, accessor, filterFunction));
    }

    public <S> B filter(
            SerializableBiFunction<T, S, Boolean> predicate,
            EventStreamBuilder<S> secondArgument) {
        return connect(
                new FilterDynamicEventStream<>(eventStream, secondArgument.eventStream, predicate));
    }

    public <S> B filter(
            SerializableBiFunction<T, Integer, Boolean> predicate,
            IntStreamBuilder secondArgument) {
        return connect(
                new FilterDynamicEventStream<>(eventStream, secondArgument.eventStream, predicate));
    }

    public <S> B filter(
            SerializableBiFunction<T, Double, Boolean> predicate,
            DoubleStreamBuilder secondArgument) {
        return connect(
                new FilterDynamicEventStream<>(eventStream, secondArgument.eventStream, predicate));
    }

    public <S> B filter(
            SerializableBiFunction<T, Long, Boolean> predicate,
            LongStreamBuilder secondArgument) {
        return connect(
                new FilterDynamicEventStream<>(eventStream, secondArgument.eventStream, predicate));
    }

    public <P, S> B filterByProperty(
            SerializableBiFunction<P, S, Boolean> predicate,
            SerializableFunction<T, P> accessor,
            EventStreamBuilder<S> secondArgument) {
        return connect(
                new FilterByPropertyDynamicEventStream<>(eventStream, accessor, secondArgument.eventStream, predicate));
    }

    public <P, S> B filterByProperty(
            SerializableBiFunction<P, Integer, Boolean> predicate,
            SerializableFunction<T, P> accessor,
            IntStreamBuilder secondArgument) {
        return connect(
                new FilterByPropertyDynamicEventStream<>(eventStream, accessor, secondArgument.eventStream, predicate));
    }

    public <P, S> B filterByProperty(
            SerializableBiFunction<P, Double, Boolean> predicate,
            SerializableFunction<T, P> accessor,
            DoubleStreamBuilder secondArgument) {
        return connect(
                new FilterByPropertyDynamicEventStream<>(eventStream, accessor, secondArgument.eventStream, predicate));
    }

    public <P, S> B filterByProperty(
            SerializableBiFunction<P, Long, Boolean> predicate,
            SerializableFunction<T, P> accessor,
            LongStreamBuilder secondArgument) {
        return connect(
                new FilterByPropertyDynamicEventStream<>(eventStream, accessor, secondArgument.eventStream, predicate));
    }


    //MAPPING
    protected <R, E> E mapOnNotifyBase(R target) {
        return (E) connectMap(new MapOnNotifyEventStream<>(eventStream, target));
    }

    protected <R, E> E mapBase(SerializableFunction<T, R> mapFunction) {
        return (E) connectMap(new MapEventStream.MapRef2RefEventStream<>(eventStream, mapFunction));
    }


    //MAP TO PRIMITIVES

    //OUTPUTS - START
    public B push(SerializableConsumer<T> pushFunction) {
//        EventProcessorConfigService.service().add(pushFunction.captured()[0]);
        return connect(new PushEventStream<>(eventStream, pushFunction));
    }

    public B sink(String sinkId) {
        return push(new SinkPublisher<>(sinkId)::publish);
    }

    public B notify(Object target) {
        EventProcessorBuilderService.service().add(target);
        return connect(new NotifyEventStream<>(eventStream, target));
    }

    public B processAsNewGraphEvent() {
        return connect(new PeekEventStream<>(eventStream, new InternalEventDispatcher()::dispatchToGraph));
    }

    public B peek(SerializableConsumer<T> peekFunction) {
        return connect(new PeekEventStream<>(eventStream, peekFunction));
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

    public IntStreamBuilder mapToInt(LambdaReflection.SerializableToIntFunction<T> mapFunction) {
        return new IntStreamBuilder(new MapEventStream.MapRef2ToIntEventStream<>(eventStream, mapFunction));
    }

    public DoubleStreamBuilder mapToDouble(LambdaReflection.SerializableToDoubleFunction<T> mapFunction) {
        return new DoubleStreamBuilder(new MapEventStream.MapRef2ToDoubleEventStream<>(eventStream, mapFunction));
    }

    public LongStreamBuilder mapToLong(LambdaReflection.SerializableToLongFunction<T> mapFunction) {
        return new LongStreamBuilder(new MapEventStream.MapRef2ToLongEventStream<>(eventStream, mapFunction));
    }

    //META-DATA
    public B id(String nodeId) {
        EventProcessorBuilderService.service().add(eventStream, nodeId);
        return identity();
    }
}
