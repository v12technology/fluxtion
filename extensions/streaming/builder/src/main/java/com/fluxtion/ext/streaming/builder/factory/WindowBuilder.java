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

import com.fluxtion.ext.streaming.api.FilterWrapper;
import com.fluxtion.ext.streaming.api.Stateful;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.window.CountReset;
import com.fluxtion.ext.streaming.api.window.TimeReset;
import static com.fluxtion.ext.streaming.builder.factory.FilterByNotificationBuilder.filter;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class WindowBuilder {



    public static <S extends Stateful> FilterWrapper<S> tumble(S source, int count) {
        return filter(source, new CountReset(source, count));
    }

    public static <S, T extends Wrapper<S>> FilterWrapper<S> tumble(T source, int count) {
        return filter(source, new CountReset(source, count));
    }

    public static <S, T extends GroupBy<S>> FilterWrapper<T> tumble(T source, int count) {
        CountReset countBucketNotifier = new CountReset(source, count);
        return filter(source, countBucketNotifier);
    }

 
    public static <S extends Stateful> FilterWrapper<S> tumble(S source, Duration time) {
        return filter(source, new TimeReset(source, time.getMillis(), null));
    }
    
    public static <S, T extends Wrapper<S>> FilterWrapper<S> tumble(T source, Duration time) {
        return filter(source, new TimeReset(source, time.getMillis(), null));
    }
    
    public static <S, T extends GroupBy<S>> FilterWrapper<T> tumble(T source, Duration time) {
        return filter(source, new TimeReset(source, time.getMillis(), null));
    }

}
