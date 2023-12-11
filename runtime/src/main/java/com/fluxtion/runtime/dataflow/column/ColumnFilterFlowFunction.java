package com.fluxtion.runtime.dataflow.column;

import com.fluxtion.runtime.partition.LambdaReflection;

import java.util.ArrayList;
import java.util.List;

public class ColumnFilterFlowFunction<T> implements Column<T> {

    private final LambdaReflection.SerializableFunction<T, Boolean> filterFunction;
    private final transient List<T> mappedList = new ArrayList<>();

    public ColumnFilterFlowFunction(LambdaReflection.SerializableFunction<T, Boolean> filterFunction) {
        this.filterFunction = filterFunction;
    }

    public Column<T> filter(Column<T> inputList) {
        mappedList.clear();
        inputList.values().stream().filter(filterFunction::apply).forEach(mappedList::add);
        return this;
    }

    @Override
    public List<T> values() {
        return mappedList;
    }

}
