package com.fluxtion.compiler.generation.anyobjectasevent;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import org.junit.Assert;
import org.junit.Test;

public class InterfaceEventTest extends MultipleSepTargetInProcessTest {

    public InterfaceEventTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void inerfaceEvent() {
        sep(c -> c.addNode(new MyInterfaceEventHandler(), "handler"));
        MyInterfaceEventHandler handler = getField("handler");
        Assert.assertEquals(0, handler.count);
        onEvent(new MyEvent());
        Assert.assertEquals(1, handler.count);
        onEvent(new Object());
        onEvent("TEST");
        onEvent(1);
        Assert.assertEquals(1, handler.count);
    }

    public interface GeneralEvent {
    }

    public static class MyEvent implements GeneralEvent {
    }

    public static class MyInterfaceEventHandler {
        public int count;

        @OnEventHandler
        public boolean updated(MyEvent myEvent) {
            count++;
            return false;
        }
    }
}
