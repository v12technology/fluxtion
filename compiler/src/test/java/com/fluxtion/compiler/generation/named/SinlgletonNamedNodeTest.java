package com.fluxtion.compiler.generation.named;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.node.SingleNamedNode;
import org.junit.Assert;
import org.junit.Test;

public class SinlgletonNamedNodeTest extends MultipleSepTargetInProcessTest {


    public SinlgletonNamedNodeTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testDifferentNames() {
        sep(c -> {
            c.addNode(new MyNamedStringHandler("AAA"));
            c.addNode(new MyNamedStringHandler("BBB"));
        });

        onEvent("test");
        MyNamedStringHandler aaa = getField("AAA");
        MyNamedStringHandler bbb = getField("BBB");
        Assert.assertEquals("test", aaa.value);
        Assert.assertEquals("test", bbb.value);
    }

    public static class MyNamedStringHandler extends SingleNamedNode {

        public String value;

        @OnEventHandler
        public boolean onString(String in) {
            this.value = in;
            return true;
        }

        public MyNamedStringHandler(String name) {
            super(name);
        }
    }
}
