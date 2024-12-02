package com.fluxtion.runtime.input;

import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.annotations.TearDown;
import com.fluxtion.runtime.annotations.runtime.ServiceDeregistered;
import com.fluxtion.runtime.annotations.runtime.ServiceRegistered;
import com.fluxtion.runtime.event.NamedFeedEvent;
import com.fluxtion.runtime.node.EventSubscription;
import com.fluxtion.runtime.node.NamedNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SubscriptionManagerNode implements SubscriptionManager, NamedNode {

    //feeds
    private final transient List<EventFeed> registeredFeeds = new ArrayList<>();
    private final transient Map<String, NamedEventFeed> registeredNameEventFeedMap = new HashMap<>();
    //subscriptions
    private final transient Map<Object, Integer> subscriptionMap = new HashMap<>();
    private final transient Map<EventSubscription<?>, Integer> namedFeedSubscriptionMap = new HashMap<>();

    private StaticEventProcessor eventProcessor = StaticEventProcessor.NULL_EVENTHANDLER;

    public void setSubscribingEventProcessor(StaticEventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    public void addEventProcessorFeed(EventFeed eventFeed) {
        if (!registeredFeeds.contains(eventFeed)) {
            eventFeed.registerSubscriber(eventProcessor);
            registeredFeeds.add(eventFeed);
            subscriptionMap.keySet().forEach(e -> eventFeed.subscribe(eventProcessor, e));
        }
    }

    public void removeEventProcessorFeed(EventFeed eventProcessorFeed) {
        registeredFeeds.remove(eventProcessorFeed);
    }

    @Override
    public void subscribe(Object subscriptionId) {
        subscriptionMap.compute(subscriptionId, (k, v) -> {
            if (v == null) {
                registeredFeeds.forEach(e -> e.subscribe(eventProcessor, subscriptionId));
                return 1;
            }
            return ++v;
        });
    }

    @Override
    public void unSubscribe(Object subscriptionId) {
        subscriptionMap.computeIfPresent(subscriptionId, (o, i) -> {
            if (--i < 1) {
                registeredFeeds.forEach(e -> e.unSubscribe(eventProcessor, subscriptionId));
                return null;
            }
            return i;
        });
    }

    @ServiceRegistered
    public void registerEventFeedService(NamedEventFeed eventFeed, String feedName) {
        if (!registeredNameEventFeedMap.containsKey(feedName)) {
            eventFeed.registerSubscriber(eventProcessor);
            registeredNameEventFeedMap.put(feedName, eventFeed);
            namedFeedSubscriptionMap.keySet().forEach(e -> {
                eventFeed.subscribe(eventProcessor, e);
            });
        }
    }

    @ServiceDeregistered
    public void deRegisterEventFeedService(NamedEventFeed eventFeed, String feedName) {
        registeredNameEventFeedMap.remove(feedName);
    }

    @Override
    public void subscribeToNamedFeed(EventSubscription<?> subscription) {
        namedFeedSubscriptionMap.compute(subscription, (k, v) -> {
            if (v == null) {
                registeredNameEventFeedMap.values().forEach(e -> e.subscribe(eventProcessor, subscription));
                return 1;
            }
            return ++v;
        });
    }

    @Override
    public void subscribeToNamedFeed(String feedName) {
        subscribeToNamedFeed(new EventSubscription<>(feedName, Integer.MAX_VALUE, feedName, NamedFeedEvent.class));
    }

    @Override
    public void unSubscribeToNamedFeed(EventSubscription<?> subscription) {
        namedFeedSubscriptionMap.computeIfPresent(subscription, (k, i) -> {
            if (--i < 1) {
                registeredNameEventFeedMap.values().forEach(e -> e.unSubscribe(eventProcessor, subscription));
                return 1;
            }
            return i;
        });
    }

    @Override
    public void unSubscribeToNamedFeed(String feedName) {
        unSubscribeToNamedFeed(new EventSubscription<>(feedName, Integer.MAX_VALUE, feedName, NamedFeedEvent.class));
    }

    @TearDown
    public void tearDown() {
        registeredFeeds.forEach(e -> e.removeAllSubscriptions(eventProcessor));
        subscriptionMap.clear();
        //
        registeredNameEventFeedMap.values().forEach(e -> e.removeAllSubscriptions(eventProcessor));
        namedFeedSubscriptionMap.clear();
    }

    @Override
    public String getName() {
        return SubscriptionManager.DEFAULT_NODE_NAME;
    }
}