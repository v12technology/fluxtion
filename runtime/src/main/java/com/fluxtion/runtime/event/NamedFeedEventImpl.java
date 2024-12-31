/*
 * Copyright (c) 2019, 2024 gregory higgins.
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

package com.fluxtion.runtime.event;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class NamedFeedEventImpl<T> extends DefaultEvent implements NamedFeedEvent<T> {

    private String topic;
    private T data;
    private boolean delete;
    private long sequenceNumber;

    public NamedFeedEventImpl(String eventFeedName) {
        this(eventFeedName, null, null);
    }

    public NamedFeedEventImpl(String eventFeedName, T data) {
        this(eventFeedName, null, data);
    }

    public NamedFeedEventImpl(String eventFeedName, String topic, T data) {
        super(eventFeedName);
        this.topic = topic;
        this.data = data;
    }

    public NamedFeedEventImpl(String eventFeedName, String topic) {
        this(eventFeedName, topic, null);
    }

    public NamedFeedEventImpl<T> copyFrom(NamedFeedEventImpl<T> other) {
        setTopic(other.topic);
        setData(other.data);
        setDelete(other.delete);
        filterId = other.filterId;
        setEventFeedName(getEventFeedName());
        setEventTime(getEventTime());
        return this;
    }

    public void setEventFeedName(String eventFeedName) {
        this.filterString = eventFeedName;
    }

    @Override
    public String getEventFeedName() {
        return filterString;
    }

    @Override
    public String toString() {
        return "NamedFeedEvent{" +
                "eventFeed='" + filterString + '\'' +
                ", topic='" + topic + '\'' +
                ", data=" + data +
                ", eventTime=" + eventTime +
                '}';
    }
}
