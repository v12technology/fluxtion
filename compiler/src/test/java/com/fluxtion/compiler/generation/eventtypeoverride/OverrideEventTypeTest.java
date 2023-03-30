package com.fluxtion.compiler.generation.eventtypeoverride;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class OverrideEventTypeTest extends MultipleSepTargetInProcessTest {


    public OverrideEventTypeTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void overrideEvent() {
        sep(c -> {
            c.addNode(new MyHandler(), "handler");
        });

        onEvent(new MyEventSubclass());
        MatcherAssert.assertThat(getField("handler", MyHandler.class).interfaceCount, is(2));
    }

    public static interface MyEvent {
    }

    public static class MyEventSubclass implements MyEvent {
    }

    public static class MyHandler {

        public int interfaceCount = 0;

        @OnEventHandler(ofType = MyEventSubclass.class)
        public boolean subclassOverride(MyEvent myEventSubclass) {
            interfaceCount++;
            return true;
        }

        @OnEventHandler
        public boolean subClass(MyEventSubclass myEventSubclass) {
            interfaceCount++;
            return true;
        }
    }
}
