/* 
 * Copyright (C) 2016-2017 V12 Technology Limited. All rights reserved. 
 *
 * This software is subject to the terms and conditions of its EULA, defined in the
 * file "LICENCE.txt" and distributed with this software. All information contained
 * herein is, and remains the property of V12 Technology Limited and its licensors, 
 * if any. This source code may be protected by patents and patents pending and is 
 * also protected by trade secret and copyright law. Dissemination or reproduction 
 * of this material is strictly forbidden unless prior written permission is 
 * obtained from V12 Technology Limited.  
 */
package com.fluxtion.api.event.test;

import com.fluxtion.runtime.lifecycle.BatchHandler;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;

public abstract class AbstractSepTestDecorator<T extends EventHandler & BatchHandler & Lifecycle> implements EventHandler, BatchHandler, Lifecycle {

    protected T sep;

    public AbstractSepTestDecorator(T sep) {
        this.sep = sep;
        sep.init();
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void onEvent(com.fluxtion.runtime.event.Event event) {

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
