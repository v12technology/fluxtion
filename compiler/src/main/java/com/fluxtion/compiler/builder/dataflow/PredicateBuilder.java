package com.fluxtion.compiler.builder.dataflow;


import com.fluxtion.runtime.dataflow.helpers.Predicates;
import com.fluxtion.runtime.dataflow.helpers.Predicates.AllUpdatedPredicate;

public class PredicateBuilder {

    /**
     * Fires a notification if all objects have fired a trigger at least once.
     *
     * @param obj the nodes to monitor
     * @return A node that triggers when all inputs have triggered at least once
     */
    static Object allChanged(Object... obj) {
        return new AllUpdatedPredicate(StreamHelper.getSourcesAsList(obj));
    }

    /**
     * Fires a notification if all objects have fired a trigger at least once. Monitor of inputs van be reset, after a
     * reset only the
     *
     * @param resetKey the trigger node that is monitored to reset the state
     * @param obj      the nodes to monitor
     * @return A node that triggers when all inputs have triggered at least once
     */
    static Object allChangedWithReset(Object resetKey, Object... obj) {
        return new AllUpdatedPredicate(StreamHelper.getSourcesAsList(obj), StreamHelper.getSource(resetKey));
    }

    /**
     * Fires a notification if any objects have fired a trigger in this event cycle
     *
     * @param obj the nodes to monitor
     * @return A node that triggers when all inputs have triggered at least once
     */
    static Object anyTriggered(Object... obj) {
        return new Predicates.AnyUpdatedPredicate(StreamHelper.getSourcesAsList(obj));
    }
}
