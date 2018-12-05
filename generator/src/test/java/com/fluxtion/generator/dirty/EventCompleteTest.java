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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.generator.dirty;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.OnEventComplete;
import com.fluxtion.api.node.SEPConfig;
import com.fluxtion.generator.dirty.DirtyElseTest.NumberEvent;
import com.fluxtion.generator.util.BaseSepTest;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author gregp
 */
public class EventCompleteTest extends BaseSepTest{
   
    
    @Test
    public void testComplete(){
        buildAndInitSep(EventCompleteBuilder.class);
        HandlerWithComplete handler = getField("completeHandler");
        onEvent(new NumberEvent(100));
        assertThat(handler.completeCount, is(1));
        assertThat(handler.eventCount, is(1));
        onEvent(new NumberEvent(1));
        assertThat(handler.completeCount, is(1));
        assertThat(handler.eventCount, is(2));
    }
    
    
    
    
    public static class EventCompleteBuilder extends SEPConfig{

        @Override
        public void buildConfig() {
            addPublicNode(new HandlerWithComplete(10), "completeHandler");
        }
        
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
