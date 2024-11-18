package com.fluxtion.runtime.event;

public class NamedFeedEvent<T> extends DefaultEvent {

    private String topic;
    private T data;

    public NamedFeedEvent(String eventFeedName) {
        this(eventFeedName, null);
    }

    public NamedFeedEvent(String eventFeedName, String topic) {
        super(eventFeedName);
        this.topic = topic;
    }

    public NamedFeedEvent<T> copyFrom(NamedFeedEvent<T> other) {
        setTopic(topic);
        setData(other.data);
        setEventFeedName(getEventFeedName());
        setEventTime(getEventTime());
        filterId = other.filterId;
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

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
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
