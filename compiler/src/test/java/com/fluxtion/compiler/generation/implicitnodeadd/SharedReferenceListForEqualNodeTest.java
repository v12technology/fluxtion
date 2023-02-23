package com.fluxtion.compiler.generation.implicitnodeadd;

import com.fluxtion.compiler.generation.util.CompiledOnlySepTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import lombok.Value;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class SharedReferenceListForEqualNodeTest extends CompiledOnlySepTest {

    public SharedReferenceListForEqualNodeTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void equalNodeAddedMultipleTimesInListTest() {
        sep(c -> {
            c.addNode(
                    new MyListHolder(Arrays.asList(new KeyedStringHandler("A"), new KeyedStringHandler("A"))),
                    "holder");
        });
        onEvent("TEST");
        MyListHolder holder = getField("holder");
        Assert.assertTrue(holder.getHandler().get(0) == holder.getHandler().get(1));
    }

    @Test
    public void equalNodeAddedMultipleTimesInMultipleListTest() {
        writeSourceFile = true;
        sep(c -> {
                    c.addNode(
                            new MyListHolder(Arrays.asList(new KeyedStringHandler("A"), new KeyedStringHandler("A"))),
                            "holder");
                    c.addNode(
                            new MyListHolder2(Arrays.asList(new KeyedStringHandler("A"), new KeyedStringHandler("A"))),
                            "holder2");
                }
        );
        onEvent("TEST");
        MyListHolder holder = getField("holder");
        Assert.assertSame(holder.getHandler().get(0), holder.getHandler().get(1));
        MyListHolder2 holder2 = getField("holder2");
        Assert.assertSame(holder2.getHandler().get(0), holder2.getHandler().get(1));
    }

    @Test
    public void equalNodeAddedMultipleTimesInArrayTest() {
        sep(c -> {
            c.addNode(
                    new MyArrayHolder(new KeyedStringHandler[]{new KeyedStringHandler("A"), new KeyedStringHandler("A")}),
                    "holder");
        });
        onEvent("TEST");
        MyArrayHolder holder = getField("holder");
        Assert.assertTrue(holder.getHandler()[0] == holder.getHandler()[1]);
    }


    @Value
    public static class MyListHolder {

        List<KeyedStringHandler> handler;

        @OnTrigger
        public void update() {

        }

    }

    @Value
    public static class MyListHolder2 {

        List<KeyedStringHandler> handler;

        @OnTrigger
        public void update() {

        }

    }

    @Value
    public static class MyArrayHolder {

        KeyedStringHandler[] handler;

        @OnTrigger
        public void update() {

        }

    }

    @Value
    public static class KeyedStringHandler {

        String key;

        @OnEventHandler
        public void update(String in) {

        }
    }
}
