/* 
 * Copyright (C) 2018 V12 Technology Ltd.
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
package com.fluxtion.api.event;

/**
 * Default implementation of {@link Event} that can be extended to provide
 * meta-data to user events.
 *
 * @author Greg Higgins
 *
 */
public abstract class DefaultEvent implements Event {

    public static final int NO_INT_FILTER = Integer.MAX_VALUE;
    public static final String NO_STRING_FILTER = "";

    protected int filterId;
    protected String filterString;
    protected long eventTime;

    public DefaultEvent() {
        this(NO_STRING_FILTER);
    }

    public DefaultEvent(String filterId) {
        this(NO_INT_FILTER, filterId);
    }

    public DefaultEvent(int filterId) {
        this(filterId, NO_STRING_FILTER);
    }

    public DefaultEvent(int filterId, String filterString) {
        this.filterId = filterId;
        this.filterString = filterString;
        this.eventTime = System.currentTimeMillis();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final int filterId() {
        return filterId;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final String filterString() {
        return filterString.toString();
    }

    public final CharSequence filterCharSequence() {
        return filterString;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long getEventTime() {
        return eventTime;
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

}
