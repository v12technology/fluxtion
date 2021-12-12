package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtim.SepContext;
import com.fluxtion.runtim.partition.LambdaReflection.*;
import com.fluxtion.runtim.stream.*;
import com.fluxtion.runtim.stream.EventStream.DoubleEventStream;
import com.fluxtion.runtim.stream.helpers.DefaultValue;

public class DoubleStreamBuilder<I, S extends EventStream<I>> {

    final DoubleEventStream eventStream;

    DoubleStreamBuilder(DoubleEventStream eventStream) {
        SepContext.service().add(eventStream);
        this.eventStream = eventStream;
    }

    //TRIGGERS - START
    public DoubleStreamBuilder<I, S> updateTrigger(Object updateTrigger){
        eventStream.setUpdateTriggerNode(StreamHelper.getSource(updateTrigger));
        return this;
    }

    public DoubleStreamBuilder<I, S> publishTrigger(Object publishTrigger){
        eventStream.setPublishTriggerNode(StreamHelper.getSource(publishTrigger));
        return this;
    }

    public DoubleStreamBuilder<I, S> resetTrigger(Object resetTrigger){
        eventStream.setResetTriggerNode(StreamHelper.getSource(resetTrigger));
        return this;
    }

    public DoubleStreamBuilder<Double, DoubleEventStream> filter(SerializableDoubleFunction<Boolean> filterFunction){
        return new DoubleStreamBuilder<>( new FilterEventStream.DoubleFilterEventStream(eventStream, filterFunction));
    }

    public DoubleStreamBuilder<Double, DoubleEventStream>defaultValue(double defaultValue){
        return map(new DefaultValue.DefaultDouble(defaultValue)::getOrDefault);
    }

    //PROCESSING - START
    public DoubleStreamBuilder<Double, DoubleEventStream> map(SerializableDoubleUnaryOperator int2IntFunction) {
        return new DoubleStreamBuilder<>(new MapEventStream.MapDouble2ToDoubleEventStream(eventStream, int2IntFunction));
    }

    public IntStreamBuilder<Double, DoubleEventStream> mapToInt(SerializableDoubleToIntFunction int2IntFunction) {
        return new IntStreamBuilder<>(new MapEventStream.MapDouble2ToIntEventStream(eventStream, int2IntFunction));
    }

    public LongStreamBuilder<Double, DoubleEventStream> mapToLong(SerializableDoubleToLongFunction int2IntFunction) {
        return new LongStreamBuilder<>(new MapEventStream.MapDouble2ToLongEventStream(eventStream, int2IntFunction));
    }

    //OUTPUTS - START
    public DoubleStreamBuilder<Double, DoubleEventStream> notify(Object target) {
        SepContext.service().add(target);
        return new DoubleStreamBuilder<>(new NotifyEventStream.DoubleNotifyEventStream(eventStream, target));
    }

    public DoubleStreamBuilder<Double, DoubleEventStream> push(SerializableDoubleConsumer pushFunction) {
        SepContext.service().add(pushFunction.captured()[0]);
        return new DoubleStreamBuilder<>(new PushEventStream.DoublePushEventStream(eventStream, pushFunction));
    }
}
