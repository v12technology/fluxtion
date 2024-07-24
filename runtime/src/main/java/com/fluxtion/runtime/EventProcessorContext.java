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
}
