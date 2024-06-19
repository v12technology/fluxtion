package com.fluxtion.runtime.callback;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.node.EventHandlerNode;
import com.fluxtion.runtime.node.SingleNamedNode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * Extend this node to expose instance callback outside the {@link com.fluxtion.runtime.EventProcessor}.
 * Use the protected methods
 */
@Getter
@Setter
public abstract class CallBackNode extends SingleNamedNode implements EventHandlerNode {

    private Object event;
    @Inject
    private EventDispatcher dispatcher;
    @Inject
    private DirtyStateMonitor dirtyStateMonitor;
    private final String name;

    @SneakyThrows
    public CallBackNode(String name) {
        super(name);
        this.name = name;
        if (EventProcessorBuilderService.service().buildTime()) {
            Class<?> cbClass = InstanceCallbackEvent.cbClassList.remove(0);
            event = cbClass.getDeclaredConstructor().newInstance();
        }
    }

    @Override
    public boolean onEvent(Object e) {
        return true;
    }


    @Override
    @SneakyThrows
    public final Class<?> eventClass() {
        return event.getClass();
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
