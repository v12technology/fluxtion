package com.fluxtion.runtime.dataflow.column;

import com.fluxtion.runtime.dataflow.aggregate.AggregateFlowFunction;

import java.util.ArrayList;
import java.util.List;

public class AggregateToColumnFunction<T> implements Column<T>, AggregateFlowFunction<T, Column<T>, AggregateToColumnFunction<T>> {

    private transient final List<T> list = new ArrayList<>();
    private final int maxElementCount;

    public AggregateToColumnFunction(int maxElementCount) {
        this.maxElementCount = maxElementCount;
    }

    @Override
    public List<T> values() {
        return list;
    }


    @Override
    public Column<T> reset() {
        list.clear();
        return this;
    }

    @Override
    public void combine(AggregateToColumnFunction<T> add) {
        list.addAll(add.list);
        while (maxElementCount > 0 & list.size() > maxElementCount) {
            list.remove(0);
        }
    }

    @Override
    public void deduct(AggregateToColumnFunction<T> add) {
        list.removeAll(add.list);
    }

    @Override
    public Column<T> get() {
        return this;
    }

    @Override
    public Column<T> aggregate(T input) {
        list.add(input);
        if (maxElementCount > 0 & list.size() > maxElementCount) {
            list.remove(0);
        }
        return this;
    }
}
