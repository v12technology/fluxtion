/*
 * Copyright (c) 2025 gregory higgins.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */

package com.fluxtion.runtime.dataflow.function;

import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.callback.DirtyStateMonitor;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Merges multiple event streams into a single transformed output
 */
@ToString
public class MergeMapToNodeFlowFunction<T> implements TriggeredFlowFunction<T> {

    private final T result;
    private final List<MergeProperty<T, ?>> mergeProperties;
    @SuppressWarnings("rawtypes")
    private List<FlowFunction> triggerList = new ArrayList<>();
    @NoTriggerReference
    @SuppressWarnings("rawtypes")
    private final transient List<FlowFunction> nonTriggeringSources = new ArrayList<>();
    private final transient Set<FlowFunction<?>> requiredSet = new HashSet<>();
    private transient boolean allTriggersUpdated = false;
    @Inject
    public DirtyStateMonitor dirtyStateMonitor;

    public MergeMapToNodeFlowFunction(T result) {
        this(result, new ArrayList<>());
    }

    public MergeMapToNodeFlowFunction(
            @AssignToField("result") T result,
            @AssignToField("mergeProperties") List<MergeProperty<T, ?>> mergeProperties) {
        this.result = result;
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
            for (int i = 0; i < mergeProperties.size(); i++) {
                mergeProperties.get(i).push(result);
            }
        }
        return allTriggersUpdated;
    }

    @Override
    public void parallel() {

    }

    @Override
    public boolean parallelCandidate() {
        return false;
    }

    @Override
    public boolean hasChanged() {
        return dirtyStateMonitor.isDirty(this);
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
