package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.stream.MapEventStream;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import com.fluxtion.runtime.stream.groupby.GroupBy;
import com.fluxtion.runtime.stream.groupby.GroupByStreamed;
import com.fluxtion.runtime.stream.groupby.MapGroupByFunctionInvoker;
import com.google.common.base.Function;

public class AbstractGroupByBuilder<K, V, T extends GroupBy<K, V>> extends EventStreamBuilder<T> {

    AbstractGroupByBuilder(TriggeredEventStream<T> eventStream) {
        super(eventStream);
    }

    <I, G extends GroupByStreamed<K, V>>
    AbstractGroupByBuilder(MapEventStream<I, T, TriggeredEventStream<I>> eventStream) {
        super(eventStream);
    }

    public <O> Object mapValues(SerializableFunction<V, O> mappingFunction){
        Function<GroupBy<K, V>, GroupByStreamed<K, O>> groupByGroupByStreamedFunction = new MapGroupByFunctionInvoker(mappingFunction)::mapValues;
        EventStreamBuilder<GroupByStreamed<K, O>> x = map(new MapGroupByFunctionInvoker(mappingFunction)::mapValues);


        return null;
    }


    static <K, V, O, G extends GroupBy<K, V>> SerializableFunction<G, GroupByStreamed<K, O>> mapValuesXXX(
            SerializableFunction<V, O> mappingFunction) {
        return new MapGroupByFunctionInvoker(mappingFunction)::mapValues;
    }

}
