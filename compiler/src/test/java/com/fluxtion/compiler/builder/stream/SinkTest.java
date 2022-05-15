/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.event.Event;
import org.junit.Test;

import java.util.concurrent.atomic.LongAdder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class SinkTest extends MultipleSepTargetInProcessTest {

    public SinkTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void simpleSinkTest() {
        sep((c) -> EventFlow.subscribeToNode(new GreaterThan(10)).sink("gt_10"));

        final LongAdder adder = new LongAdder();
        addSink("gt_10", i -> adder.increment());
        onEvent(new NumberEvent(12));
        onEvent(new NumberEvent(3));
        assertThat(adder.intValue(), is(1));
    }

    public static class NumberEvent implements Event {

        public final int value;

        public NumberEvent(int value) {
            this.value = value;
        }

    }

    public static class GreaterThan implements Event{

        public int barrier;
        private int toCheck;

        public GreaterThan() {
        }

        public GreaterThan(int barrier) {
            this.barrier = barrier;
        }

        @OnEventHandler
        public boolean isGreaterThan(NumberEvent number) {
            toCheck = number.value;
            return isBreached();
        }

        public boolean isBreached(){
            return toCheck > barrier;
        }

        @Override
        public String toString() {
            return "GreaterThan{" + "barrier=" + barrier + '}';
        }
        
    }
}
