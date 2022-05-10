package com.fluxtion.runtime.stream.aggregate;

import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.EventStream;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import com.fluxtion.runtime.time.FixedRateTrigger;

public class TumblingWindowStream<T, R, S extends EventStream<T>, F extends BaseSlidingWindowFunction<T, R, F>>
        extends EventLogNode
        implements TriggeredEventStream<R> {

    @NoTriggerReference
    private final S inputEventStream;
    private final SerializableSupplier<F> windowFunctionSupplier;
    private final transient F windowFunction;
    public FixedRateTrigger rollTrigger;
    private R value;


    public TumblingWindowStream(S inputEventStream, SerializableSupplier<F> windowFunctionSupplier, int windowSizeMillis) {
        this(inputEventStream, windowFunctionSupplier);
        rollTrigger = FixedRateTrigger.atMillis(windowSizeMillis);
    }

    public TumblingWindowStream(S inputEventStream, SerializableSupplier<F> windowFunctionSupplier) {
        this.inputEventStream = inputEventStream;
        this.windowFunctionSupplier = windowFunctionSupplier;
        this.windowFunction = windowFunctionSupplier.get();
    }

    @Override
    public R get() {
        return value;
    }

    @OnParentUpdate
    public void timeTriggerFired(FixedRateTrigger rollTrigger) {
        value = windowFunction.get();
        windowFunction.reset();
    }

    @OnParentUpdate
    public void updateData(S inputEventStream) {
        windowFunction.aggregate(inputEventStream.get());
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
}
