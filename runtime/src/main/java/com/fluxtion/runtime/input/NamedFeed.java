/*
 * Copyright (c) 2024-2025 gregory higgins.
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

package com.fluxtion.runtime.input;

import com.fluxtion.runtime.event.NamedFeedEvent;
import com.fluxtion.runtime.node.EventSubscription;

public interface NamedFeed extends EventFeed<EventSubscription<?>> {
    NamedFeedEvent<?>[] EMPTY_ARRAY = new NamedFeedEvent[0];

    @SuppressWarnings({"raw", "unchecked"})
    default <T> NamedFeedEvent<T>[] eventLog() {
        return (NamedFeedEvent<T>[]) (Object) EMPTY_ARRAY;
    }
}