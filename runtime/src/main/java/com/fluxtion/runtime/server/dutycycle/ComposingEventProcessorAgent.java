package com.fluxtion.runtime.server.dutycycle;

import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.annotations.feature.Experimental;
import com.fluxtion.runtime.input.EventFeed;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.runtime.server.FluxtionServer;
import com.fluxtion.runtime.server.service.scheduler.DeadWheelScheduler;
import com.fluxtion.runtime.server.service.scheduler.SchedulerService;
import com.fluxtion.runtime.server.subscription.EventFlowManager;
import com.fluxtion.runtime.server.subscription.EventSubscriptionKey;
import com.fluxtion.runtime.service.Service;
import lombok.extern.java.Log;
import org.agrona.concurrent.DynamicCompositeAgent;
import org.agrona.concurrent.OneToOneConcurrentArrayQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 *
 */
@Experimental
@Log
public class ComposingEventProcessorAgent extends DynamicCompositeAgent implements EventFeed<EventSubscriptionKey<?>> {

    private final EventFlowManager eventFlowManager;
    private final ConcurrentHashMap<String, Service<?>> registeredServices;
    private final ConcurrentHashMap<EventSubscriptionKey<?>, EventQueueToEventProcessor> queueProcessorMap = new ConcurrentHashMap<>();
    private final OneToOneConcurrentArrayQueue<Supplier<StaticEventProcessor>> toStartList = new OneToOneConcurrentArrayQueue<>(128);
    private final List<EventQueueToEventProcessor> queueReadersToAdd = new ArrayList<>();
    private final FluxtionServer fluxtionServer;
    private final DeadWheelScheduler scheduler;
    private final Service<SchedulerService> schedulerService;

    public ComposingEventProcessorAgent(String roleName,
                                        EventFlowManager eventFlowManager,
                                        FluxtionServer fluxtionServer,
                                        DeadWheelScheduler scheduler,
                                        ConcurrentHashMap<String, Service<?>> registeredServices) {
        super(roleName, scheduler);
        this.eventFlowManager = eventFlowManager;
        this.fluxtionServer = fluxtionServer;
        this.scheduler = scheduler;
        this.registeredServices = registeredServices;
        this.schedulerService = new Service<>(scheduler, SchedulerService.class);
    }

    public void addEventFeedConsumer(Supplier<StaticEventProcessor> initFunction) {
        toStartList.add(initFunction);
    }

    @Override
    public void onStart() {
        log.info("onStart");
        super.onStart();
    }

    @Override
    public int doWork() throws Exception {
        toStartList.drain(init -> {
            StaticEventProcessor eventProcessor = init.get();
            EventFlowManager.setCurrentProcessor(eventProcessor);
            eventProcessor.registerService(schedulerService);
            registeredServices.values().forEach(eventProcessor::registerService);
            eventProcessor.addEventFeed(this);
            if (eventProcessor instanceof Lifecycle) {
                ((Lifecycle) eventProcessor).start();
            }
            EventFlowManager.removeCurrentProcessor();
        });

        if (!queueReadersToAdd.isEmpty()) {
            if (tryAdd(queueReadersToAdd.get(0))) {
                queueReadersToAdd.removeFirst();
            }
        }

        return super.doWork();
    }

    @Override
    public void onClose() {
        log.info("onClose");
        super.onClose();
    }

    @Override
    public void registerSubscriber(StaticEventProcessor subscriber) {
        log.info("registerSubscriber:" + subscriber);
    }

    @Override
    public void subscribe(StaticEventProcessor subscriber, EventSubscriptionKey<?> subscriptionKey) {
        Objects.requireNonNull(subscriber, "subscriber is null");
        Objects.requireNonNull(subscriptionKey, "subscriptionKey is null");
        log.info("subscribe subscriptionKey:" + subscriptionKey + " subscriber:" + subscriber);

        EventQueueToEventProcessor eventQueueToEventProcessor = queueProcessorMap.get(subscriptionKey);

        if (eventQueueToEventProcessor == null) {
            eventQueueToEventProcessor = eventFlowManager.getMappingAgent(subscriptionKey, this);
            queueProcessorMap.put(subscriptionKey, eventQueueToEventProcessor);
            queueReadersToAdd.add(eventQueueToEventProcessor);
            log.info("added new subscribe subscriptionKey:" + subscriptionKey + " subscriber:" + subscriber);
        }

        eventQueueToEventProcessor.registerProcessor(subscriber);
        eventFlowManager.subscribe(subscriptionKey);
    }

    @Override
    public void unSubscribe(StaticEventProcessor subscriber, EventSubscriptionKey<?> subscriptionKey) {
        if (queueProcessorMap.containsKey(subscriptionKey)) {
            EventQueueToEventProcessor eventQueueToEventProcessor = queueProcessorMap.get(subscriptionKey);
            if (eventQueueToEventProcessor.deregisterProcessor(subscriber) == 0) {
                log.info("EventQueueToEventProcessor listener count = 0, removing subscription:" + subscriptionKey);
                queueProcessorMap.remove(subscriptionKey);
                eventFlowManager.unSubscribe(subscriptionKey);
            }
        }
    }

    @Override
    public void removeAllSubscriptions(StaticEventProcessor subscriber) {

    }
}
