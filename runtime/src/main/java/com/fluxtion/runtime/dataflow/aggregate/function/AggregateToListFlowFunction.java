package com.fluxtion.runtime.dataflow.aggregate.function;

import com.fluxtion.runtime.dataflow.aggregate.AggregateFlowFunction;

import java.util.ArrayList;
import java.util.List;

public class AggregateToListFlowFunction<T> implements AggregateFlowFunction<T, List<T>, AggregateToListFlowFunction<T>> {

    private final List<T> list = new ArrayList<>();
    private final int maxElementCount;


    public AggregateToListFlowFunction() {
        this(-1);
    }

    public AggregateToListFlowFunction(int maxElementCount) {
        this.maxElementCount = maxElementCount;
    }

    @Override
    public List<T> reset() {
        list.clear();
        return list;
    }

    @Override
    public void combine(AggregateToListFlowFunction<T> add) {
        list.addAll(add.list);
        while (maxElementCount > 0 & list.size() > maxElementCount) {
            list.remove(0);
        }
    }

    @Override
    public void deduct(AggregateToListFlowFunction<T> add) {
        list.removeAll(add.list);
    }

    @Override
    public List<T> get() {
        return list;
    }

    @Override
    public List<T> aggregate(T input) {
        list.add(input);
        if (maxElementCount > 0 & list.size() > maxElementCount) {
            list.remove(0);
        }
        return list;
    }


    public static class AggregateToListFactory {
        private final int maxElementCount;

        public AggregateToListFactory(int maxElementCount) {
            this.maxElementCount = maxElementCount;
        }

        public <T> AggregateToListFlowFunction<T> newList() {
            return new AggregateToListFlowFunction<>(maxElementCount);
        }
    }
}
