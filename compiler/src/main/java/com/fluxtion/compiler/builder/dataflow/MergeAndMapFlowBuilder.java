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

package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.dataflow.function.MergeMapFlowFunction;
import com.fluxtion.runtime.dataflow.function.MergeMapToNodeFlowFunction;
import com.fluxtion.runtime.dataflow.function.MergeProperty;
import com.fluxtion.runtime.partition.LambdaReflection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * A builder that merges and maps several {@link FlowFunction}'s into a single event stream of type T
 *
 * @param <T> The output type of the merged stream
 */
@AllArgsConstructor
public class MergeAndMapFlowBuilder<T> {

    private final LambdaReflection.SerializableSupplier<T> resultFactory;
    private final T resultInstance;
    private final List<MergeProperty<T, ?>> required = new ArrayList<>();

    private MergeAndMapFlowBuilder(LambdaReflection.SerializableSupplier<T> resultFactory) {
        this.resultFactory = resultFactory;
        resultInstance = null;
    }

    private MergeAndMapFlowBuilder(T resultInstance) {
        this.resultFactory = null;
        this.resultInstance = resultInstance;
    }

    public static <T> MergeAndMapFlowBuilder<T> of(LambdaReflection.SerializableSupplier<T> resultFactory) {
        return new MergeAndMapFlowBuilder<T>(resultFactory);
    }

    public static <T> MergeAndMapFlowBuilder<T> of(T resultInstance) {
        return new MergeAndMapFlowBuilder<T>(resultInstance);
    }

    public <R> MergeAndMapFlowBuilder<T> required(FlowBuilder<R> trigger, LambdaReflection.SerializableBiConsumer<T, R> setValue) {
        required.add(new MergeProperty<T, R>(trigger.eventStream, setValue, true, true));
        return this;
    }

    public <R> MergeAndMapFlowBuilder<T> requiredNoTrigger(FlowBuilder<R> trigger, LambdaReflection.SerializableBiConsumer<T, R> setValue) {
        required.add(new MergeProperty<T, R>(trigger.eventStream, setValue, false, true));
        return this;
    }

    /**
     * Builds a FlowBuilder that is formed from multiple inouts pushing to a target instance.
     *
     * @param target   Supplier of target instances that store the result of the push
     * @param joinLegs The legs that supply the inputs to the join
     * @param <K>      The key class
     * @param <T>      The join target class
     * @return The GroupByFlow with a new instance of the target allocated to every key
     */
    @SuppressWarnings("all")
    public static <K, T> FlowBuilder<T> merge(LambdaReflection.SerializableSupplier<T> target, MergeInput<T, ?>... joinLegs) {
        MergeAndMapFlowBuilder multiJoinBuilder = new MergeAndMapFlowBuilder(target);
        for (MergeInput joinLeg : joinLegs) {
            if (joinLeg.isTriggering()) {
                multiJoinBuilder.required(joinLeg.flow, joinLeg.getSetter());
            } else {
                multiJoinBuilder.requiredNoTrigger(joinLeg.flow, joinLeg.getSetter());
            }
        }
        return multiJoinBuilder.dataFlow();
    }

    @SuppressWarnings("all")
    public static <K, T> FlowBuilder<T> mergeToNode(T target, MergeInput<T, ?>... joinLegs) {
        MergeAndMapFlowBuilder multiJoinBuilder = new MergeAndMapFlowBuilder(target);
        for (MergeInput joinLeg : joinLegs) {
            if (joinLeg.isTriggering()) {
                multiJoinBuilder.required(joinLeg.flow, joinLeg.getSetter());
            } else {
                multiJoinBuilder.requiredNoTrigger(joinLeg.flow, joinLeg.getSetter());
            }
        }
        return multiJoinBuilder.dataFlow();
    }

    public static <T1, R> MergeInput<T1, R> requiredMergeInput(FlowBuilder<R> flow,
                                                               LambdaReflection.SerializableBiConsumer<T1, R> setter) {
        return new MergeAndMapFlowBuilder.MergeInput<>(true, flow, setter);
    }

    public static <T1, R> MergeInput<T1, R> optionalMergeInput(FlowBuilder<R> flow,
                                                               LambdaReflection.SerializableBiConsumer<T1, R> setter) {
        return new MergeAndMapFlowBuilder.MergeInput<>(false, flow, setter);
    }

    @Data
    public static class MergeInput<T, R> {
        private final boolean triggering;
        private final FlowBuilder<R> flow;
        private final LambdaReflection.SerializableBiConsumer<T, R> setter;
    }

    public TriggeredFlowFunction<T> build() {
        TriggeredFlowFunction<T> flowFunction;
        if (resultFactory == null) {
            MergeMapToNodeFlowFunction<T> streamNode = new MergeMapToNodeFlowFunction<>(resultInstance);
            required.forEach(streamNode::registerTrigger);
            flowFunction = streamNode;
        } else {
            MergeMapFlowFunction<T> stream = new MergeMapFlowFunction<>(resultFactory, new ArrayList<>());
            required.forEach(stream::registerTrigger);
            flowFunction = stream;
        }
        return flowFunction;
    }

    /**
     * Merges and maps several {@link FlowFunction}'s into a single event stream of type T
     *
     * @return An {@link FlowBuilder} that can used to construct stream processing logic
     */
    public FlowBuilder<T> dataFlow() {
        return new FlowBuilder<>(build());
    }
}
