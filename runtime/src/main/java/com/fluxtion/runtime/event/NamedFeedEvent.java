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

import com.fluxtion.runtime.util.CollectionHelper;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class NamedFeedEvent<T> extends DefaultEvent {

    @Setter
    private String topic;
    private List<T> data;
    @Getter
    @Setter
    private boolean delete;
    @Getter
    @Setter
    private long sequenceNumber;

    public NamedFeedEvent(String eventFeedName) {
        this(eventFeedName, null, null);
    }

    public NamedFeedEvent(String eventFeedName, List<T> data) {
        this(eventFeedName, null, data);
    }

    public NamedFeedEvent(String eventFeedName, String topic, List<T> data) {
        super(eventFeedName);
        this.topic = topic;
        this.data = data;
    }

    public NamedFeedEvent(String eventFeedName, String topic) {
        this(eventFeedName, topic, null);
    }

    public NamedFeedEvent<T> copyFrom(NamedFeedEvent<T> other) {
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

    public String getEventFeedName() {
        return filterString;
    }

    public String getTopic() {
        return topic;
    }

    public List<T> getData() {
        return data;
    }

    public NamedFeedEvent<T> setData(List<T> data) {
        this.data = data;
        return this;
    }

    @SafeVarargs
    public final NamedFeedEvent<T> setData(T... data) {
        setData(CollectionHelper.listOf(data));
        return this;
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
