package com.fluxtion.compiler.generation.implicitnodeadd;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import lombok.Value;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class SharedReferenceForEqualNodeTest extends MultipleSepTargetInProcessTest {

    public SharedReferenceForEqualNodeTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void equalNodeAddedMultipleTimesTest() {
        sep(c -> {
            c.addNode(new MyHolder(
                    new KeyedStringHandler("A"),
                    new KeyedStringHandler("A")), "holder");
        });
        onEvent("TEST");
        MyHolder holder = getField("holder");
        Assert.assertTrue(holder.handler1 == holder.handler2);
    }


    public static class MyHolder {
        private final KeyedStringHandler handler1;
        private final KeyedStringHandler handler2;

        public MyHolder(
                @AssignToField("handler1")
                KeyedStringHandler handler1,
                @AssignToField("handler2")
                KeyedStringHandler handler2) {
            this.handler1 = handler1;
            this.handler2 = handler2;
        }

        @OnTrigger
        public boolean update() {
            return true;
        }
    }

    @Value
    public static class MyListHolder {

        List<KeyedStringHandler> handler;

        @OnTrigger
        public boolean update() {
            return true;
        }

    }

    @Value
    public static class KeyedStringHandler {

        String key;

        @OnEventHandler
        public boolean update(String in) {
            return true;
        }
    }
}
