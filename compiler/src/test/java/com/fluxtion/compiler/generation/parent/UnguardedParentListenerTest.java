package com.fluxtion.compiler.generation.parent;

import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import lombok.Data;
import lombok.Value;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UnguardedParentListenerTest extends MultipleSepTargetInProcessTest {
    public UnguardedParentListenerTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testClassFilter() {
        sep(cfg -> {
            cfg.addPublicNode(new Counter(new FilterHandler("match me")), "counter");
        });
        onEvent("no match");
        Counter counter = getField("counter");
        assertThat(counter.getParentCount(), is(1));
        assertThat(counter.getEventCount(), is(0));
        onEvent("match me");
        assertThat(counter.getParentCount(), is(2));
        assertThat(counter.getEventCount(), is(1));
    }

    @Value
    public static class FilterHandler {

        String filter;

        @OnEventHandler
        public boolean checkString(String s) {
            return filter.equalsIgnoreCase(s);
        }
    }

    @Data
    public static class Counter {

        final FilterHandler parent;
        int eventCount;
        int parentCount;

        @OnParentUpdate(guarded = false)
        public void parentUpdated(FilterHandler marketHandler) {
            parentCount++;
        }

        @OnTrigger
        public void onEvent() {
            eventCount++;
        }
    }
}
