package com.fluxtion.compiler.generation.callback;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.callback.EventDispatcher;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EventDispatchTest extends MultipleSepTargetInProcessTest {
    public EventDispatchTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void nodeGeneratesNewEventCyclesTest() {
        sep(c -> c.addNode(new EventGeneratingNode(), "target"));
        EventGeneratingNode target = getField("target");
        Assert.assertNull(target.in);
        target.postEvent("hello");
        assertThat(target.in, is("hello"));
    }

    public static class EventGeneratingNode {

        @Inject
        public EventDispatcher dispatcher;

        String in;

        @OnEventHandler
        public void stringEvent(String in) {
            this.in = in;
        }

        public void postEvent(String newInput) {
            dispatcher.processAsNewEventCycle(newInput);
        }
    }
}
