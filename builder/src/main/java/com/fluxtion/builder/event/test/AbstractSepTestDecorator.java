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
package com.fluxtion.builder.event.test;

import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.api.lifecycle.Lifecycle;

public abstract class AbstractSepTestDecorator<T extends EventHandler & BatchHandler & Lifecycle> implements EventHandler, BatchHandler, Lifecycle {

    protected T sep;

    public AbstractSepTestDecorator(T sep) {
        this.sep = sep;
        sep.init();
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void onEvent(com.fluxtion.api.event.Event event) {

        switch (event.eventId()) {
            case (AssertTestEvent.ID):
                switch(event.filterId()){
                    case(AssertTestEvent.FILTER_ID_INIT):
                        sep.init();
                        break;
                    case(AssertTestEvent.FILTER_ID_BATCH_PAUSE):
                        sep.batchPause();
                        break;
                    case(AssertTestEvent.FILTER_ID_BATCH_END):
                        sep.batchEnd();
                        break;
                    case(AssertTestEvent.FILTER_ID_TEARDOWN):
                        sep.tearDown();
                        break;
                    default:
                        AssertTestEvent testEvent = (AssertTestEvent) event;
                        testNode(testEvent);
                        break;
                }
            default:
                sep.onEvent(event);
        }
    }

    protected abstract void testNode(AssertTestEvent testEvent);

    @Override
    public void init() {
    }

    @Override
    public void tearDown() {
        sep.tearDown();
    }

    @Override
    public void batchPause() {
        sep.batchPause();
    }

    @Override
    public void batchEnd() {
        sep.batchPause();
    }

}
