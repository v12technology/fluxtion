package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.stream.groupby.FilterGroupByFunctionInvoker;
import com.fluxtion.runtime.stream.groupby.GroupBy;
import com.fluxtion.runtime.stream.groupby.GroupByCollection;
import com.fluxtion.runtime.stream.groupby.MapGroupByFunctionInvoker;
import com.fluxtion.runtime.stream.groupby.Tuple;

import java.util.Map;

public interface GroupByFunction {

    static <K, V, O, G extends GroupBy<K, V>> SerializableFunction<G, GroupBy<K, O>> mapValues(
            SerializableFunction<V, O> mappingFunction) {
        return new MapGroupByFunctionInvoker(mappingFunction)::mapValues;
    }

    static <K, V, O, G extends GroupBy<K, V>> SerializableFunction<G, GroupBy<O, V>> mapKeys(
            SerializableFunction<K, O> mappingFunction) {
        return new MapGroupByFunctionInvoker(mappingFunction)::mapKeys;
    }

    static <K, V, K1, V1, G extends GroupBy<K, V>> SerializableFunction<G, GroupBy<K1, V1>> mapEntry(
            SerializableFunction<Map.Entry<K, V>, Map.Entry<K1, V1>> mappingFunction) {
        return new MapGroupByFunctionInvoker(mappingFunction)::mapEntry;
    }

    static <K, V, G extends GroupBy<K, V>> SerializableFunction<G, GroupBy<K, V>> filterValues(
            SerializableFunction<V, Boolean> mappingFunction) {
        return new FilterGroupByFunctionInvoker(mappingFunction)::filterValues;
    }

    //TODO replace with instance version that re-uses the map - although tuple creation will be source of garbage
    static <K1, V1, K2 extends K1, V2> GroupBy<K1, Tuple<V1, V2>> innerJoin(
            GroupBy<K1, V1> leftGroupBy, GroupBy<K2, V2> rightGroupBY) {
        GroupBy<K1, Tuple<V1, V2>> joinedGroup = new GroupByCollection<>();
        if (leftGroupBy != null && rightGroupBY != null) {
            leftGroupBy.map().entrySet().forEach(e -> {
                V2 value2 = rightGroupBY.map().get(e.getKey());
                if (value2 != null) {
                    joinedGroup.map().put(e.getKey(), new Tuple<>(e.getValue(), value2));
                }
            });
        }
        return joinedGroup;
    }

    static <K1, V1, K2 extends K1, V2> EventStreamBuilder<GroupBy<K1, Tuple<V1, V2>>> innerJoinStreams(
            EventStreamBuilder<? extends GroupBy<K1, V1>> leftGroupBy,
            EventStreamBuilder<? extends GroupBy<K2, V2>> rightGroupBy) {
        return leftGroupBy.mapBiFunction(GroupByFunction::innerJoin, rightGroupBy);
    }

    //TODO hack for serialisation, generic types not supported in BinaryMapToRefEventStream
    static GroupBy innerJoin(Object leftGroup, Object rightGroup) {
        return innerJoin((GroupBy) leftGroup, (GroupBy) rightGroup);
    }
}
