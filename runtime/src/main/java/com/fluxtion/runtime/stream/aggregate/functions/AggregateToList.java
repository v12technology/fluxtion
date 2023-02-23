package com.fluxtion.runtime.stream.aggregate.functions;

import com.fluxtion.runtime.stream.aggregate.AggregateFunction;

import java.util.ArrayList;
import java.util.List;

public class AggregateToList<T> implements AggregateFunction<T, List<T>, AggregateToList<T>> {

    private final List<T> list = new ArrayList<>();
    private final int maxElementCount;


    public AggregateToList() {
        this(-1);
    }

    public AggregateToList(int maxElementCount) {
        this.maxElementCount = maxElementCount;
    }

    @Override
    public List<T> reset() {
        list.clear();
        return list;
    }

    @Override
    public void combine(AggregateToList<T> add) {
        list.addAll(add.list);
        while (maxElementCount > 0 & list.size() > maxElementCount) {
            list.remove(0);
        }
    }

    @Override
    public void deduct(AggregateToList<T> add) {
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

        public <T> AggregateToList<T> newList() {
            return new AggregateToList<>(maxElementCount);
        }
    }
}
