package com.fluxtion.runtime.server.service;

import com.fluxtion.runtime.annotations.feature.Experimental;
import com.fluxtion.runtime.annotations.runtime.ServiceRegistered;
import com.fluxtion.runtime.callback.CallBackNode;

@Experimental
public class ScheduledTriggerNode extends CallBackNode {

    private SchedulerService schedulerService;

    @ServiceRegistered
    public void scheduler(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void triggerAfterDelay(long millis) {
        if (schedulerService != null) {
            schedulerService.scheduleAfterDelay(millis, this::triggerGraphCycle);
        }
    }
}
