package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.EventStream;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import com.fluxtion.runtime.stream.aggregate.BaseSlidingWindowFunction;
import com.fluxtion.runtime.stream.aggregate.BucketedSlidingWindowedFunction;
import com.fluxtion.runtime.time.FixedRateTrigger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * @param <T> Input type
 * @param <R> Output of aggregate function
 * @param <K> Key type from input T
 * @param <V> Value type from input T, input to aggregate function
 * @param <S> {@link EventStream} input type
 * @param <F>
 */
public class SlidingGroupByWindowStream<T, K, V, R, S extends EventStream<T>, F extends BaseSlidingWindowFunction<V, R, F>>
        extends EventLogNode
        implements TriggeredEventStream<GroupByBatched<K, R>> {

    @NoTriggerReference
    private final S inputEventStream;
    private final SerializableSupplier<F> windowFunctionSupplier;
    private final SerializableFunction<T, K> keyFunction;
    private final SerializableFunction<T, V> valueFunction;
    private final int bucketSizeMillis;
    private final int bucketCount;
    public FixedRateTrigger rollTrigger;
    private final transient SerializableSupplier<GroupByCollection<T, K, V, R, F>> groupBySupplier;
    private final transient BucketedSlidingWindowedFunction<T, GroupBy<K, R>, GroupByCollection<T, K, V, R, F>> slidingCalculator;
    private transient final Map<K, R> mapOfValues = new HashMap<>();
    private transient final MyGroupByBatched results = new MyGroupByBatched();

    public SlidingGroupByWindowStream(
            S inputEventStream,
            SerializableSupplier<F> windowFunctionSupplier,
            SerializableFunction<T, K> keyFunction,
            SerializableFunction<T, V> valueFunction,
            int bucketSizeMillis,
            int bucketCount) {
        this.inputEventStream = inputEventStream;
        this.windowFunctionSupplier = windowFunctionSupplier;
        this.keyFunction = keyFunction;
        this.valueFunction = valueFunction;
        this.bucketSizeMillis = bucketSizeMillis;
        this.bucketCount = bucketCount;
        rollTrigger = FixedRateTrigger.atMillis(bucketSizeMillis);
        groupBySupplier = () -> new GroupByCollection<>(keyFunction, valueFunction, windowFunctionSupplier);
        slidingCalculator = new BucketedSlidingWindowedFunction<>(groupBySupplier, bucketCount);
    }


    @Override
    public GroupByBatched<K, R> get() {
        return results;
    }

    @OnParentUpdate
    public void timeTriggerFired(FixedRateTrigger rollTrigger) {
        slidingCalculator.roll(rollTrigger.getTriggerCount());
    }

    @OnParentUpdate
    public void updateData(S inputEventStream) {
        slidingCalculator.aggregate(inputEventStream.get());
    }

    @OnTrigger
    public boolean triggered() {
        boolean publish = slidingCalculator.isAllBucketsFilled();
        if (publish) {
            GroupBy<K, R> value = slidingCalculator.get();
            mapOfValues.clear();
            mapOfValues.putAll(value.map());
        }
        return publish;

    }

    @Override
    public void setUpdateTriggerNode(Object updateTriggerNode) {

    }

    @Override
    public void setPublishTriggerNode(Object publishTriggerNode) {

    }

    @Override
    public void setResetTriggerNode(Object resetTriggerNode) {

    }

    @Override
    public void setPublishTriggerOverrideNode(Object publishTriggerOverrideNode) {

    }

    private class MyGroupByBatched implements GroupByBatched<K, R>{

        @Override
        public Map<K, R> map() {
            return mapOfValues;
        }

        @Override
        public Collection<R> values() {
            return mapOfValues.values();
        }
    }
}
