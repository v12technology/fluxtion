/*
 * Copyright (c) 2020, V12 Technology Ltd.
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
package com.fluxtion.ext.streaming.builder.factory;

import com.fluxtion.api.SepContext;
import static com.fluxtion.api.SepContext.service;
import com.fluxtion.ext.streaming.api.FilterWrapper;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.test.BooleanEitherFilter;
import com.fluxtion.ext.streaming.api.test.BooleanFilter;
import com.fluxtion.ext.streaming.api.test.BooleanMatchFilter;

/**
 * A set of utility functions that override the standard notification of a node in the graph. Notifications are externalised
 * to another graph node. Supported modes:
 * <ul>
 * <li>notifierOverride - notifications of the subject are only triggered by an external trigger instance
 * <li>notifierMerge - notifications of the subject are triggered if either it or the external trigger notifies
 * <li>notifyOnMatch - notifications of the subject are triggered if both it and the external trigger notifies in the same cycle
 * </ul>
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class NotificationBuilder {

    /**
     * Overrides the event notification for a tracked object. Allows a
     * child node to depend
     * on a parent and will be notified when a separate notifier instance
     * triggers an update.
     *
     * @param <T> The Type of the tracked object
     * @param trackedWrapped The wrapped tracked instance
     * @param notifier The triggering instance
     * @return The tracked instance as a FilterWrapper
     */
    public static <T> FilterWrapper<T> notifierOverride(Wrapper<T> trackedWrapped, Object notifier) {
        BooleanFilter<T> filter = new BooleanFilter<>(service().addOrReuse(trackedWrapped), service().addOrReuse(notifier));
        SepContext.service().addOrReuse(filter);
        return filter;
    }

    /**
     * Overrides the event notification for a tracked object. Allows a
     * child node to depend
     * on a parent and will be notified when a separate notifier instance
     * triggers an update.
     *
     * @param <T> The Type of the tracked object
     * @param tracked The wrapped tracked instance
     * @param notifier The triggering instance
     * @return The tracked instance as a FilterWrapper
     */
    public static <T> FilterWrapper<T> notifierOverride(T tracked, Object notifier) {
        BooleanFilter<T> filter = new BooleanFilter<>(service().addOrReuse(tracked), service().addOrReuse(notifier));
        service().addOrReuse(filter);
        return filter;
    }

    /**
     * Overrides the event notification for a tracked object. Allows a
     * child node to depend on a parent and will be notified when either the tracked instance or an independent notifier 
     * triggers an update or the .
     * 
     * @param <T> The Type of the tracked object
     * @param trackedWrapped The wrapped tracked instance
     * @param notifier The triggering instance
     * @return The tracked instance as a FilterWrapper
     */
    public static <T> FilterWrapper<T> notifierMerge(Wrapper<T> trackedWrapped, Object notifier) {
        BooleanEitherFilter<T> filter = new BooleanEitherFilter<>(trackedWrapped, notifier);
        service().addOrReuse(filter);
        return filter;
    }

    /**
     * Overrides the event notification for a tracked object. Allows a
     * child node to depend on a parent and will be notified when either the tracked instance or an independent notifier 
     * triggers an update.
     * 
     * @param <T> The Type of the tracked object
     * @param tracked The wrapped tracked instance
     * @param notifier The triggering instance
     * @return The tracked instance as a FilterWrapper
     */
    public static <T> FilterWrapper<T> notifierMerge(T tracked, Object notifier) {
        BooleanEitherFilter<T> filter = new BooleanEitherFilter<>(service().addOrReuse(tracked), SepContext.service().addOrReuse(notifier));
        service().addOrReuse(filter);
        return filter;
    }

    /**
     * Overrides the event notification for a tracked object. Allows a
     * child node to depend on a parent and will be notified when both the tracked instance and an independent notifier 
     * triggers an update.
     * 
     * @param <T> The Type of the tracked object
     * @param trackedWrapped The wrapped tracked instance
     * @param notifier The triggering instance
     * @return The tracked instance as a FilterWrapper
     */
    public static <T> Wrapper<T> notifyOnMatch(Wrapper<T> trackedWrapped, Object notifier) {
        BooleanMatchFilter<T> filter = new BooleanMatchFilter<>(service().addOrReuse(trackedWrapped), service().addOrReuse(notifier));;
        service().addOrReuse(filter);
        return filter;
    }

    /**
     * Overrides the event notification for a tracked object. Allows a
     * child node to depend on a parent and will be notified when both the tracked instance and an independent notifier 
     * triggers an update.
     * 
     * @param <T> The Type of the tracked object
     * @param tracked The wrapped tracked instance
     * @param notifier The triggering instance
     * @return The tracked instance as a FilterWrapper
     */
    public static <T> FilterWrapper<T> notifyOnMatch(T tracked, Object notifier) {
        BooleanMatchFilter<T> filter = new BooleanMatchFilter<>(service().addOrReuse(tracked), service().addOrReuse(notifier));
        service().addOrReuse(filter);
        return filter;
    }
}
