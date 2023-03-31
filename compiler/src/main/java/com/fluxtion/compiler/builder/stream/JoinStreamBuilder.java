package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.stream.Tuple;
import com.fluxtion.runtime.stream.helpers.Mappers;

public interface JoinStreamBuilder {
    static <K1, V1, K2 extends K1, V2> GroupByStreamBuilder<K1, Tuple<V1, V2>> innerJoin(
            GroupByStreamBuilder<K1, V1> leftGroupBy,
            GroupByStreamBuilder<K2, V2> rightGroupBy) {
        return leftGroupBy.mapBiFunction(Mappers::innerJoin, rightGroupBy);
    }

    static <K1, V1, K2 extends K1, V2> GroupByStreamBuilder<K1, Tuple<V1, V2>> outerJoin(
            GroupByStreamBuilder<K1, V1> leftGroupBy,
            GroupByStreamBuilder<K2, V2> rightGroupBy) {
        return leftGroupBy.mapBiFunction(Mappers::outerJoin, rightGroupBy);
    }

    static <K1, V1, K2 extends K1, V2> GroupByStreamBuilder<K1, Tuple<V1, V2>> leftJoin(
            GroupByStreamBuilder<K1, V1> leftGroupBy,
            GroupByStreamBuilder<K2, V2> rightGroupBy) {
        return leftGroupBy.mapBiFunction(Mappers::leftJoin, rightGroupBy);
    }

    static <K1, V1, K2 extends K1, V2> GroupByStreamBuilder<K1, Tuple<V1, V2>> rightJoinStreams(
            GroupByStreamBuilder<K1, V1> leftGroupBy,
            GroupByStreamBuilder<K2, V2> rightGroupBy) {
        return leftGroupBy.mapBiFunction(Mappers::rightJoin, rightGroupBy);
    }
}
