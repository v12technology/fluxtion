package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.runtime.dataflow.Tuple;
import com.fluxtion.runtime.dataflow.groupby.InnerJoin;
import com.fluxtion.runtime.dataflow.groupby.LeftJoin;
import com.fluxtion.runtime.dataflow.groupby.OuterJoin;
import com.fluxtion.runtime.dataflow.groupby.RightJoin;
import com.fluxtion.runtime.dataflow.helpers.Tuples;
import com.fluxtion.runtime.partition.LambdaReflection;

public interface JoinFlowBuilder {
    static <K1, V1, K2 extends K1, V2> GroupByFlowBuilder<K1, Tuple<V1, V2>> innerJoin(
            GroupByFlowBuilder<K1, V1> leftGroupBy,
            GroupByFlowBuilder<K2, V2> rightGroupBy) {
        return leftGroupBy.mapBiFunction(new InnerJoin()::join, rightGroupBy);
    }

    static <K1, V1, K2 extends K1, V2, R> GroupByFlowBuilder<K1, R> innerJoin(
            GroupByFlowBuilder<K1, V1> leftGroupBy,
            GroupByFlowBuilder<K2, V2> rightGroupBy,
            LambdaReflection.SerializableBiFunction<V1, V2, R> mergeFunction) {
        return leftGroupBy.mapBiFunction(new InnerJoin()::join, rightGroupBy).mapValues(Tuples.mapTuple(mergeFunction));
    }

    static <K1, V1, K2 extends K1, V2> GroupByFlowBuilder<K1, Tuple<V1, V2>> outerJoin(
            GroupByFlowBuilder<K1, V1> leftGroupBy,
            GroupByFlowBuilder<K2, V2> rightGroupBy) {
        return leftGroupBy.mapBiFunction(new OuterJoin()::join, rightGroupBy);
    }

    static <K1, V1, K2 extends K1, V2, R> GroupByFlowBuilder<K1, R> outerJoin(
            GroupByFlowBuilder<K1, V1> leftGroupBy,
            GroupByFlowBuilder<K2, V2> rightGroupBy,
            LambdaReflection.SerializableBiFunction<V1, V2, R> mergeFunction) {
        return leftGroupBy.mapBiFunction(new OuterJoin()::join, rightGroupBy).mapValues(Tuples.mapTuple(mergeFunction));
    }

    static <K1, V1, K2 extends K1, V2> GroupByFlowBuilder<K1, Tuple<V1, V2>> leftJoin(
            GroupByFlowBuilder<K1, V1> leftGroupBy,
            GroupByFlowBuilder<K2, V2> rightGroupBy) {
        return leftGroupBy.mapBiFunction(new LeftJoin()::join, rightGroupBy);
    }

    static <K1, V1, K2 extends K1, V2, R> GroupByFlowBuilder<K1, R> leftJoin(
            GroupByFlowBuilder<K1, V1> leftGroupBy,
            GroupByFlowBuilder<K2, V2> rightGroupBy,
            LambdaReflection.SerializableBiFunction<V1, V2, R> mergeFunction) {
        return leftGroupBy.mapBiFunction(new LeftJoin()::join, rightGroupBy).mapValues(Tuples.mapTuple(mergeFunction));
    }

    static <K1, V1, K2 extends K1, V2> GroupByFlowBuilder<K1, Tuple<V1, V2>> rightJoin(
            GroupByFlowBuilder<K1, V1> leftGroupBy,
            GroupByFlowBuilder<K2, V2> rightGroupBy) {
        return leftGroupBy.mapBiFunction(new RightJoin()::join, rightGroupBy);
    }

    static <K1, V1, K2 extends K1, V2, R> GroupByFlowBuilder<K1, R> rightJoin(
            GroupByFlowBuilder<K1, V1> leftGroupBy,
            GroupByFlowBuilder<K2, V2> rightGroupBy,
            LambdaReflection.SerializableBiFunction<V1, V2, R> mergeFunction) {
        return leftGroupBy.mapBiFunction(new RightJoin()::join, rightGroupBy).mapValues(Tuples.mapTuple(mergeFunction));
    }
}
