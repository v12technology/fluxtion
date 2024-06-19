package com.fluxtion.runtime.node;

import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.Start;
import com.fluxtion.runtime.annotations.Stop;
import com.fluxtion.runtime.annotations.TearDown;

public interface LifecycleNode {

    /**
     * callback received before any events are processed by the Static event
     * processor. Init methods are invoked in topological order. The {@link com.fluxtion.runtime.EventProcessor}
     * can only process events once init has completed.
     */
    @Initialise
    default void init() {
    }

    /**
     * invoke after init. Start methods are invoked in topological order. Start/stop can attach application nodes to
     * a life cycle method when the {@link com.fluxtion.runtime.EventProcessor} can process methods
     */
    @Start
    default void start() {
    }

    /**
     * invoke after start. Stop methods are invoked in reverse-topological order. Start/stop can attach application nodes to
     * a life cycle method when the {@link com.fluxtion.runtime.EventProcessor} can process methods
     */
    @Stop
    default void stop() {
    }

    /**
     * callback received after all events are processed by the Static event
     * processor, and no more are expected. tearDown methods are invoked in
     * reverse-topological order.
     */
    @TearDown
    default void tearDown() {
    }
}
