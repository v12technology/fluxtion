package com.fluxtion.runtime.input;

import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.annotations.TearDown;
import com.fluxtion.runtime.node.NamedNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class SubscriptionManagerNode implements SubscriptionManager, NamedNode {

    private final List<EventProcessorFeed> registeredFeeds = new ArrayList<>();
    private final Map<Object, Integer> subscriptionMap = new HashMap<>();
    private StaticEventProcessor eventProcessor = StaticEventProcessor.NULL_EVENTHANDLER;

    public void setSubscribingEventProcessor(StaticEventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    public void addEventProcessorFeed(EventProcessorFeed eventProcessorFeed) {
        if (!registeredFeeds.contains(eventProcessorFeed)) {
            registeredFeeds.add(eventProcessorFeed);
            subscriptionMap.keySet().forEach(e -> eventProcessorFeed.subscribe(eventProcessor, e));
        }
    }

    public void removeEventProcessorFeed(EventProcessorFeed eventProcessorFeed) {
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

    @TearDown
    public void tearDown() {
        registeredFeeds.forEach(e -> e.removeAllSubscriptions(eventProcessor));
    }

    @Override
    public String getName() {
        return SubscriptionManager.DEFAULT_NODE_NAME;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionManagerNode that = (SubscriptionManagerNode) o;
        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
