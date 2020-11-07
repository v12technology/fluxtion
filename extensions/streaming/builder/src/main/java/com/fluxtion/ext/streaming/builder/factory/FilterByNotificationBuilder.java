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
 * A set of utility functions that filter the subject dependent upon
 * notifications processed.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class FilterByNotificationBuilder {

    /**
     * Separates triggers and tracked object for event notification. Allows a
     * child node to depend
     * on a parent and will be notified when a separate notifier instance
     * triggers an update.
     *
     * @param <T> The Type of the tracked object
     * @param trackedWrapped The wrapped tracked instance
     * @param notifier The triggering instance
     * @return The tracked instance as a FilterWrapper
     */
    public static <T> FilterWrapper<T> filter(Wrapper<T> trackedWrapped, Object notifier) {
        BooleanFilter<T> filter = new BooleanFilter<>(service().addOrReuse(trackedWrapped), service().addOrReuse(notifier));
        SepContext.service().addOrReuse(filter);
        return filter;
    }

    /**
     * Separates triggers and tracked object for event notification. Allows a
     * child node to depend
     * on a parent and will be notified when a separate notifier instance
     * triggers an update.
     *
     * @param <T> The Type of the tracked object
     * @param tracked The wrapped tracked instance
     * @param notifier The triggering instance
     * @return The tracked instance as a FilterWrapper
     */
    public static <T> FilterWrapper<T> filter(T tracked, Object notifier) {
        BooleanFilter<T> filter = new BooleanFilter<>(service().addOrReuse(tracked), service().addOrReuse(notifier));
        service().addOrReuse(filter);
        return filter;
    }

    public static <T> FilterWrapper<T> filterEither(Wrapper<T> trackedWrapped, Object notifier) {
        BooleanEitherFilter<T> filter = new BooleanEitherFilter<>(trackedWrapped, notifier);
        service().addOrReuse(filter);
        return filter;
    }

    public static <T> FilterWrapper<T> filterEither(T tracked, Object notifier) {
        BooleanEitherFilter<T> filter = new BooleanEitherFilter<>(service().addOrReuse(tracked), SepContext.service().addOrReuse(notifier));
        service().addOrReuse(filter);
        return filter;
    }

    public static <T> Wrapper<T> filterMatch(Wrapper<T> trackedWrapped, Object notifier) {
        BooleanMatchFilter<T> filter = new BooleanMatchFilter<>(service().addOrReuse(trackedWrapped), service().addOrReuse(notifier));;
        service().addOrReuse(filter);
        return filter;
    }

    public static <T> FilterWrapper<T> filterMatch(T tracked, Object notifier) {
        BooleanMatchFilter<T> filter = new BooleanMatchFilter<>(service().addOrReuse(tracked), service().addOrReuse(notifier));
        service().addOrReuse(filter);
        return filter;
    }
}
