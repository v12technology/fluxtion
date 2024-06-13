package com.fluxtion.runtime.dataflow.groupby;

import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.OnParentUpdate;

import static com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;

public class GroupByDeleteByNameFlowFunctionWrapper {

    private final SerializableFunction<Object, Boolean> deletePredicateFunction;
    private Object trigger;
    private boolean applyPredicate;

    @SuppressWarnings({"unchecked"})
    public <T> GroupByDeleteByNameFlowFunctionWrapper(SerializableFunction<T, Boolean> deletePredicateFunction) {
        this.deletePredicateFunction = (SerializableFunction<Object, Boolean>) deletePredicateFunction;
        this.trigger = null;
    }

    @SuppressWarnings({"unchecked"})
    public <T> GroupByDeleteByNameFlowFunctionWrapper(SerializableFunction<T, Boolean> deletePredicateFunction, Object trigger) {
        this.deletePredicateFunction = (SerializableFunction<Object, Boolean>) deletePredicateFunction;
        this.trigger = trigger;
    }

    @Initialise
    public void init() {
        applyPredicate = false;
    }

    @OnParentUpdate("trigger")
    public void predicateUpdated(Object trigger) {
        applyPredicate = true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public GroupBy deleteByKey(GroupBy groupBy, Object keysToDelete) {
        if (applyPredicate) {
            groupBy.toMap().values().removeIf(deletePredicateFunction::apply);
        }
        applyPredicate = false;
        return groupBy;
    }
}
