package com.fluxtion.runtime.dataflow;

import com.fluxtion.runtime.annotations.OnParentUpdate;

/**
 * A marker interface used by {@link TriggeredFlowFunction} to mark a stream function as stateful and should be reset when
 * the reset trigger fires. The reset trigger is set via {@link TriggeredFlowFunction#setUpdateTriggerNode(Object)}.
 */
public interface Stateful<T> {
    T reset();

    abstract class StatefulWrapper {

        private final Object resetTrigger;

        public StatefulWrapper(Object resetTrigger) {
            this.resetTrigger = resetTrigger;
        }

        @OnParentUpdate("resetTrigger")
        public void resetTrigger(Object resetTrigger) {
            reset();
        }

        protected abstract void reset();

    }
}
