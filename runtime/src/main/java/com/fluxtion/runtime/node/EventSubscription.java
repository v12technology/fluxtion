package com.fluxtion.runtime.node;

import com.fluxtion.runtime.event.Event;

import java.util.Objects;

public class EventSubscription<T> implements Event {
    private final int filterId;
    private final String filterString;
    private final Class<T> eventClass;
    private String toString;
    protected long eventTime;

    public EventSubscription(int filterId, String filterString, Class<T> eventClass) {
        this.filterId = filterId;
        this.filterString = filterString;
        this.eventClass = eventClass;
        this.eventTime = System.currentTimeMillis();
        toString = "EventSubscription{" +
                "eventClass=" + eventClass +
                (filterId == Integer.MAX_VALUE ? "" : ", filterId=" + filterId) +
                (filterString.isEmpty() ? "" : ", filterString=" + filterString) +
                '}';
        ;
    }

    public int filterId() {
        return filterId;
    }

    public String filterString() {
        return filterString;
    }

    public Class<T> eventClass() {
        return eventClass;
    }

    /**
     * Override the default value for event creation time. The default value is
     * set with {@link System#currentTimeMillis()} during construction. The
     * value must be greater than 0, otherwise the value is ignored
     *
     * @param eventTime
     */
    public void setEventTime(long eventTime) {
        if (eventTime > 0) {
            this.eventTime = eventTime;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventSubscription<?> that = (EventSubscription<?>) o;
        return filterId == that.filterId && filterString.equals(that.filterString) && eventClass.equals(that.eventClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filterId, filterString, eventClass);
    }

    @Override
    public String toString() {
        return toString;
    }
}
