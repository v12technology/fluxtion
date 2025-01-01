/*
 * Copyright (c) 2019-2025 gregory higgins.
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
@Accessors(chain = true, fluent = true)
public class NamedFeedEventImpl<T> extends DefaultEvent implements NamedFeedEvent<T> {

    private String topic;
    private T data;
    private boolean delete;
    private long sequenceNumber;

    public NamedFeedEventImpl(String eventFeedName) {
        this(eventFeedName, null, 0, null);
    }

    public NamedFeedEventImpl(String eventFeedName, T data) {
        this(eventFeedName, null, 0, data);
    }

    public NamedFeedEventImpl(String filterId, long sequenceNumber, T data) {
        this(filterId, null, sequenceNumber, data);
    }

    public NamedFeedEventImpl(String eventFeedName, String topic, T data) {
        this(eventFeedName, topic, 0, data);
    }

    public NamedFeedEventImpl(String eventFeedName, String topic) {
        this(eventFeedName, topic, 0, null);
    }

    public NamedFeedEventImpl(String filterId, String topic, long sequenceNumber, T data) {
        super(filterId);
        this.sequenceNumber = sequenceNumber;
        this.topic = topic;
        this.data = data;
    }

    public NamedFeedEventImpl<T> copyFrom(NamedFeedEventImpl<T> other) {
        topic(other.topic);
        data(other.data);
        delete(other.delete);
        sequenceNumber(other.sequenceNumber);
        filterId = other.filterId;
        setEventFeedName(eventFeedName());
        setEventTime(getEventTime());
        return this;
    }

    public NamedFeedEventImpl<T> clone() {
        NamedFeedEventImpl<T> namedFeedEvent = new NamedFeedEventImpl<>(eventFeedName(), topic(), sequenceNumber(), data());
        namedFeedEvent.copyFrom(this);
        return namedFeedEvent;
    }

    public void setEventFeedName(String eventFeedName) {
        this.filterString = eventFeedName;
    }

    @Override
    public String eventFeedName() {
        return filterString;
    }

    @Override
    public String toString() {
        return "NamedFeedEvent{" +
                "eventFeed='" + filterString + '\'' +
                ", topic='" + topic + '\'' +
                ", sequenceNumber='" + sequenceNumber + '\'' +
                ", delete='" + delete + '\'' +
                ", data=" + data +
                ", eventTime=" + eventTime +
                '}';
    }
}
