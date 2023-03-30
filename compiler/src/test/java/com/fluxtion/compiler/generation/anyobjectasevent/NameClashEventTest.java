package com.fluxtion.compiler.generation.anyobjectasevent;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class NameClashEventTest extends MultipleSepTargetInProcessTest {

    public NameClashEventTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void eventNameClashTest() {
        sep(c -> {
            c.addNode(new MyEventHandler(), "handler");
        });
        onEvent(new Event());
        onEvent(new Event());
        onEvent(new Event());
        MatcherAssert.assertThat(getField("handler", MyEventHandler.class).count, is(3));
    }

    public static class Event {
    }

    public static class MyEventHandler {
        int count;

        @OnEventHandler
        public boolean event(Event event) {
            count++;
            return true;
        }
    }
}
