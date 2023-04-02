package com.fluxtion.runtime.dataflow.function;

import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.partition.LambdaReflection;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Merges multiple event streams into a single transformed output
 */
@ToString
public class MergeMapFlowFunction<T> implements TriggeredFlowFunction<T> {

    private final Supplier<T> factory;
    private T result;
    private final List<MergeProperty<T, ?>> mergeProperties;
    @SuppressWarnings("rawtypes")
    private List<FlowFunction> triggerList = new ArrayList<>();
    @NoTriggerReference
    @SuppressWarnings("rawtypes")
    private final transient List<FlowFunction> nonTriggeringSources = new ArrayList<>();
    private final transient Set<FlowFunction<?>> requiredSet = new HashSet<>();

    private transient boolean allTriggersUpdated = false;

    public MergeMapFlowFunction(LambdaReflection.SerializableSupplier<T> factory) {
        this(factory, new ArrayList<>());
    }

    public MergeMapFlowFunction(LambdaReflection.SerializableSupplier<T> factory, List<MergeProperty<T, ?>> mergeProperties) {
        this.factory = factory;
        this.mergeProperties = mergeProperties;
    }

    @OnParentUpdate("triggerList")
    public void inputUpdated(FlowFunction<?> trigger) {
        if (!allTriggersUpdated) {
            requiredSet.remove(trigger);
            allTriggersUpdated = requiredSet.isEmpty();
        }
    }

    @OnParentUpdate("nonTriggeringSources")
    public void inputNonTriggeringUpdated(FlowFunction<?> trigger) {
        if (!allTriggersUpdated) {
            requiredSet.remove(trigger);
            allTriggersUpdated = requiredSet.isEmpty();
        }
    }

    @OnTrigger
    public boolean triggered() {
        if (allTriggersUpdated) {
            result = factory.get();
            for (int i = 0; i < mergeProperties.size(); i++) {
                mergeProperties.get(i).push(result);
            }
        }
        return allTriggersUpdated;
    }

    public <R> void registerTrigger(MergeProperty<T, R> mergeProperty) {
        if (mergeProperty.isTriggering()) {
            triggerList.add(mergeProperty.getTrigger());
        } else {
            nonTriggeringSources.add(mergeProperty.getTrigger());
        }
        mergeProperties.add(mergeProperty);
    }

    @Override
    public T get() {
        return result;
    }

    @Initialise
    public void init() {
        allTriggersUpdated = false;
        triggerList.clear();
        mergeProperties.stream()
                .map(MergeProperty::getTrigger)
                .forEach(requiredSet::add);
    }

    @Override
    public void setUpdateTriggerNode(Object updateTriggerNode) {
        //do nothing
    }

    @Override
    public void setPublishTriggerNode(Object publishTriggerNode) {
        //do nothing
    }

    @Override
    public void setResetTriggerNode(Object resetTriggerNode) {
        //do nothing
    }

    @Override
    public void setPublishTriggerOverrideNode(Object publishTriggerOverrideNode) {
    }
}
