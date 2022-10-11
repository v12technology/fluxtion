package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.EventStream;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import com.fluxtion.runtime.stream.aggregate.AggregateFunction;
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
public class SlidingGroupByWindowStream<T, K, V, R, S extends EventStream<T>, F extends AggregateFunction<V, R, F>>
        extends EventLogNode
        implements TriggeredEventStream<GroupBy<K, R>> {

    @NoTriggerReference
    private final S inputEventStream;
    private final SerializableSupplier<F> windowFunctionSupplier;
    private final SerializableFunction<T, K> keyFunction;
    private final SerializableFunction<T, V> valueFunction;
    private final int bucketSizeMillis;
    private final int bucketCount;
    public FixedRateTrigger rollTrigger;
    private transient SerializableSupplier<GroupByWindowedCollection<T, K, V, R, F>> groupBySupplier;
    private transient BucketedSlidingWindowedFunction<T, GroupByStreamed<K, R>, GroupByWindowedCollection<T, K, V, R, F>> slidingCalculator;
    private transient final Map<K, R> mapOfValues = new HashMap<>();
    private transient final MyGroupBy results = new MyGroupBy();
    private Object resetTriggerNode;
    private boolean resetTriggered;

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
        resetTriggered = false;
        rollTrigger = FixedRateTrigger.atMillis(bucketSizeMillis);
        groupBySupplier = () -> new GroupByWindowedCollection<>(keyFunction, valueFunction, windowFunctionSupplier);
        slidingCalculator = new BucketedSlidingWindowedFunction<>(groupBySupplier, bucketCount);
    }


    @Override
    public GroupBy<K, R> get() {
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
        if (resetTriggered) {
            groupBySupplier = () -> new GroupByWindowedCollection<>(keyFunction, valueFunction, windowFunctionSupplier);
            slidingCalculator = new BucketedSlidingWindowedFunction<>(groupBySupplier, bucketCount);
            rollTrigger.init();
            mapOfValues.clear();
            publish = true;
        } else if (publish) {
            GroupByStreamed<K, R> value = slidingCalculator.get();
            mapOfValues.clear();
            mapOfValues.putAll(value.map());
        }
        resetTriggered = false;
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
        this.resetTriggerNode = resetTriggerNode;
    }

    @Override
    public void setPublishTriggerOverrideNode(Object publishTriggerOverrideNode) {

    }


    @OnParentUpdate("resetTriggerNode")
    public final void resetTriggerNodeUpdated(Object triggerNode) {
        resetTriggered = true;
    }

    private class MyGroupBy implements GroupBy<K, R> {

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
