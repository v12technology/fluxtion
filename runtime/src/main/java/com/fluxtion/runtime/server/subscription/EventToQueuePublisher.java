package com.fluxtion.runtime.server.subscription;

import com.fluxtion.runtime.annotations.feature.Experimental;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;
import lombok.extern.java.Log;
import org.agrona.concurrent.OneToOneConcurrentArrayQueue;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

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
@Log
@Getter
public class EventToQueuePublisher<T> {

    private final List<NamedQueue<T>> targetQueues = new CopyOnWriteArrayList<>();
    private final String name;

    public void addTargetQueue(OneToOneConcurrentArrayQueue<T> targetQueue, String name) {
        NamedQueue<T> namedQueue = new NamedQueue<>(name, targetQueue);
        if (log.isLoggable(Level.FINE)) {
            log.fine("adding a publisher queue:" + namedQueue);
        }
        if (!targetQueues.contains(namedQueue)) {
            targetQueues.add(namedQueue);
        }
    }

    public void publish(T itemToPublish) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("listenerCount:" + targetQueues.size() + " publish:" + itemToPublish);
        }

        for (int i = 0, targetQueuesSize = targetQueues.size(); i < targetQueuesSize; i++) {
            NamedQueue<T> namedQueue = targetQueues.get(i);
            OneToOneConcurrentArrayQueue<T> targetQueue = namedQueue.getTargetQueue();
            targetQueue.offer(itemToPublish);
            if (log.isLoggable(Level.FINE)) {
                log.fine("queue:" + namedQueue.getName() + " size:" + targetQueue.size());
            }
        }
    }

    @Value
    public static class NamedQueue<T> {
        String name;
        OneToOneConcurrentArrayQueue<T> targetQueue;
    }
}
