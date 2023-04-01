package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.runtime.dataflow.Tuple;
import com.fluxtion.runtime.dataflow.groupby.InnerJoin;
import com.fluxtion.runtime.dataflow.groupby.LeftJoin;
import com.fluxtion.runtime.dataflow.groupby.OuterJoin;
import com.fluxtion.runtime.dataflow.groupby.RightJoin;

public interface JoinFlowBuilder {
    static <K1, V1, K2 extends K1, V2> GroupByFlowBuilder<K1, Tuple<V1, V2>> innerJoin(
            GroupByFlowBuilder<K1, V1> leftGroupBy,
            GroupByFlowBuilder<K2, V2> rightGroupBy) {
        return leftGroupBy.mapBiFunction(new InnerJoin()::join, rightGroupBy);
    }

    static <K1, V1, K2 extends K1, V2> GroupByFlowBuilder<K1, Tuple<V1, V2>> outerJoin(
            GroupByFlowBuilder<K1, V1> leftGroupBy,
            GroupByFlowBuilder<K2, V2> rightGroupBy) {
        return leftGroupBy.mapBiFunction(new OuterJoin()::join, rightGroupBy);
    }

    static <K1, V1, K2 extends K1, V2> GroupByFlowBuilder<K1, Tuple<V1, V2>> leftJoin(
            GroupByFlowBuilder<K1, V1> leftGroupBy,
            GroupByFlowBuilder<K2, V2> rightGroupBy) {
        return leftGroupBy.mapBiFunction(new LeftJoin()::join, rightGroupBy);
    }

    static <K1, V1, K2 extends K1, V2> GroupByFlowBuilder<K1, Tuple<V1, V2>> rightJoinStreams(
            GroupByFlowBuilder<K1, V1> leftGroupBy,
            GroupByFlowBuilder<K2, V2> rightGroupBy) {
        return leftGroupBy.mapBiFunction(new RightJoin()::join, rightGroupBy);
    }
}
