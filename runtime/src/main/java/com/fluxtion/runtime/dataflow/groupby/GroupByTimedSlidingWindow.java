package com.fluxtion.runtime.dataflow.groupby;

import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.BucketedSlidingWindow;
import com.fluxtion.runtime.dataflow.function.AbstractFlowFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.time.FixedRateTrigger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


/**
 * @param <T> Input type
 * @param <R> Output of aggregate function
 * @param <K> Key type from input T
 * @param <V> Value type from input T, input to aggregate function
 * @param <S> {@link FlowFunction} input type
 * @param <F>
 */
public class GroupByTimedSlidingWindow<T, K, V, R, S extends FlowFunction<T>, F extends AggregateFlowFunction<V, R, F>>
        extends AbstractFlowFunction<T, GroupBy<K, R>, S>
        implements TriggeredFlowFunction<GroupBy<K, R>> {

    private final SerializableSupplier<F> windowFunctionSupplier;
    private final SerializableFunction<T, K> keyFunction;
    private final SerializableFunction<T, V> valueFunction;
    private final int bucketSizeMillis;
    private final int bucketCount;
    public FixedRateTrigger rollTrigger;
    private transient Supplier<GroupByFlowFunctionWrapper<T, K, V, R, F>> groupBySupplier;
    private transient BucketedSlidingWindow<T, GroupBy<K, R>, GroupByFlowFunctionWrapper<T, K, V, R, F>> slidingCalculator;
    private transient final Map<K, R> mapOfValues = new HashMap<>();
    private transient final MyGroupBy results = new MyGroupBy();


    public GroupByTimedSlidingWindow(
            S inputEventStream,
            SerializableSupplier<F> windowFunctionSupplier,
            @AssignToField("keyFunction")
            SerializableFunction<T, K> keyFunction,
            @AssignToField("valueFunction")
            SerializableFunction<T, V> valueFunction,
            @AssignToField("bucketSizeMillis")
            int bucketSizeMillis,
            @AssignToField("bucketCount")
            int bucketCount) {
        super(inputEventStream, null);
        this.windowFunctionSupplier = windowFunctionSupplier;
        this.keyFunction = keyFunction;
        this.valueFunction = valueFunction;
        this.bucketSizeMillis = bucketSizeMillis;
        this.bucketCount = bucketCount;
        resetTriggered = false;
        rollTrigger = FixedRateTrigger.atMillis(bucketSizeMillis);
        groupBySupplier = () -> new GroupByFlowFunctionWrapper<>(keyFunction, valueFunction, windowFunctionSupplier);
        slidingCalculator = new BucketedSlidingWindow<>(groupBySupplier, bucketCount);
    }

    @Override
    public GroupBy<K, R> get() {
        return results;
    }

    protected void cacheWindowValue() {
        GroupBy<K, R> value = slidingCalculator.get();
        mapOfValues.clear();
        mapOfValues.putAll(value.toMap());
    }

    protected void aggregateInputValue(S inputEventStream) {
        slidingCalculator.aggregate(inputEventStream.get());
    }

    @OnParentUpdate
    public void timeTriggerFired(FixedRateTrigger rollTrigger) {
        slidingCalculator.roll(rollTrigger.getTriggerCount());
        if (slidingCalculator.isAllBucketsFilled()) {
            cacheWindowValue();
            publishOverrideTriggered = !overridePublishTrigger & !overrideUpdateTrigger;
            inputStreamTriggered_1 = true;
            inputStreamTriggered = true;
        }
    }

    @OnParentUpdate
    public void inputUpdated(S inputEventStream) {
        aggregateInputValue(inputEventStream);
        inputStreamTriggered_1 = false;
        inputStreamTriggered = false;
    }

    @OnParentUpdate("updateTriggerNode")
    public void updateTriggerNodeUpdated(Object triggerNode) {
        super.updateTriggerNodeUpdated(triggerNode);
        cacheWindowValue();
    }

    @Override
    protected void resetOperation() {
        groupBySupplier = () -> new GroupByFlowFunctionWrapper<>(keyFunction, valueFunction, windowFunctionSupplier);
        slidingCalculator = new BucketedSlidingWindow<>(groupBySupplier, bucketCount);
        rollTrigger.init();
        mapOfValues.clear();
    }

    @Override
    public boolean isStatefulFunction() {
        return true;
    }

    @OnTrigger
    public boolean triggered() {
        return fireEventUpdateNotification();
    }

    private class MyGroupBy implements GroupBy<K, R> {

        @Override
        public Map<K, R> toMap() {
            return mapOfValues;
        }

        @Override
        public Collection<R> values() {
            return mapOfValues.values();
        }

        @Override
        public R lastValue() {
            return slidingCalculator.get().lastValue();
        }

        @Override
        public KeyValue<K, R> lastKeyValue() {
            return slidingCalculator.get().lastKeyValue();
        }
    }
}
