package com.fluxtion.compiler.generation.input;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.input.EventProcessorFeed;
import com.fluxtion.runtime.input.SubscriptionManager;
import org.junit.Test;

public class SubscriptionTest extends MultipleSepTargetInProcessTest {
    public SubscriptionTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void subscriptionTest() {
        sep(c -> c.addNode(new MySubscriberNode()));
        sep.addEventProcessorFeed(new EventProcessorFeed() {
            @Override
            public void subscribe(StaticEventProcessor target, Object subscriptionId) {
                System.out.println("subscribe:" + subscriptionId);
            }

            @Override
            public void unSubscribe(StaticEventProcessor target, Object subscriptionId) {
                System.out.println("unSubscribe:" + subscriptionId);
            }

            @Override
            public void removeAllSubscriptions(StaticEventProcessor eventProcessor) {
                System.out.println("removeAllSubscriptions:" + eventProcessor);
            }
        });
        sep.onEvent("test");
//        sep.onEvent("test");
//        sep.onEvent("high");
    }


    public static class MySubscriberNode {

        @Inject
        public SubscriptionManager subscriptionManager;
        private String id;

        @OnEventHandler
        public void subscriptionId(String id) {
            if (this.id != null) {
                subscriptionManager.unSubscribe(this.id);
            }
            subscriptionManager.subscribe(id);
            this.id = id;
        }

        @Initialise
        public void init() {

        }

    }
}
