package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.dataflow.groupby.MultiJoin;
import com.fluxtion.runtime.partition.LambdaReflection;

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
