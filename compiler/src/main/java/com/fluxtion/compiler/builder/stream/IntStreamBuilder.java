package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtim.SepContext;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableIntConsumer;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableIntFunction;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableIntUnaryOperator;
import com.fluxtion.runtim.stream.*;
import com.fluxtion.runtim.stream.EventStream.IntEventStream;

public class IntStreamBuilder<I, S extends EventStream<I>> {

    final IntEventStream eventStream;

    IntStreamBuilder(IntEventStream eventStream) {
        SepContext.service().add(eventStream);
        this.eventStream = eventStream;
    }

    //TRIGGERS - START
    public IntStreamBuilder<I, S> updateTrigger(Object updateTrigger){
        eventStream.setUpdateTriggerNode(StreamHelper.getSource(updateTrigger));
        return this;
    }

    public IntStreamBuilder<I, S> publishTrigger(Object publishTrigger){
        eventStream.setPublishTriggerNode(StreamHelper.getSource(publishTrigger));
        return this;
    }

    public IntStreamBuilder<I, S> resetTrigger(Object resetTrigger){
        eventStream.setResetTriggerNode(StreamHelper.getSource(resetTrigger));
        return this;
    }

    //PROCESSING - START
    public IntStreamBuilder<Integer, IntEventStream> mapToInt(SerializableIntUnaryOperator int2IntFunction) {
        return new IntStreamBuilder<>(new MapEventStream.MapInt2IntEventStream(eventStream, int2IntFunction));
    }

    public IntStreamBuilder<Integer, IntEventStream> filter( SerializableIntFunction<Boolean> filterFunction){
        return new IntStreamBuilder<>( new FilterEventStream.IntFilterEventStream(eventStream, filterFunction));
    }

    //OUTPUTS - START
    public IntStreamBuilder<Integer, IntEventStream> notify(Object target) {
        SepContext.service().add(target);
        return new IntStreamBuilder<>(new NotifyEventStream.IntNotifyEventStream(eventStream, target));
    }

    public IntStreamBuilder<Integer, IntEventStream> push(SerializableIntConsumer pushFunction) {
        SepContext.service().add(pushFunction.captured()[0]);
        return new IntStreamBuilder<>(new PushEventStream.IntPushEventStream(eventStream, pushFunction));
    }
}
