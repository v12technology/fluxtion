package com.fluxtion.runtime.dataflow.groupby;

import com.fluxtion.runtime.dataflow.Tuple;

public class OuterJoin extends AbstractJoin {

    @Override
    @SuppressWarnings("unckecked")
    public <K1, V1, K2 extends K1, V2> GroupBy<K1, Tuple<V1, V2>> join(
            GroupBy<K1, V1> leftGroupBy, GroupBy<K2, V2> rightGroupBY) {
        reset();
        if (leftGroupBy != null) {
            leftGroupBy.toMap().entrySet().forEach(left -> {
                V2 right = rightGroupBY == null ? null : rightGroupBY.toMap().get(left.getKey());
                joinedGroup.toMap().put(
                        left.getKey(),
                        tupleObjectPool.checkOut().setFirst(left.getValue()).setSecond(right));
            });
        }
        if (rightGroupBY != null) {
            rightGroupBY.toMap().entrySet().forEach(right -> {
                V1 left = leftGroupBy == null ? null : leftGroupBy.toMap().get(right.getKey());
                joinedGroup.toMap().put(
                        right.getKey(),
                        tupleObjectPool.checkOut().setFirst(left).setSecond(right.getValue()));
            });
        }
        return (GroupBy<K1, Tuple<V1, V2>>) (Object) joinedGroup;
    }

}
