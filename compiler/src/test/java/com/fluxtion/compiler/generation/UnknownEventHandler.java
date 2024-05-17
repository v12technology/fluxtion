package com.fluxtion.compiler.generation;

import com.fluxtion.compiler.generation.graphcombinations.HandlerAsDependencyTest;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import org.junit.Test;

import java.util.concurrent.atomic.LongAdder;

import static org.junit.Assert.*;

public class UnknownEventHandler extends MultipleSepTargetInProcessTest {


    public UnknownEventHandler(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void mappedAndUnMappedEventTypes() {
        sep((c) -> c.addNode(new HandlerAsDependencyTest.StringHandler(), "strHandler"));
        HandlerAsDependencyTest.StringHandler strHandler = getField("strHandler");
        assertFalse(strHandler.isNotified());
        onEvent("hello world");
        assertTrue(strHandler.isNotified());

        onEvent(111);

        LongAdder adder = new LongAdder();
        sep.setUnKnownEventHandler(e -> adder.increment());
        onEvent(111);
        assertEquals(1, adder.intValue());
    }
}
