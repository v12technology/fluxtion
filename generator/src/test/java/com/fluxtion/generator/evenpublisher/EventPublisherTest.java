/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.generator.evenpublisher;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.event.Event;
import com.fluxtion.api.event.EventPublsher;
import com.fluxtion.api.event.RegisterEventHandler;
import com.fluxtion.generator.util.BaseSepInprocessTest;
import java.util.concurrent.atomic.LongAdder;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class EventPublisherTest extends BaseSepInprocessTest {

    @Test
    public void testAudit() {
        sep((c) -> {
            GreaterThan gt_10 = c.addNode(new GreaterThan(10));
            EventPublsher publisher = c.addPublicNode(new EventPublsher(), "publisher");
            publisher.addEventSource(gt_10);
            c.formatSource = true;
        });

        final LongAdder adder = new LongAdder();
//        sep.onEvent(new RegisterEventHandler(System.out::println));
        sep.onEvent(new RegisterEventHandler(e -> adder.increment()));
        sep.onEvent(new NumberEvent(12));
        sep.onEvent(new NumberEvent(3));
        assertThat(adder.intValue(), is(1));
    }

    public static class NumberEvent extends Event {

        public final int value;

        public NumberEvent(int value) {
            this.value = value;
        }

    }

    public static class GreaterThan extends Event{

        public int barrier;

        public GreaterThan() {
        }

        public GreaterThan(int barrier) {
            this.barrier = barrier;
        }

        @EventHandler
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
