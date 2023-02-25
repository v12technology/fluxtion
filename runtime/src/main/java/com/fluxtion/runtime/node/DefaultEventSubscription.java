package com.fluxtion.runtime.node;

import java.util.Objects;

public class DefaultEventSubscription<T> {
    private final int filterId;
    private final String filterString;
    private final Class<T> eventClass;

    public DefaultEventSubscription(int filterId, String filterString, Class<T> eventClass) {
        this.filterId = filterId;
        this.filterString = filterString;
        this.eventClass = eventClass;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultEventSubscription<?> that = (DefaultEventSubscription<?>) o;
        return filterId == that.filterId && filterString.equals(that.filterString) && eventClass.equals(that.eventClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filterId, filterString, eventClass);
    }

    @Override
    public String toString() {
        return "DefaultEventSubscription{" +
                "filterId=" + filterId +
                ", filterString='" + filterString + '\'' +
                ", eventClass=" + eventClass +
                '}';
    }
}
