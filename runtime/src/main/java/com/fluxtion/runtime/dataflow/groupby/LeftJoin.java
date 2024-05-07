package com.fluxtion.runtime.dataflow.groupby;

import com.fluxtion.runtime.dataflow.Tuple;

public class LeftJoin extends AbstractJoin {

    @Override
    @SuppressWarnings("unckecked")
    public <K1, V1, K2 extends K1, V2> GroupBy<K1, Tuple<V1, V2>> join(
            GroupBy<K1, V1> leftGroupBy, GroupBy<K2, V2> rightGroupBY) {
        reset();
        if (leftGroupBy != null) {
            leftGroupBy.toMap().entrySet().forEach(left -> {
                V2 right = rightGroupBY == null ? null : rightGroupBY.toMap().get(left.getKey());
//                joinedGroup.toMap().put(left.getKey(), Tuple.build(left.getValue(), right));
                joinedGroup.toMap().put(
                        left.getKey(),
                        tupleObjectPool.checkOut().setFirst(left.getValue()).setSecond(right));
            });
        }
        return (GroupBy<K1, Tuple<V1, V2>>) (Object) joinedGroup;
    }

}
