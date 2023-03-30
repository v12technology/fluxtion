package com.fluxtion.compiler.generation.named;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.node.NamedNode;
import lombok.Value;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class NamedNodeTest extends MultipleSepTargetInProcessTest {

    public static final String UNIQUE_NAME = "UniqueName";

    public NamedNodeTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testNaming() {
        sep(c -> c.addNode(new NameMe(UNIQUE_NAME)));
        NameMe node = getField(UNIQUE_NAME);
        assertNotNull(node);
    }

    @Value
    public static class NameMe implements NamedNode {

        String name;

        @Override
        public String getName() {
            return name;
        }
    }
}
