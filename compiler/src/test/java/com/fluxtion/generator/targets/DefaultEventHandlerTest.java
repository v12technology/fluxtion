/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.generator.targets;

import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.test.event.DefaultFilteredEventHandler;
import com.fluxtion.test.event.TestEvent;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class DefaultEventHandlerTest {

    @Test
    public void testDefaultHandler() throws Exception {
        SEPConfig cfg = new SEPConfig();
        cfg.generateDescription = false;
        DefaultFilteredEventHandler<TestEvent> testHandler2 = new DefaultFilteredEventHandler<>(10, TestEvent.class);
        cfg.addPublicNode(testHandler2, "testHandlerimpl");
        JavaTestGeneratorHelper.generateClass(cfg, JavaGeneratorNames.test_defaulthandler);
    }
}
