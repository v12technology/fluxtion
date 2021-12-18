package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.annotations.OnParentUpdate;

/**
 * A marker interface used by {@link TriggeredEventStream} to mark a stream function as stateful and should be reset when
 * the reset trigger fires. The reset trigger is set via {@link TriggeredEventStream#setUpdateTriggerNode(Object)}.
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
