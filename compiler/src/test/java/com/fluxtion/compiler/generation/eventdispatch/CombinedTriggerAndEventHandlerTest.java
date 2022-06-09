package com.fluxtion.compiler.generation.eventdispatch;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import lombok.Data;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CombinedTriggerAndEventHandlerTest extends MultipleSepTargetInProcessTest {
    public CombinedTriggerAndEventHandlerTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void noRootClassTest(){
        sep(c -> c.addNode(new CombinedTriggerAndEventHandler(), "node"));
        CombinedTriggerAndEventHandler node = getField("node");
        assertFalse(node.isEventNotified());
        assertFalse(node.isTriggerNotified());

        onEvent("hello");
        assertTrue(node.isEventNotified());
        assertFalse(node.isTriggerNotified());
    }

    @Test
    public void withRootClassTest(){
        sep(c -> c.addNode(
                new Root(c.addNode(new CombinedTriggerAndEventHandler(), "node"))));
        CombinedTriggerAndEventHandler node = getField("node");
        assertFalse(node.isEventNotified());
        assertFalse(node.isTriggerNotified());

        onEvent("hello");
        assertTrue(node.isEventNotified());
        assertFalse(node.isTriggerNotified());
    }


    @Test
    public void withRootNoTriggerClassTest(){
        sep(c -> c.addNode(
                new RootNoTrigger(c.addNode(new CombinedTriggerAndEventHandler(), "node"))));
        CombinedTriggerAndEventHandler node = getField("node");
        assertFalse(node.isEventNotified());
        assertFalse(node.isTriggerNotified());

        onEvent("hello");
        assertTrue(node.isEventNotified());
        assertFalse(node.isTriggerNotified());
    }
    @Data
    public static class CombinedTriggerAndEventHandler {
        private boolean eventNotified;
        private boolean triggerNotified;

        @OnEventHandler
        public void stringUpdate(String in) {
            eventNotified = true;
        }

        @OnTrigger
        public void triggered() {
            triggerNotified = true;
        }
    }

    @Data
    public static class Root {
        private final Object parent;
        private boolean triggered;

        @OnTrigger
        public void parentTriggered() {
            triggered = true;
        }
    }

    @Data
    public static class RootNoTrigger {
        private final Object parent;
        private boolean triggered;

    }
}
