package com.fluxtion.runtime.callback;

import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.Start;

public class ExportFunctionNode {

    private boolean triggered;
    private boolean functionTriggered;

    @OnTrigger
    public final boolean triggered() {
        boolean tempTriggered = triggered;
        boolean tempFunctionTriggered = functionTriggered;
        triggered = false;
        functionTriggered = false;
        if (tempFunctionTriggered) {
            return tempTriggered;
        } else {
            return propagateParentNotification();
        }
    }

    /**
     * Overriding classes should subclass this method if they want to be notified
     * when parents have triggered a notification.
     *
     * @return flag to propagate event notification, true -> propagate, false -> swallow
     */
    protected boolean propagateParentNotification() {
        return true;
    }

    //    @AfterEvent
    public final void afterEvent() {
        triggered = false;
        functionTriggered = false;
    }

    @Initialise
    public final void init() {
        triggered = false;
        functionTriggered = false;
    }

    @Start
    public final void start() {
        triggered = false;
        functionTriggered = false;
    }

    public final boolean isTriggered() {
        return triggered;
    }

    public final void setTriggered(boolean triggered) {
        this.triggered = triggered;
        functionTriggered = true;
    }
}
