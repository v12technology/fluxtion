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
package com.fluxtion.generator.dirty;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.OnEventComplete;
import com.fluxtion.generator.dirty.DirtyElseTest.NumberEvent;
import com.fluxtion.generator.util.BaseSepInProcessTest;
import com.fluxtion.generator.util.MultipleSepTargetInProcessTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author gregp
 */
public class EventCompleteTest extends MultipleSepTargetInProcessTest {

    public EventCompleteTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testComplete(){
        sep((c) -> c.addPublicNode(new HandlerWithComplete(10), "completeHandler"));
        HandlerWithComplete handler = getField("completeHandler");
        onEvent(new NumberEvent(100));
        assertThat(handler.completeCount, is(1));
        assertThat(handler.eventCount, is(1));
        onEvent(new NumberEvent(1));
        assertThat(handler.completeCount, is(1));
        assertThat(handler.eventCount, is(2));
    }
    
    public static class HandlerWithComplete{
        
        final int barrier;
        private int completeCount;
        private int eventCount;

        public HandlerWithComplete(int barrier) {
            this.barrier = barrier;
        }
        
        @EventHandler
        public boolean numberEvent(NumberEvent event){
            eventCount++;
            return event.value > barrier;
        }

        @OnEventComplete
        public void eventComplete(){
            completeCount++;
        }
    }
    
}
