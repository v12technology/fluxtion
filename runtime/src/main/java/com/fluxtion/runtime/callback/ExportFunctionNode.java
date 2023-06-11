package com.fluxtion.runtime.callback;

import com.fluxtion.runtime.annotations.AfterEvent;
import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.Start;

public class ExportFunctionNode {

    private boolean triggered;

    @OnTrigger
    public boolean triggered() {
        return triggered;
    }

    @AfterEvent
    public void afterEvent() {
        triggered = false;
    }

    @Initialise
    public void init() {
        triggered = false;
    }

    @Start
    public void start() {
        triggered = false;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }
}
