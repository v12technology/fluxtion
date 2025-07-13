/*
 * SPDX-FileCopyrightText: © 2025 Gregory Higgins <greg.higgins@v12technology.com>
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package com.fluxtion.runtime;

import com.fluxtion.runtime.annotations.runtime.ServiceRegistered;
import com.fluxtion.runtime.node.ObjectEventHandlerNode;
import lombok.Getter;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class DefaultEventHandlerProcessorTest {

    @Test
    public void test() {
        MyObjectEventHandlerNode handler = new MyObjectEventHandlerNode();
        DefaultEventProcessor processor = new DefaultEventProcessor(handler);

        Assert.assertFalse(handler.isInitialised());
        processor.init();
        Assert.assertTrue(handler.isInitialised());

        Assert.assertFalse(handler.isStarted());
        processor.start();
        Assert.assertTrue(handler.isStarted());

        processor.onEvent("test");
        Assert.assertEquals("test", handler.getEvent());

        AtomicReference<String> ref = new AtomicReference<>();
        Consumer<String> consumer = ref::set;

        processor.registerService(consumer, Consumer.class, "ignoreConsumer");
        processor.onEvent("YYYYY");
        Assert.assertEquals("YYYYY", handler.getEvent());
        Assert.assertNull(ref.get());

        processor.registerService(consumer, Consumer.class, "myConsumer");
        processor.onEvent("XXX");
        Assert.assertEquals("XXX", handler.getEvent());
        Assert.assertEquals("XXX", ref.get());

        Assert.assertFalse(handler.isStopped());
        processor.stop();
        Assert.assertTrue(handler.isStopped());

        Assert.assertFalse(handler.isTearDown());
        processor.tearDown();
        Assert.assertTrue(handler.isTearDown());
    }

    @Getter
    public static class MyObjectEventHandlerNode extends ObjectEventHandlerNode {

        private Consumer<String> consumer;
        private boolean initialised = false;
        private boolean started = false;
        private boolean stopped = false;
        private boolean tearDown = false;
        private Object event;

        @Override
        protected void _initialise() {
            initialised = true;
        }

        @Override
        public void start() {
            started = true;
        }

        @Override
        public void stop() {
            stopped = true;
        }

        @Override
        public void tearDown() {
            tearDown = true;
        }

        @Override
        protected boolean handleEvent(Object event) {
            this.event = event;
            if (consumer != null) {
                consumer.accept(event.toString());
            }
            return true;
        }

        @ServiceRegistered
        public void register(Consumer<String> consumer, String name) {
            if ("myConsumer".equals(name)) {
                this.consumer = consumer;
            }
        }
    }
}
