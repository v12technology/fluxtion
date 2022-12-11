package com.fluxtion.runtime.lifecycle;

import com.fluxtion.runtime.annotations.OnTrigger;

/**
 * Triggered callback when a notification of an event propagation is signalled by a parent node. Can be used in place
 * of an {{@link OnTrigger}} annotation if preferred.
 *
 * @see OnTrigger
 */
public interface Node {

    /**
     * A callback invoked during a graph cycle when a parent indicates an event notification should be progagated.
     * Returns an event propagation flag:
     * <ul>
     *     <li>true - invoke child triggered methods</li>
     *     <li>false - do not invoke child triggered methods, consume the event propagation wave</li>
     * </ul>
     *
     * @return Event propagation flag
     */
    @OnTrigger
    public boolean triggered();

}
