package com.fluxtion.runtime.stream.aggregate;

import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.EventStream;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import com.fluxtion.runtime.time.FixedRateTrigger;

public class TimedSlidingWindowStream
        <T, R, S extends EventStream<T>, F extends BaseSlidingWindowFunction<T, R, F>>
        extends EventLogNode
        implements TriggeredEventStream<R> {

    @NoTriggerReference
    private final S inputEventStream;
    private final SerializableSupplier<F> windowFunctionSupplier;
    private final int buckets;
    private transient final BucketedSlidingWindowedFunction<T, R, F> windowFunction;
    public FixedRateTrigger rollTrigger;
    private R value;

    public TimedSlidingWindowStream(S inputEventStream, SerializableSupplier<F> windowFunctionSupplier, int windowSizeMillis, int buckets) {
        this(inputEventStream, windowFunctionSupplier, buckets);
        rollTrigger = FixedRateTrigger.atMillis(windowSizeMillis);
    }

    public TimedSlidingWindowStream(S inputEventStream, SerializableSupplier<F> windowFunctionSupplier, int buckets) {
        this.inputEventStream = inputEventStream;
        this.windowFunctionSupplier = windowFunctionSupplier;
        this.buckets = buckets;
        this.windowFunction = new BucketedSlidingWindowedFunction<>(windowFunctionSupplier, buckets);
    }

    @OnParentUpdate
    public void timeTriggerFired(FixedRateTrigger rollTrigger) {
        windowFunction.roll(rollTrigger.getTriggerCount());
    }

    @OnParentUpdate
    public void updateData(S inputEventStream) {
        windowFunction.aggregate(inputEventStream.get());
    }

    @OnTrigger
    public boolean triggered() {
        boolean publish = windowFunction.isAllBucketsFilled();
        if (publish) value = windowFunction.get();
        return publish;
    }

    @Override
    public R get() {
        return value;//windowFunction.get();
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
}
