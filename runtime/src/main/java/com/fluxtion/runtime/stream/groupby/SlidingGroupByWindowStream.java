package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.AggregateFunction;
import com.fluxtion.runtime.stream.EventStream;
import com.fluxtion.runtime.stream.GroupByStreamed;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import com.fluxtion.runtime.stream.aggregate.BucketedSlidingWindowedFunction;
import com.fluxtion.runtime.stream.impl.AbstractEventStream;
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
 * @param <S> {@link EventStream} input type
 * @param <F>
 */
public class SlidingGroupByWindowStream<T, K, V, R, S extends EventStream<T>, F extends AggregateFunction<V, R, F>>
        extends AbstractEventStream<T, GroupByStreamed<K, R>, S>
        implements TriggeredEventStream<GroupByStreamed<K, R>> {

    private final SerializableSupplier<F> windowFunctionSupplier;
    private final SerializableFunction<T, K> keyFunction;
    private final SerializableFunction<T, V> valueFunction;
    private final int bucketSizeMillis;
    private final int bucketCount;
    public FixedRateTrigger rollTrigger;
    private transient Supplier<GroupByWindowedCollection<T, K, V, R, F>> groupBySupplier;
    private transient BucketedSlidingWindowedFunction<T, GroupByStreamed<K, R>, GroupByWindowedCollection<T, K, V, R, F>> slidingCalculator;
    private transient final Map<K, R> mapOfValues = new HashMap<>();
    private transient final MyGroupBy results = new MyGroupBy();


    public SlidingGroupByWindowStream(
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
        groupBySupplier = () -> new GroupByWindowedCollection<>(keyFunction, valueFunction, windowFunctionSupplier);
        slidingCalculator = new BucketedSlidingWindowedFunction<>(groupBySupplier, bucketCount);
    }

    @Override
    public GroupByStreamed<K, R> get() {
        return results;
    }

    protected void cacheWindowValue() {
        GroupByStreamed<K, R> value = slidingCalculator.get();
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
        groupBySupplier = () -> new GroupByWindowedCollection<>(keyFunction, valueFunction, windowFunctionSupplier);
        slidingCalculator = new BucketedSlidingWindowedFunction<>(groupBySupplier, bucketCount);
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

    private class MyGroupBy implements GroupByStreamed<K, R> {

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
