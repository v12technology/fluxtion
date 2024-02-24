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
package com.fluxtion.test.event;

import com.fluxtion.runtime.annotations.OnEventHandler;

/**
 * @author Greg Higgins
 */
public class AnnotatedEventHandlerWithOverrideFilter {

    public static final int FILTER_ID_TIME = 100;
    public static final String FILTER_STRING_TEST = "testMatch";

    @OnEventHandler(filterId = FILTER_ID_TIME)
    public boolean onFilteredTime(TimeEvent e) {
        return true;
    }

    @OnEventHandler(filterString = FILTER_STRING_TEST)
    public boolean onFilteredTest(TestEvent e) {
        return true;
    }

    @OnEventHandler
    public boolean onAllTImeEvents(TimeEvent e) {
        return true;
    }
}
