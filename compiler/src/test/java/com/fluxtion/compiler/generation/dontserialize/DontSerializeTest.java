package com.fluxtion.compiler.generation.dontserialize;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.CompiledOnlySepTest;
import com.fluxtion.runtime.annotations.FluxtionDontSerialize;
import com.fluxtion.runtime.annotations.OnEventHandler;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

public class DontSerializeTest extends CompiledOnlySepTest {
    public DontSerializeTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void dontSerialize() {
        MyNode myNode = new MyNode();
        myNode.setOut("XXXX");
        sep(c -> c.addNode(myNode, "myNode"));
        MyNode myNodeGraph = getField("myNode");
        Assert.assertEquals("OUT", myNodeGraph.getOut());
        Assert.assertEquals("IN", myNodeGraph.getIn());
    }

    @Data
    public static class MyNode {
        @FluxtionDontSerialize
        private final String in;
        @FluxtionDontSerialize
        private String out;

        public MyNode() {
            this.in = "IN";
            this.out = "OUT";
        }

        @OnEventHandler
        public boolean stringEvent(String evert) {
            return true;
        }
    }
}
