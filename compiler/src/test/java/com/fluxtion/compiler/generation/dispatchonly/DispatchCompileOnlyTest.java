package com.fluxtion.compiler.generation.dispatchonly;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.DispatchOnlySepTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.SepNode;
import org.junit.Assert;
import org.junit.Test;

public class DispatchCompileOnlyTest extends DispatchOnlySepTest {
    public DispatchCompileOnlyTest(CompiledAndInterpretedSepTest.SepTestConfig compile) {
        super(compile);
    }

    @Test
    public void dispatchOnlyTest() {
        MyStringHandler myStringHandler = new MyStringHandler();
        myStringHandler.dataHolder = new DataHolder();
        myStringHandler.intHandler = new MyIntegerHandler();
        myStringHandler.intHandler.x = 100;
        sep(myStringHandler);

        onEvent("TEST");
        Assert.assertEquals("TEST", myStringHandler.in);
        Assert.assertEquals(100, myStringHandler.intHandler.x);
        Assert.assertEquals(0, myStringHandler.intHandler.y);

        onEvent(200);
        Assert.assertEquals(200, myStringHandler.intHandler.y);
    }

    public static class MyStringHandler {
        public DataHolder dataHolder;
        public MyIntegerHandler intHandler;
        public String in;

        @OnEventHandler
        public boolean onString(String in) {
            System.out.println("received - " + in);
            this.in = in;
            return true;
        }
    }

    public static class MyIntegerHandler {

        public int x;
        public int y;

        @OnEventHandler
        public boolean onInteger(Integer in) {
            y = in;
            return true;
        }
    }

    @SepNode
    public static class DataHolder {


    }
}
