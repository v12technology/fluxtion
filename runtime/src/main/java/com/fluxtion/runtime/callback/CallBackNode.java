package com.fluxtion.runtime.callback;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.node.EventHandlerNode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * Extend this node to expose instance callback outside the {@link com.fluxtion.runtime.EventProcessor}.
 * Use the protected methods
 */
@Getter
@Setter
public abstract class CallBackNode implements EventHandlerNode {

    private Object event;
    @Inject
    private EventDispatcher dispatcher;


    @SneakyThrows
    public CallBackNode() {
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
}
