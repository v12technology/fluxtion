package com.fluxtion.runtime.node;

import com.fluxtion.runtime.annotations.OnTrigger;

/**
 * Triggered callback when an event propagation is signalled by a parent node. Can be used in place
 * of an {{@link OnTrigger}} annotation if preferred.
 * <p>
 * At least one of the tree of dependencies of this instance must be an {@link EventHandlerNode} or have a method annotated with an {@link com.fluxtion.runtime.annotations.OnEventHandler}
 * annotation for the trigger method to be in a event dispatch call stack.
 *
 * @see OnTrigger
 */
public interface TriggeredNode {

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
    boolean triggered();

}
