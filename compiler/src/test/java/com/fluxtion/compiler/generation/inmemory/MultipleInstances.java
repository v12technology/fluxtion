package com.fluxtion.compiler.generation.inmemory;

import com.fluxtion.compiler.Fluxtion;
import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.EventProcessorContext;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.node.SingletonNode;
import org.junit.Assert;
import org.junit.Test;

public class MultipleInstances {

    @Test
    public void multipleInstances() throws NoSuchFieldException {
        EventProcessor processor1 = Fluxtion.interpret(c -> c.addNode(new Handler("AAA_graph")));
        EventProcessor processor2 = Fluxtion.interpret(c -> c.addNode(new Handler("XXX_graph")));
        processor1.init();
        processor2.init();
        Assert.assertNotEquals(processor1, processor2);

        Handler h1 = processor1.getNodeById("AAA_graph");
        Handler h2 = processor2.getNodeById("XXX_graph");
        Assert.assertNotEquals(h1.context, h2.context);
        Assert.assertNotEquals(h1.context.getDirtyStateMonitor(), h2.context.getDirtyStateMonitor());
        Assert.assertNotEquals(h1.context.getEventDispatcher(), h2.context.getEventDispatcher());
        Assert.assertNotEquals(h1.context.getNodeNameLookup(), h2.context.getNodeNameLookup());
        Assert.assertNotEquals(h1.context.getSubscriptionManager(), h2.context.getSubscriptionManager());
    }

    public static class Handler extends SingletonNode {

        @Inject
        public EventProcessorContext context;

        public Handler(String name) {
            super(name);
        }

        @OnEventHandler
        public void stringReceived(String in) {
            System.out.println(getName() + " -> " + in);
        }
    }

}
