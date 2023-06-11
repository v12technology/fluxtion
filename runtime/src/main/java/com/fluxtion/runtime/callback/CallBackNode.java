package com.fluxtion.runtime.callback;

import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.node.EventHandlerNode;
import com.fluxtion.runtime.node.SingleNamedNode;
import lombok.SneakyThrows;

/**
 * Extend this node to expose instance callback outside the {@link com.fluxtion.runtime.EventProcessor}.
 * Use the protected methods
 */
public abstract class CallBackNode extends SingleNamedNode implements EventHandlerNode {

    private transient Class<?> cbClass;
    private transient Object event;
    public String eventClassName;
    @Inject
    public EventDispatcher dispatcher;
    @Inject
    public DirtyStateMonitor dirtyStateMonitor;

    @SneakyThrows
    public CallBackNode(String name) {
        super(name);
    }

    @Override
    public boolean onEvent(Object e) {
        return true;
    }

    @Override
    @SneakyThrows
    public final Class<?> eventClass() {
        if (cbClass == null) {
            cbClass = InstanceCallbackEvent.cbClassList.remove(0);
            eventClassName = cbClass.getName();
        }
        return cbClass;
    }

    @Initialise
    @SneakyThrows
    public void init() {
        event = Class.forName(eventClassName).getDeclaredConstructor().newInstance();
    }

    /**
     * Trigger a callback calculation with this node as the root of the
     */
    protected final void triggerGraphCycle() {
        dispatcher.processAsNewEventCycle(event);
    }

    protected final void markDirty() {
        dirtyStateMonitor.markDirty(this);
    }

}
