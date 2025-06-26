/*
 * Copyright (c) 2025 gregory higgins.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */

package com.fluxtion.runtime.time;

/**
 * Represents an entity capable of scheduling and triggering actions after a specified delay.
 *
 * This interface defines a contract for components that need to initiate an action
 * or event after a given period of time has elapsed. Implementations may utilize
 * various scheduling mechanisms to achieve this functionality.
 */
public interface ScheduledTrigger {
    /**
     * Triggers an action or event after the specified delay in milliseconds.
     *
     * @param millis the delay in milliseconds after which the action should be triggered
     */
    void triggerAfterDelay(long millis);
}
