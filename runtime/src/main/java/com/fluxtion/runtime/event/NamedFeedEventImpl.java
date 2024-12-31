/*
 * SPDX-FileCopyrightText: © 2024 Gregory Higgins <greg.higgins@v12technology.com>
 * SPDX-License-Identifier: AGPL-3.0-only
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
