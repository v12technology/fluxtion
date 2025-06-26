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

/**
 * The SchedulerService interface provides mechanisms for scheduling actions to be executed
 * at a specific time or after a certain delay. It also includes methods for retrieving
 * the current time in various granularities (milliseconds, microseconds, and nanoseconds).
 * <p>
 * This interface is marked as {@link Experimental}, indicating it is an early-stage feature
 * subject to changes, instability, or incomplete functionality. Use in production environments
 * with caution.
 */
@Experimental
public interface SchedulerService {

    /**
     * Schedules a task to execute at the specified wall-clock time.
     *
     * @param expireTime   the absolute time, in milliseconds since the epoch, at which the task should be executed
     * @param expiryAction the action to perform when the specified time is reached
     * @return a unique identifier for the scheduled task
     */
    long scheduleAtTime(long expireTime, Runnable expiryAction);

    /**
     * Schedules a task to be executed after a specified delay.
     *
     * @param waitTime     the delay time, in milliseconds, after which the task should be executed
     * @param expiryAction the action to be executed when the delay period has elapsed
     * @return a unique identifier for the scheduled task
     */
    long scheduleAfterDelay(long waitTime, Runnable expiryAction);

    long milliTime();

    long microTime();

    long nanoTime();
}
