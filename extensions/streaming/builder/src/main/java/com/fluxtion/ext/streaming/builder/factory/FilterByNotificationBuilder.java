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

import com.fluxtion.builder.generation.GenerationContext;
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

    public static <T> Wrapper<T> filter(Wrapper<T> trackedWrapped, Object notifier) {
        BooleanFilter<T> filter = new BooleanFilter<>(trackedWrapped, notifier);
        GenerationContext.SINGLETON.addOrUseExistingNode(filter);
        return filter;
    }

    public static <T> Wrapper<T> filter(T tracked, Object notifier) {
        BooleanFilter<T> filter = new BooleanFilter<>(tracked, notifier);
        GenerationContext.SINGLETON.addOrUseExistingNode(filter);
        return filter;
    }

    public static <T> Wrapper<T> filterEither(Wrapper<T> trackedWrapped, Object notifier) {
        BooleanEitherFilter<T> filter = new BooleanEitherFilter<>(trackedWrapped, notifier);
        GenerationContext.SINGLETON.addOrUseExistingNode(filter);
        return filter;
    }

    public static <T> Wrapper<T> filterEither(T tracked, Object notifier) {
        BooleanEitherFilter<T> filter = new BooleanEitherFilter<>(tracked, notifier);
        GenerationContext.SINGLETON.addOrUseExistingNode(filter);
        return filter;
    }

    public static <T> Wrapper<T> filterMatch(Wrapper<T> trackedWrapped, Object notifier) {
        BooleanMatchFilter<T> filter = new BooleanMatchFilter<>(trackedWrapped, notifier);
        GenerationContext.SINGLETON.addOrUseExistingNode(filter);
        return filter;
    }

    public static <T> Wrapper<T> filterMatch(T tracked, Object notifier) {
        BooleanMatchFilter<T> filter = new BooleanMatchFilter<>(tracked, notifier);
        GenerationContext.SINGLETON.addOrUseExistingNode(filter);
        return filter;
    }
}
