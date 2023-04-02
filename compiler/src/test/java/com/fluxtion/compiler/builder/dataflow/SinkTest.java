/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import org.junit.Test;

import java.util.concurrent.atomic.LongAdder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SinkTest extends MultipleSepTargetInProcessTest {

    public SinkTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void simpleSinkTest() {
        sep((c) -> DataFlow.subscribeToNode(new GreaterThan(10)).sink("gt_10"));
        final LongAdder adder = new LongAdder();
        addSink("gt_10", gt_10 -> adder.increment());
        //
        onEvent(12);
        onEvent(3);
        assertThat(adder.intValue(), is(1));
    }

    public static class GreaterThan {

        private final int barrier;

        public GreaterThan(int barrier) {
            this.barrier = barrier;
        }

        @OnEventHandler
        public boolean isGreaterThan(Integer toCheck) {
            return toCheck > barrier;
        }
    }
}
