package com.fluxtion.compiler.generation.eventdispatch;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.node.EventHandlerNode;
import com.fluxtion.runtime.node.TriggeredNode;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Supplier;

public class EventHandlerNodeTest extends MultipleSepTargetInProcessTest {

    public EventHandlerNodeTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testFilteredHandler() throws NoSuchMethodException {
        writeSourceFile = true;
        sep(c -> c.addNode(new FormatterNode(new MySubclass()), "node"));
        onEvent("hello");
        Assert.assertNull(getField("node", FormatterNode.class).formattedString());
        onEvent("notify-hello");
        Assert.assertEquals("NOTIFY-HELLO", getField("node", FormatterNode.class).formattedString());
    }

    public static class StringHandlerNode implements EventHandlerNode<String> {
        String received;

        @Override
        public boolean onEvent(String e) {
            received = e;
            return e.startsWith("notify");
        }

    }

    public static class MySubclass extends StringHandlerNode implements Supplier<String> {
        @Override
        public String get() {
            return received;
        }
    }

    @Data
    public static class FormatterNode implements TriggeredNode {
        final Supplier<String> stringSupplier;
        String formatted;

        @Override
        public boolean triggered() {
            formatted = stringSupplier.get().toUpperCase();
            return true;
        }

        public String formattedString() {
            return formatted;
        }
    }

}
