package com.fluxtion.compiler.generation.eventdispatch;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.node.NamedNode;
import lombok.Data;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CombinedTriggerAndEventHandlerTest extends MultipleSepTargetInProcessTest {
    public CombinedTriggerAndEventHandlerTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void noRootClassTest() {
        sep(new CombinedTriggerAndEventHandler());
        CombinedTriggerAndEventHandler node = getField("node");
        assertFalse(node.isEventNotified());
        assertFalse(node.isTriggerNotified());

        onEvent("hello");
        assertTrue(node.isEventNotified());
        assertFalse(node.isTriggerNotified());
    }

    @Test
    public void withRootClassTest() {
        sep(new Root(new CombinedTriggerAndEventHandler()));
        CombinedTriggerAndEventHandler node = getField("node");
        Root root = getField("root");
        assertFalse(node.isEventNotified());
        assertFalse(node.isTriggerNotified());
        assertFalse(root.isTriggered());

        onEvent("hello");
        assertTrue(node.isEventNotified());
        assertFalse(node.isTriggerNotified());
        assertTrue(root.isTriggered());

        root.triggered = false;
        node.triggerNotified = false;
        node.eventNotified = false;

        onEvent(22);
        assertTrue(node.isEventNotified());
        assertFalse(node.isTriggerNotified());
        assertFalse(root.isTriggered());
    }


    @Test
    public void withRootNoTriggerClassTest() {
        sep(new RootNoTrigger(new CombinedTriggerAndEventHandler()));
        CombinedTriggerAndEventHandler node = getField("node");
        assertFalse(node.isEventNotified());
        assertFalse(node.isTriggerNotified());

        onEvent("hello");
        assertTrue(node.isEventNotified());
        assertFalse(node.isTriggerNotified());
    }

    @Data
    public static class CombinedTriggerAndEventHandler implements NamedNode {
        private boolean eventNotified;
        private boolean triggerNotified;

        @OnEventHandler
        public boolean stringUpdate(String in) {
            eventNotified = true;
            return true;
        }

        @OnEventHandler(propagate = false)
        public boolean intUpdate(int newValue) {
            eventNotified = true;
            return true;
        }

        @OnTrigger
        public boolean triggered() {
            triggerNotified = true;
            return true;
        }

        @Override
        public String getName() {
            return "node";
        }
    }

    @Data
    public static class Root implements NamedNode {
        private final Object parent;
        private boolean triggered;

        @OnTrigger
        public boolean parentTriggered() {
            triggered = true;
            return true;
        }

        @Override
        public String getName() {
            return "root";
        }
    }

    @Data
    public static class RootNoTrigger {
        private final Object parent;
        private boolean triggered;

    }
}
