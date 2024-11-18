package com.fluxtion.compiler.generation.eventfeed;

import com.fluxtion.compiler.builder.dataflow.DataFlow;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.event.NamedFeedEvent;
import com.fluxtion.runtime.event.Signal.IntSignal;
import com.fluxtion.runtime.input.EventFeed;
import com.fluxtion.runtime.input.NamedEventFeed;
import com.fluxtion.runtime.input.SubscriptionManager;
import com.fluxtion.runtime.node.EventSubscription;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;

public class SubscriptionTest extends MultipleSepTargetInProcessTest {
    public SubscriptionTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void subscriptionTest() {
        Set<Object> subscriptions = new HashSet<>();
        sep(c -> {
            c.addNode(new MySubscriberNode("subscriber_1"), new MySubscriberNode("subscriber_2"), new MySubscriberNode("subscriber_3"));
        });
        sep.addEventFeed(new MyEventFeed(subscriptions));

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

    @Test
    public void subscriptionTestFunctional() {
        Set<EventSubscription<?>> subscriptions = new HashSet<>();
        sep(c -> {
            DataFlow.subscribeToFeed("feedA");
        });

        sep.registerService(new MyNamedEventFeed(subscriptions), NamedEventFeed.class, "feedA");
        MatcherAssert.assertThat(subscriptions,
                Matchers.containsInAnyOrder(
                        new EventSubscription<>(
                                "feedA",
                                Integer.MAX_VALUE,
                                "feedA",
                                NamedFeedEvent.class)
                )
        );
    }

    @Test
    public void subscriberTearDownThenInit() {
        Set<Object> subscriptions = new HashSet<>();
        sep(c -> c.addNode(new MySubscriberNode("subscriber_1")));
        sep.addEventFeed(new MyEventFeed(subscriptions));

        Assert.assertTrue(subscriptions.isEmpty());
        sep.publishIntSignal("subscriber_1", 10);
        assertThat(subscriptions, Matchers.containsInAnyOrder(10));

        //
        tearDown();
        Assert.assertTrue(subscriptions.isEmpty());

        //
        init();
        sep.publishIntSignal("subscriber_1", 10);
        assertThat(subscriptions, Matchers.containsInAnyOrder(10));
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
        public boolean subscriptionId(IntSignal newSubscriptionSymbol) {
            if (this.currentSubscriptionSymbol != Integer.MAX_VALUE) {
                subscriptionManager.unSubscribe(currentSubscriptionSymbol);
            }
            currentSubscriptionSymbol = newSubscriptionSymbol.getValue();
            if (currentSubscriptionSymbol != Integer.MAX_VALUE) {
                subscriptionManager.subscribe(currentSubscriptionSymbol);
            }
            return true;
        }

    }

    public static class MyEventFeed implements EventFeed {
        private final Set<Object> subscriptions;

        public MyEventFeed(Set<Object> subscriptions) {
            this.subscriptions = subscriptions;
        }

        @Override
        public void registerSubscriber(StaticEventProcessor subscriber) {

        }

        @Override
        public void subscribe(StaticEventProcessor subscriber, Object subscriptionId) {
            if (subscriptions.contains(subscriptionId)) {
                throw new IllegalStateException("multiple subscriptions for same symbol:" + subscriptionId);
            }
            subscriptions.add(subscriptionId);
        }

        @Override
        public void unSubscribe(StaticEventProcessor subscriber, Object subscriptionId) {
            if (!subscriptions.contains(subscriptionId)) {
                throw new IllegalStateException("No subscription to remove for symbol:" + subscriptionId);
            }
            subscriptions.remove(subscriptionId);
        }

        @Override
        public void removeAllSubscriptions(StaticEventProcessor subscriber) {
            subscriptions.clear();
        }
    }

    public static class MyNamedEventFeed implements NamedEventFeed {

        private final Set<EventSubscription<?>> subscriptions;
        private StaticEventProcessor subscriber;

        public MyNamedEventFeed(Set<EventSubscription<?>> subscriptions) {
            this.subscriptions = subscriptions;
        }

        @Override
        public void registerSubscriber(StaticEventProcessor subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void subscribe(StaticEventProcessor subscriber, EventSubscription<?> subscriptionId) {
            if (subscriptions.contains(subscriptionId)) {
                throw new IllegalStateException("multiple subscriptions for same symbol:" + subscriptionId);
            }
            subscriptions.add(subscriptionId);
        }

        @Override
        public void unSubscribe(StaticEventProcessor subscriber, EventSubscription<?> subscriptionId) {
            if (!subscriptions.contains(subscriptionId)) {
                throw new IllegalStateException("No subscription to remove for symbol:" + subscriptionId);
            }
            subscriptions.remove(subscriptionId);
        }

        @Override
        public void removeAllSubscriptions(StaticEventProcessor subscriber) {
            subscriptions.clear();
        }
    }
}
