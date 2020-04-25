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
package com.fluxtion.ext.streaming.api;

import java.util.List;

/**
 * An ordered based collection of items that can be sorted or queried by position.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 * @param <T>
 */
public interface WrappedList<T> extends WrappedCollection<T, List<T>, WrappedList<T>> {

    WrappedList<T> top(int n);

    WrappedList<T> last(int n);

    WrappedList<T> skip(int n);

    WrappedList<T> reverse();

    default List<T> subList(int fromIndex, int toIndex) {
        return collection().subList(fromIndex, toIndex);
    }

}
