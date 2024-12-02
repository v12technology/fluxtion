package com.fluxtion.runtime;

import com.fluxtion.runtime.callback.DirtyStateMonitor;
import com.fluxtion.runtime.callback.EventDispatcher;
import com.fluxtion.runtime.input.SubscriptionManager;
import com.fluxtion.runtime.node.NodeNameLookup;
import com.fluxtion.runtime.time.Clock;

/**
 * Runtime access to various services in the running EventProcessor instance.
 */
public interface EventProcessorContext {
    String DEFAULT_NODE_NAME = "context";

    NodeNameLookup getNodeNameLookup();

    EventDispatcher getEventDispatcher();

    DirtyStateMonitor getDirtyStateMonitor();

    SubscriptionManager getSubscriptionManager();

    Clock getClock();

    <T> T getExportedService(Class<T> exportedServiceClass);

    <T> T getExportedService();

    /**
     * Retrieves an injected instance at runtime. Fails with {@link RuntimeException} if no instance is found
     * <p>
     * see {@link StaticEventProcessor#injectInstance(Object)}
     *
     * @param instanceClass The class of the instance to retrieve
     * @param <T>           The type of the returned class
     * @return The instance injected.
     */
    <T> T getInjectedInstance(Class<T> instanceClass);

    /**
     * Retrieves an injected instance at runtime. Fails with {@link RuntimeException} if no instance is found
     * <p>
     * see {@link StaticEventProcessor#injectNamedInstance(Object, String)}
     *
     * @param instanceClass The class of the instance to retrieve
     * @param <T>           The type of the returned class
     * @return The instance injected.
     */
    <T> T getInjectedInstance(Class<T> instanceClass, String name);


    /**
     * Retrieves an injected instance at runtime.
     * <p>
     * see {@link StaticEventProcessor#injectInstance(Object)}
     *
     * @param instanceClass The class of the instance to retrieve
     * @param <T>           The type of the returned class
     * @return The instance injected.
     */
    <T> T getInjectedInstanceAllowNull(Class<T> instanceClass);

    /**
     * Retrieves an injected instance at runtime.
     * <p>
     * see {@link StaticEventProcessor#injectNamedInstance(Object, String)}
     *
     * @param instanceClass The class of the instance to retrieve
     * @param <T>           The type of the returned class
     * @return The instance injected.
     */
    <T> T getInjectedInstanceAllowNull(Class<T> instanceClass, String name);

    <K, V> V getContextProperty(K key);


    /**
     * The public {@link StaticEventProcessor} instance for this context
     *
     * @return Encapsulating StaticEventProcessor
     */
    default StaticEventProcessor getStaticEventProcessor() {
        return getExportedService(StaticEventProcessor.class);
    }

    /**
     * Helper method for {@link EventDispatcher#processReentrantEvent(Object)}
     *
     * @param event to dispatch to this {@link StaticEventProcessor}
     */
    default void processReentrantEvent(Object event) {
        getEventDispatcher().processReentrantEvent(event);
    }

    /**
     * Helper method for {@link EventDispatcher#processAsNewEventCycle(Object)} (Object)}
     *
     * @param event to dispatch to this {@link StaticEventProcessor}
     */
    default void processAsNewEventCycle(Object event) {
        getEventDispatcher().processAsNewEventCycle(event);
    }

    /**
     * Helper method for {@link EventDispatcher#processReentrantEvents(Iterable)}
     *
     * @param iterable to dispatch to this {@link StaticEventProcessor}
     */
    default void processAsNewEventCycle(Iterable<Object> iterable) {
        getEventDispatcher().processAsNewEventCycle(iterable);
    }

    /**
     * Helper method for {@link DirtyStateMonitor#isDirty(Object)}}
     *
     * @param node to check for dirty state
     */
    default void isDirty(Object node) {
        getDirtyStateMonitor().isDirty(node);
    }

    /**
     * Helper method for {@link DirtyStateMonitor#markDirty(Object)} (Object)}}
     *
     * @param node to mark as dirty during this event cycle
     */
    default void markDirty(Object node) {
        getDirtyStateMonitor().markDirty(node);
    }

    /**
     * Helper method for {@link NodeNameLookup#lookupInstanceName(Object)}
     *
     * @param node the node whose name to lookup
     * @return the name of the node
     */
    default String lookupInstanceName(Object node) {
        return getNodeNameLookup().lookupInstanceName(node);
    }

    /**
     * Helper method for {@link NodeNameLookup#getInstanceById(String)}}
     *
     * @param instanceId used to look up a node instance
     * @return the node whose name matches the supplied predicate
     */
    default <V> V getInstanceById(String instanceId) throws NoSuchFieldException {
        return getNodeNameLookup().getInstanceById(instanceId);
    }

    /**
     * Creates a subscription to {@link com.fluxtion.runtime.input.EventFeed} registered with a matching feed name
     * @param feedName the name to match on a registered {@link com.fluxtion.runtime.input.EventFeed}
     */
    default void subscribeToNamedFeed(String feedName) {
        getSubscriptionManager().subscribeToNamedFeed(feedName);
    }

    /**
     * Removes a subscription to {@link com.fluxtion.runtime.input.EventFeed} registered with a matching feed name
     * @param feedName the name to match on a registered {@link com.fluxtion.runtime.input.EventFeed}
     */
    default void unSubscribeToNamedFeed(String feedName) {
        getSubscriptionManager().unSubscribeToNamedFeed(feedName);
    }
}
