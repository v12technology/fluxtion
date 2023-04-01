package com.fluxtion.runtime.dataflow.aggregate.function;

import com.fluxtion.runtime.dataflow.aggregate.AggregateFlowFunction;

import java.util.HashSet;
import java.util.Set;

public class AggregateToSetFlowFunction<T> implements AggregateFlowFunction<T, Set<T>, AggregateToSetFlowFunction<T>> {

    private final Set<T> list = new HashSet<>();

    @Override
    public Set<T> reset() {
        list.clear();
        return list;
    }

    @Override
    public void combine(AggregateToSetFlowFunction<T> add) {
        list.addAll(add.list);
    }

    @Override
    public void deduct(AggregateToSetFlowFunction<T> add) {
        list.removeAll(add.list);
    }

    @Override
    public Set<T> get() {
        return list;
    }

    @Override
    public Set<T> aggregate(T input) {
        list.add(input);
        return list;
    }


    public static class AggregateToSetFactory {

        public <T> AggregateToSetFlowFunction<T> newList() {
            return new AggregateToSetFlowFunction<>();
        }
    }
}
