/*
 * Copyright (c) 2019, V12 Technology Ltd.
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

import com.fluxtion.runtime.annotations.AfterTrigger;
import com.fluxtion.runtime.annotations.FilterId;
import com.fluxtion.runtime.annotations.OnEventHandler;

/**
 * @author Greg Higgins
 */
public class AnnotatedOnCompleteTestEventHandler {
    @FilterId
    private final int stateId;

    public AnnotatedOnCompleteTestEventHandler(int id) {
        this.stateId = id;
    }

    @OnEventHandler
    public boolean onTest(TestEvent e) {
        return true;
    }

    @AfterTrigger
    public void onCompleteEventProcessing() {

    }
}
