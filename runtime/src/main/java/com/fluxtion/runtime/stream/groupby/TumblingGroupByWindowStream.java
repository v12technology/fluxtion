package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.SepNode;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.AbstractEventStream;
import com.fluxtion.runtime.stream.EventStream;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import com.fluxtion.runtime.stream.aggregate.AggregateFunction;
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
public class TumblingGroupByWindowStream<T, K, V, R, S extends EventStream<T>, F extends AggregateFunction<V, R, F>>
        extends AbstractEventStream<T, GroupByStreamed<K, R>, S>
        implements TriggeredEventStream<GroupByStreamed<K, R>> {

    @SepNode
    @NoTriggerReference
    public GroupByWindowedCollection<T, K, V, R, F> groupByWindowedCollection;
    public FixedRateTrigger rollTrigger;

    private transient final Map<K, R> mapOfValues = new HashMap<>();
    private transient final MyGroupBy results = new MyGroupBy();

    public TumblingGroupByWindowStream(
            S inputEventStream,
            SerializableSupplier<F> windowFunctionSupplier,
            SerializableFunction<T, K> keyFunction,
            SerializableFunction<T, V> valueFunction,
            int windowSizeMillis) {
        this(inputEventStream);
        this.groupByWindowedCollection = new GroupByWindowedCollection<>(keyFunction, valueFunction, windowFunctionSupplier);
        rollTrigger = FixedRateTrigger.atMillis(windowSizeMillis);
    }

    public TumblingGroupByWindowStream(S inputEventStream) {
        super(inputEventStream, null);
    }

    @Override
    public GroupByStreamed<K, R> get() {
        return results;
    }

    protected void cacheWindowValue() {
        mapOfValues.clear();
        mapOfValues.putAll(groupByWindowedCollection.map());
    }

    protected void aggregateInputValue(S inputEventStream) {
        groupByWindowedCollection.aggregate(inputEventStream.get());
    }

    @OnParentUpdate
    public void timeTriggerFired(FixedRateTrigger rollTrigger) {
        cacheWindowValue();
        inputStreamTriggered_1 = true;
        inputStreamTriggered = true;
        groupByWindowedCollection.reset();
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
        mapOfValues.clear();
        groupByWindowedCollection.reset();
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
        public Map<K, R> map() {
            return mapOfValues;
        }

        @Override
        public Collection<R> values() {
            return mapOfValues.values();
        }

        @Override
        public R value() {
            return groupByWindowedCollection.value();
        }

        @Override
        public KeyValue<K, R> keyValue() {
            return groupByWindowedCollection.keyValue();
        }
    }
}
