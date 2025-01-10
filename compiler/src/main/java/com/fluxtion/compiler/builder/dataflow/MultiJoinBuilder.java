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

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.dataflow.groupby.MultiJoin;
import com.fluxtion.runtime.partition.LambdaReflection;
import lombok.Data;

/**
 * Builds a co-group or multi leg join
 *
 * @param <K> The key type for join stream
 * @param <T> Target type of results for multi join
 */
public class MultiJoinBuilder<K, T> {

    private final MultiJoin<K, T> multiLegJoin;

    public MultiJoinBuilder(Class<K> keyClass, LambdaReflection.SerializableSupplier<T> target) {
        multiLegJoin = new MultiJoin<>(keyClass, target);
    }

    public static <K, T> MultiJoinBuilder<K, T> builder(Class<K> keyClass, LambdaReflection.SerializableSupplier<T> target) {
        return new MultiJoinBuilder<>(keyClass, target);
    }

    /**
     * Builds a GroupByFlowBuilder that is formed from multiple joins and pushed to a target instance.
     *
     * @param target   Supplier of target instances that store the result of the join
     * @param joinLegs The legs that supply the inputs to the join
     * @param <K>      The key class
     * @param <T>      The join target class
     * @return The GroupByFlow with a new instance of the target allocated to every key
     */
    @SuppressWarnings("all")
    public static <K, T> GroupByFlowBuilder<K, T> build(LambdaReflection.SerializableSupplier<T> target, MultiJoinLeg<K, T, ?>... joinLegs) {
        MultiJoinBuilder multiJoinBuilder = new MultiJoinBuilder(Object.class, target);
        for (MultiJoinLeg joinLeg : joinLegs) {
            multiJoinBuilder.addJoin(joinLeg.flow, joinLeg.setter);
        }
        return multiJoinBuilder.dataFlow();
    }

    public static <K1, T1, R> MultiJoinLeg<K1, T1, R> multiJoinLeg(GroupByFlowBuilder<K1, R> flow, LambdaReflection.SerializableBiConsumer<T1, R> setter) {
        return new MultiJoinLeg<>(flow, setter);
    }

    @Data
    public static class MultiJoinLeg<K, T, R> {
        private final GroupByFlowBuilder<K, R> flow;
        private final LambdaReflection.SerializableBiConsumer<T, R> setter;
    }

    public <K2 extends K, B> MultiJoinBuilder<K, T> addJoin(
            GroupByFlowBuilder<K2, B> flow1,
            LambdaReflection.SerializableBiConsumer<T, B> setter1) {
        multiLegJoin.addJoin(flow1.flowSupplier(), setter1);
        return this;
    }

    public <K2 extends K, B> MultiJoinBuilder<K, T> addOptionalJoin(
            GroupByFlowBuilder<K2, B> flow1,
            LambdaReflection.SerializableBiConsumer<T, B> setter1) {
        multiLegJoin.addOptionalJoin(flow1.flowSupplier(), setter1);
        return this;
    }

    public GroupByFlowBuilder<K, T> dataFlow() {
        EventProcessorBuilderService.service().add(multiLegJoin);
        return new GroupByFlowBuilder<>(multiLegJoin);
    }
}
