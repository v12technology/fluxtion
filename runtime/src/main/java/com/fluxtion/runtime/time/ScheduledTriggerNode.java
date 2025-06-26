/*
 * Copyright (C) 2019 2024 gregory higgins.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */

package com.fluxtion.runtime.time;

import com.fluxtion.runtime.annotations.feature.Experimental;
import com.fluxtion.runtime.annotations.runtime.ServiceRegistered;
import com.fluxtion.runtime.callback.AbstractCallbackNode;

/**
 * Represents a node that triggers scheduled actions using a {@link SchedulerService}.
 * This node extends {@link AbstractCallbackNode}, enabling it to fire callback events.
 * It supports scheduling events to occur after a specified delay. When the timer expires this node is the root of a
 * new event cycle.
 *
 * This class is marked as {@link Experimental}, meaning its features are subject to
 * change and its API should be used with caution in production environments.
 */
@Experimental
public class ScheduledTriggerNode extends AbstractCallbackNode<Object> implements ScheduledTrigger {

    private SchedulerService schedulerService;

    public ScheduledTriggerNode() {
        super();
    }

    public ScheduledTriggerNode(int filterId) {
        super(filterId);
    }

    @ServiceRegistered
    public void scheduler(SchedulerService scheduler) {
        this.schedulerService = scheduler;
    }

    @Override
    public void triggerAfterDelay(long millis) {
        if (schedulerService != null) {
            schedulerService.scheduleAfterDelay(millis, this::fireNewEventCycle);
        }
    }
}
