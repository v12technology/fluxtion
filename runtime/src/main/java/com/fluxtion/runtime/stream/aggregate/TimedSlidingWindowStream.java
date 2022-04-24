package com.fluxtion.runtime.stream.aggregate;

import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.stream.EventStream;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import com.fluxtion.runtime.time.FixedRateTrigger;

import java.util.function.Supplier;

public class TimedSlidingWindowStream <T, R, S extends EventStream<T>, F extends BaseSlidingWindowFunction<T, R, F>> extends EventLogNode
        implements TriggeredEventStream<R> {

    @NoTriggerReference
    private final S inputEventStream;
    public FixedRateTrigger rollTrigger;

    public TimedSlidingWindowStream(S inputEventStream, Supplier<F> windowFunctionSupplier, int windowSizeMillis, int buckets){
        this.inputEventStream = inputEventStream;
        rollTrigger = FixedRateTrigger.atMillis(windowSizeMillis);


    }

    @Override
    public R get() {
        return null;
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
}
