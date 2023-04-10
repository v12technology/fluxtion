package com.fluxtion.runtime.dataflow.groupby;

import com.fluxtion.runtime.dataflow.Tuple;

public class LeftJoin extends AbstractJoin {

    @Override
    @SuppressWarnings("unckecked")
    public <K1, V1, K2 extends K1, V2> GroupBy<K1, Tuple<V1, V2>> join(
            GroupBy<K1, V1> leftGroupBy, GroupBy<K2, V2> rightGroupBY) {
        reset();


        if (leftGroupBy != null) {
            leftGroupBy.toMap().entrySet().forEach(e -> {
                V2 value2 = rightGroupBY == null ? null : rightGroupBY.toMap().get(e.getKey());
//                joinedGroup.toMap().put(e.getKey(), Tuple.build(e.getValue(), value2));
                joinedGroup.toMap().put(
                        e.getKey(),
                        tupleObjectPool.checkOut().setFirst(e.getValue()).setSecond(value2));
            });
        }
        return (GroupBy<K1, Tuple<V1, V2>>) (Object) joinedGroup;
    }

}
