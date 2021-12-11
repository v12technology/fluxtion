package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtim.SepContext;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableLongConsumer;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableLongFunction;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableLongUnaryOperator;
import com.fluxtion.runtim.stream.*;
import com.fluxtion.runtim.stream.EventStream.LongEventStream;

public class LongStreamBuilder<I, S extends EventStream<I>> {

    final LongEventStream eventStream;

    LongStreamBuilder(LongEventStream eventStream) {
        SepContext.service().add(eventStream);
        this.eventStream = eventStream;
    }

    //TRIGGERS - START
    public LongStreamBuilder<I, S> updateTrigger(Object updateTrigger){
        eventStream.setUpdateTriggerNode(StreamHelper.getSource(updateTrigger));
        return this;
    }

    public LongStreamBuilder<I, S> publishTrigger(Object publishTrigger){
        eventStream.setPublishTriggerNode(StreamHelper.getSource(publishTrigger));
        return this;
    }

    public LongStreamBuilder<I, S> resetTrigger(Object resetTrigger){
        eventStream.setResetTriggerNode(StreamHelper.getSource(resetTrigger));
        return this;
    }

    //PROCESSING - START
    public LongStreamBuilder<Long, LongEventStream> map(SerializableLongUnaryOperator int2IntFunction) {
        return new LongStreamBuilder<>(new MapEventStream.MapLong2ToLongEventStream(eventStream, int2IntFunction));
    }

    public LongStreamBuilder<Long, LongEventStream> filter(SerializableLongFunction<Boolean> filterFunction){
        return new LongStreamBuilder<>( new FilterEventStream.LongFilterEventStream(eventStream, filterFunction));
    }

    //OUTPUTS - START
    public LongStreamBuilder<Long, LongEventStream> notify(Object target) {
        SepContext.service().add(target);
        return new LongStreamBuilder<>(new NotifyEventStream.LongNotifyEventStream(eventStream, target));
    }

    public LongStreamBuilder<Long, LongEventStream> push(SerializableLongConsumer pushFunction) {
        SepContext.service().add(pushFunction.captured()[0]);
        return new LongStreamBuilder<>(new PushEventStream.LongPushEventStream(eventStream, pushFunction));
    }
}
