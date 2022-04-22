/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.compiler.generation.evenpublisher;

import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.event.EventPublisher;
import com.fluxtion.runtime.event.RegisterEventHandler;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import org.junit.Test;

import java.util.concurrent.atomic.LongAdder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class EventPublisherTest extends MultipleSepTargetInProcessTest {

    public EventPublisherTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testAudit() {
        sep((c) -> {
            GreaterThan gt_10 = c.addNode(new GreaterThan(10));
            EventPublisher<GreaterThan> publisher = c.addPublicNode(new EventPublisher<>(), "publisher");
            publisher.addEventSource(gt_10);
            c.setFormatSource(true);
        });

        final LongAdder adder = new LongAdder();
//        sep.onEvent(new RegisterEventHandler(System.out::println));
        sep.onEvent(new RegisterEventHandler(e -> adder.increment()));
        sep.onEvent(new NumberEvent(12));
        sep.onEvent(new NumberEvent(3));
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

        public GreaterThan() {
        }

        public GreaterThan(int barrier) {
            this.barrier = barrier;
        }

        @OnEventHandler
        public boolean isGreaterThan(NumberEvent number) {
            //System.out.println("number:" + number.value);
            return number.value > barrier;
        }

        @Override
        public String toString() {
            return "GreaterThan{" + "barrier=" + barrier + '}';
        }
        
    }
}
