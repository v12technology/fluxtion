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
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.event.Event;
import com.fluxtion.generator.util.BaseSepInprocessTest;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class

CombinedHandlerAndOnEventTest extends BaseSepInprocessTest {

    
    @Test
    public void testCombined(){
//        fixedPkg = true;
        sep((c) ->{
            CombinedHandling combiner = c.addPublicNode(new CombinedHandling(), "combiner");
            combiner.parent = c.addPublicNode(new HandlerB(), "handlerB");
        });
    }
    
    public static class CombinedHandling {
        
        public Object parent;
        
        @EventHandler
        public boolean processEventA(EventA in){
            return true;
        }
        
        @OnEvent
        public void onEvent(){
            
        }

    }
    
    public static class HandlerB{
        @EventHandler
        public boolean processEventB(EventB in){
            return true;
        }
    }

    public static class EventA implements Event {
    }

    public static class EventB implements Event {
    }

}
