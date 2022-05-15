package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.SepNode;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.EventStream;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import com.fluxtion.runtime.stream.aggregate.BaseSlidingWindowFunction;
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
public class TumblingGroupByWindowStream<T, K, V, R, S extends EventStream<T>, F extends BaseSlidingWindowFunction<V, R, F>>
        extends EventLogNode
        implements TriggeredEventStream<GroupByBatched<K, R>> {

    @NoTriggerReference
    private final S inputEventStream;
    @SepNode
    @NoTriggerReference
    public GroupByCollection<T, K, V, R, F> groupByCollection;
    public FixedRateTrigger rollTrigger;

    private transient final Map<K, R> mapOfValues = new HashMap<>();
    private transient final MyGroupByBatched results = new MyGroupByBatched();

    public TumblingGroupByWindowStream(
            S inputEventStream,
            SerializableSupplier<F> windowFunctionSupplier,
            SerializableFunction<T, K> keyFunction,
            SerializableFunction<T, V> valueFunction,
            int windowSizeMillis) {
        this(inputEventStream);
        this.groupByCollection = new GroupByCollection<>(keyFunction, valueFunction, windowFunctionSupplier);
        rollTrigger = FixedRateTrigger.atMillis(windowSizeMillis);
    }

    public TumblingGroupByWindowStream(S inputEventStream) {
        this.inputEventStream = inputEventStream;
    }

    @Override
    public GroupByBatched<K, R> get() {
        return results;
    }

    @OnParentUpdate
    public void timeTriggerFired(FixedRateTrigger rollTrigger) {
        mapOfValues.clear();
        mapOfValues.putAll(groupByCollection.map());
        groupByCollection.reset();
    }

    @OnParentUpdate
    public void updateData(S inputEventStream) {
        groupByCollection.aggregate(inputEventStream.get());
    }

    @OnTrigger
    public boolean triggered() {
        return true;
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
