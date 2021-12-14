package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtim.SepContext;
import com.fluxtion.runtim.stream.*;
import com.fluxtion.runtim.stream.EventStream.IntEventStream;
import com.fluxtion.runtim.stream.helpers.DefaultValue;

import static com.fluxtion.runtim.partition.LambdaReflection.*;

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

    //META-DATA
    public IntStreamBuilder id(String nodeId){
//        SepContext.service().add(eventStream, nodeId +"EventStream");
        SepContext.service().add(eventStream, nodeId);
         return this;
    }

//    public IntStreamBuilder<Integer, IntEventStream> peek(SerializableIntConsumer peekFunction) {
//        return new IntStreamBuilder<>(new PeekEventStream<>(eventStream, peekFunction));
//    }
}
