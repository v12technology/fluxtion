package com.fluxtion.runtime.dataflow.column;

import com.fluxtion.runtime.partition.LambdaReflection;

import java.util.ArrayList;
import java.util.List;

public class ColumnMapFlowFunction<T, R> implements Column<R> {

    private final LambdaReflection.SerializableFunction<T, R> mapFunction;
    private final transient List<R> mappedList = new ArrayList<>();

    public ColumnMapFlowFunction(LambdaReflection.SerializableFunction<T, R> mapFunction) {
        this.mapFunction = mapFunction;
    }

    public Column<R> map(Column<T> inputList) {
        mappedList.clear();
        inputList.values().stream().map(mapFunction).forEach(mappedList::add);
        return this;
    }

    @Override
    public List<R> values() {
        return mappedList;
    }

}
