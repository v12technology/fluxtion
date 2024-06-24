package com.fluxtion.runtime.server.subscription;

import com.fluxtion.runtime.annotations.feature.Experimental;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.agrona.concurrent.OneToOneConcurrentArrayQueue;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Handles publishing events to internal dispatch queues, provides the functionality:
 * <ul>
 *     <li>Multiplexes a single event message to multiple queues</li>
 *     <li>Monitors and disconnects slow readers</li>
 * </ul>
 *
 * @param <T>
 */
@Experimental
@RequiredArgsConstructor
@ToString
@Log4j2
public class EventToQueuePublisher<T> {

    private final List<NamedQueue<T>> targetQueues = new CopyOnWriteArrayList<>();
    private final String name;

    public void addTargetQueue(OneToOneConcurrentArrayQueue<T> targetQueue, String name) {
        NamedQueue<T> namedQueue = new NamedQueue<>(name, targetQueue);
        if (!targetQueues.contains(namedQueue)) {
            targetQueues.add(namedQueue);
        }
    }

    public void publish(T itemToPublish) {
        log.info("listenerCount:{} publish:{}", targetQueues.size(), itemToPublish);
        for (int i = 0, targetQueuesSize = targetQueues.size(); i < targetQueuesSize; i++) {
            NamedQueue<T> namedQueue = targetQueues.get(i);
            OneToOneConcurrentArrayQueue<T> targetQueue = namedQueue.getTargetQueue();
            targetQueue.offer(itemToPublish);
            log.info("queue:{} size:{}", namedQueue.getName(), targetQueue.size());
        }
    }

    @Value
    private static class NamedQueue<T> {
        String name;
        OneToOneConcurrentArrayQueue<T> targetQueue;
    }
}
