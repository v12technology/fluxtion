/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.compiler.generation.anyobjectasevent;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.test.event.DefaultFilteredEventHandler;
import com.fluxtion.test.event.TestEvent;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class DefaultEventHandlerTest extends MultipleSepTargetInProcessTest {

    public DefaultEventHandlerTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testDefaultHandler() throws Exception {
        sep(c ->{
            c.addNode(new DefaultFilteredEventHandler<>(10, TestEvent.class), "testHandlerimpl");
        });
        DefaultFilteredEventHandler<TestEvent> handler = getField("testHandlerimpl");
        onEvent(new TestEvent());
        Assert.assertNull(handler.event);

        onEvent(new TestEvent(10));
        Assert.assertEquals(10, handler.event.filterId());
    }
}
