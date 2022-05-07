package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.SepContext;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.partition.LambdaReflection.*;
import com.fluxtion.runtime.stream.*;
import com.fluxtion.runtime.stream.EventStream.DoubleEventStream;
import com.fluxtion.runtime.stream.helpers.DefaultValue;
import com.fluxtion.runtime.stream.helpers.Peekers;

public class DoubleStreamBuilder {

    final DoubleEventStream eventStream;

    DoubleStreamBuilder(DoubleEventStream eventStream) {
        SepContext.service().add(eventStream);
        this.eventStream = eventStream;
    }

    //TRIGGERS - START
    public DoubleStreamBuilder updateTrigger(Object updateTrigger){
        eventStream.setUpdateTriggerNode(StreamHelper.getSource(updateTrigger));
        return this;
    }

    public DoubleStreamBuilder publishTrigger(Object publishTrigger){
        eventStream.setPublishTriggerNode(StreamHelper.getSource(publishTrigger));
        return this;
    }

    public DoubleStreamBuilder resetTrigger(Object resetTrigger){
        eventStream.setResetTriggerNode(StreamHelper.getSource(resetTrigger));
        return this;
    }

    public DoubleStreamBuilder filter(SerializableDoubleFunction<Boolean> filterFunction){
        return new DoubleStreamBuilder ( new FilterEventStream.DoubleFilterEventStream(eventStream, filterFunction));
    }

    public DoubleStreamBuilder  defaultValue(double defaultValue){
        return map(new DefaultValue.DefaultDouble(defaultValue)::getOrDefault);
    }

    //PROCESSING - START
    public DoubleStreamBuilder map(SerializableDoubleUnaryOperator int2IntFunction) {
        return new DoubleStreamBuilder (new MapEventStream.MapDouble2ToDoubleEventStream(eventStream, int2IntFunction));
    }

    public DoubleStreamBuilder map(SerializableBiDoubleFunction int2IntFunction, DoubleStreamBuilder stream2Builder) {
        return new DoubleStreamBuilder (
                new BinaryMapEventStream.BinaryMapToDoubleEventStream<>(
                        eventStream, stream2Builder.eventStream, int2IntFunction)
        );
    }

    public <T> EventStreamBuilder<T> mapOnNotify(T target){
        return new EventStreamBuilder<>(new MapOnNotifyEventStream<>(eventStream, target));
    }

    public EventStreamBuilder<Double> box(){
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
        SepContext.service().add(target);
        return new DoubleStreamBuilder (new NotifyEventStream.DoubleNotifyEventStream(eventStream, target));
    }

    public DoubleStreamBuilder push(SerializableDoubleConsumer pushFunction) {
        SepContext.service().add(pushFunction.captured()[0]);
        return new DoubleStreamBuilder (new PushEventStream.DoublePushEventStream(eventStream, pushFunction));
    }

    public DoubleStreamBuilder peek(LambdaReflection.SerializableConsumer<Double> peekFunction) {
        return new DoubleStreamBuilder(new PeekEventStream.DoublePeekEventStream(eventStream, peekFunction));
    }

    public DoubleStreamBuilder console(String in){
        return peek(Peekers.console(in));
    }

    //META-DATA
    public DoubleStreamBuilder id(String nodeId){
        SepContext.service().add(eventStream, nodeId);
        return this;
    }

}
