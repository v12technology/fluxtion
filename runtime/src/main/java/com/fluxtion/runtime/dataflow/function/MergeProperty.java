package com.fluxtion.runtime.dataflow.function;

import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiConsumer;

/**
 * @param <T> The target type
 * @param <R> The consumer property type on the target and type of the incoming {@link FlowFunction}
 */
public class MergeProperty<T, R> {
    private final FlowFunction<R> trigger;
    private final LambdaReflection.SerializableBiConsumer<T, R> setValue;
    private final boolean triggering;
    private final boolean mandatory;

    public MergeProperty(FlowFunction<R> trigger, SerializableBiConsumer<T, R> setValue, boolean triggering, boolean mandatory) {
        this.trigger = trigger;
        this.setValue = setValue;
        this.triggering = triggering;
        this.mandatory = mandatory;
    }

    public FlowFunction<R> getTrigger() {
        return trigger;
    }

    public SerializableBiConsumer<T, R> getSetValue() {
        return setValue;
    }

    public boolean isTriggering() {
        return triggering;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void push(T target) {
        setValue.accept(target, trigger.get());
    }
}
