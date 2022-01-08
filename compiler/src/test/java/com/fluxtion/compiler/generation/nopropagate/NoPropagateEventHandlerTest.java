package com.fluxtion.compiler.generation.nopropagate;

import com.fluxtion.runtime.annotations.EventHandler;
import com.fluxtion.runtime.annotations.OnEvent;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import lombok.Data;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class NoPropagateEventHandlerTest extends MultipleSepTargetInProcessTest {

    public NoPropagateEventHandlerTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void noPropagateFromEventHandler(){
        sep(c ->{
            c.addPublicNode(new CountingNode(new StringHandler()), "countingNode");
        });
        CountingNode countingNode = getField("countingNode");
        onEvent("ignore me");
        assertThat(countingNode.getCount(), is(0));
    }

    @Test
    public void partialPropagationFromEventHandler(){
        sep(c ->{
            c.addPublicNode(new CountingNode(new MultiHandler()), "countingNode");
        });
        CountingNode countingNode = getField("countingNode");
        onEvent("ignore me");
        assertThat(countingNode.getCount(), is(0));

        onEvent(111);
        assertThat(countingNode.getCount(), is(1));

    }

    public static class StringHandler {

        boolean notified = false;

        @EventHandler(propagate = false)
        public boolean newString(String s) {
            notified = true;
            return true;
        }
    }
    public static class MultiHandler {

        boolean notified = false;

        @EventHandler(propagate = false)
        public boolean newString(String s) {
            notified = true;
            return true;
        }

        @EventHandler
        public boolean newInt(Integer s) {
            notified = true;
            return true;
        }
    }

    @Data
    public static class CountingNode {

        final Object parent;
        int count;

        @OnEvent
        public void onEvent(){
            count++;
        }
    }
}
