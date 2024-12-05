/*
 * SPDX-FileCopyrightText: © 2024 Gregory Higgins <greg.higgins@v12technology.com>
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package com.fluxtion.runtime.callback;

import java.util.Iterator;

public interface Callback<R> {

    /**
     * fire a callback so this instance will be invoked when the current event cycle finishes
     */
    void fireCallback();

    /**
     * fire a callback so this instance will be invoked when the current event cycle finishes. Data is passed to this
     * instance when it is called back
     *
     * @param data
     */
    void fireCallback(R data);

    /**
     * fire a callback for each item to iterate this instance will be invoked when the current event cycle finishes. Data is passed to this
     * instance when it is called back for each iteration.
     *
     * @param dataIterator the data iterator for callback data
     */
    void fireCallback(Iterator<R> dataIterator);

    /**
     * Fires a new event cycle, with this callback executed after any queued events have completed. Data is passed to this
     * instance when it is called back
     *
     * @param data
     */
    void fireNewEventCycle(R data);

    /**
     * Fires a new event cycle, with this callback executed after any queued events have completed.
     */
    default void fireNewEventCycle() {
        fireNewEventCycle((R) null);
    }

    /**
     * Fires a new event cycle and callback for each item to iterate this instance will be invoked when the current event cycle finishes. Data is passed to this
     * instance when it is called back for each iteration.
     *
     * @param dataIterator the data iterator for callback data
     */
    default void fireNewEventCycle(Iterator<R> dataIterator) {
        while (dataIterator.hasNext()) {
            R nextItem = dataIterator.next();
            fireNewEventCycle(nextItem);
        }
    }

    /**
     * Any data associated with the callback invocation methods
     *
     * @return the data attached to the callback
     */
    R get();
}
