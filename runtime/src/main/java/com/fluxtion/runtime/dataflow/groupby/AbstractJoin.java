package com.fluxtion.runtime.dataflow.groupby;

import com.fluxtion.runtime.dataflow.Stateful;
import com.fluxtion.runtime.dataflow.Tuple;
import com.fluxtion.runtime.util.ObjectPool;

public abstract class AbstractJoin implements Stateful<GroupBy> {
    //GroupBy<K1, Tuple<V1, V2>> joinedGroup = new GroupByHashMap<>();
    protected final transient GroupByHashMap<Object, MutableTuple<Object, Object>> joinedGroup = new GroupByHashMap<>();
    protected final transient ObjectPool<MutableTuple> tupleObjectPool = new ObjectPool<>(MutableTuple::new);

    @SuppressWarnings("unckecked")
    public abstract <K1, V1, K2 extends K1, V2> GroupBy<K1, Tuple<V1, V2>> join(
            GroupBy<K1, V1> leftGroupBy, GroupBy<K2, V2> rightGroupBY);

    //hack for incomplete generics in generated code
    public GroupBy join(Object leftGroupBy, Object rightGroupBY) {
        return this.join((GroupBy) leftGroupBy, (GroupBy) rightGroupBY);
    }

    @Override
    public GroupBy reset() {
        joinedGroup.values().forEach(t -> t.returnToPool(tupleObjectPool));
        return joinedGroup.reset();
    }
}
