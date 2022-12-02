package com.fluxtion.compiler.generation.eventtypeoverride;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class OverrideEventTypeTest extends MultipleSepTargetInProcessTest {


    public OverrideEventTypeTest(boolean compiledSep) {
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
        public void subclassOverride(MyEvent myEventSubclass) {
            interfaceCount++;
        }

        @OnEventHandler
        public void subClass(MyEventSubclass myEventSubclass) {
            interfaceCount++;
        }
    }
}
