package com.fluxtion.compiler.generation.input;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.event.Signal.IntSignal;
import com.fluxtion.runtime.input.EventProcessorFeed;
import com.fluxtion.runtime.input.SubscriptionManager;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;

public class SubscriptionTest extends MultipleSepTargetInProcessTest {
    public SubscriptionTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void subscriptionTest() {
        Set<Object> subscriptions = new HashSet<>();
        sep(c -> {
            c.addNode(
                    new MySubscriberNode("subscriber_1"),
                    new MySubscriberNode("subscriber_2"),
                    new MySubscriberNode("subscriber_3")
            );
        });
        sep.addEventProcessorFeed(new EventProcessorFeed() {
            @Override
            public void subscribe(StaticEventProcessor target, Object subscriptionId) {
                if (subscriptions.contains(subscriptionId)) {
                    throw new IllegalStateException("multiple subscriptions for same symbol:" + subscriptionId);
                }
                subscriptions.add(subscriptionId);
            }

            @Override
            public void unSubscribe(StaticEventProcessor target, Object subscriptionId) {
                if (!subscriptions.contains(subscriptionId)) {
                    throw new IllegalStateException("No subscription to remove for symbol:" + subscriptionId);
                }
                subscriptions.remove(subscriptionId);
            }

            @Override
            public void removeAllSubscriptions(StaticEventProcessor eventProcessor) {
                subscriptions.clear();
            }
        });

        Assert.assertTrue(subscriptions.isEmpty());
        sep.publishIntSignal("subscriber_1", 10);
        assertThat(subscriptions, Matchers.containsInAnyOrder(10));
        sep.publishIntSignal("subscriber_2", 10);
        sep.publishIntSignal("subscriber_3", 10);

        sep.publishIntSignal("subscriber_1", 15);
        assertThat(subscriptions, Matchers.containsInAnyOrder(10, 15));
        sep.publishIntSignal("subscriber_2", 22);
        assertThat(subscriptions, Matchers.containsInAnyOrder(10, 15, 22));
        sep.publishIntSignal("subscriber_3", 11);
        assertThat(subscriptions, Matchers.containsInAnyOrder(11, 15, 22));

        sep.publishIntSignal("subscriber_3", Integer.MAX_VALUE);
        assertThat(subscriptions, Matchers.containsInAnyOrder(15, 22));

        tearDown();
        Assert.assertTrue(subscriptions.isEmpty());
    }


    public static class MySubscriberNode {

        private final String subscriberId;
        @Inject
        public SubscriptionManager subscriptionManager;
        private int currentSubscriptionSymbol = Integer.MAX_VALUE;

        public MySubscriberNode(String subscriberId) {
            this.subscriberId = subscriberId;
        }

        @OnEventHandler(filterVariable = "subscriberId")
        public void subscriptionId(IntSignal newSubscriptionSymbol) {
            if (this.currentSubscriptionSymbol != Integer.MAX_VALUE) {
                subscriptionManager.unSubscribe(currentSubscriptionSymbol);
            }
            currentSubscriptionSymbol = newSubscriptionSymbol.getValue();
            if (currentSubscriptionSymbol != Integer.MAX_VALUE) {
                subscriptionManager.subscribe(currentSubscriptionSymbol);
            }
        }

    }
}
