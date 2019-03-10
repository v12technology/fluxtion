/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class CombinedHandlerAndOnEventTest extends BaseSepInprocessTest {

    
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

    public static class EventA extends Event {
    }

    public static class EventB extends Event {
    }

}
