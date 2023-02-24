package com.fluxtion.runtime;

import com.fluxtion.runtime.callback.DirtyStateMonitor;
import com.fluxtion.runtime.callback.EventDispatcher;
import com.fluxtion.runtime.input.SubscriptionManager;
import com.fluxtion.runtime.node.NodeNameLookup;

public interface EventProcessorContext {
    String DEFAULT_NODE_NAME = "context";

    NodeNameLookup getNodeNameLookup();

    EventDispatcher getEventDispatcher();

    DirtyStateMonitor getDirtyStateMonitor();

    SubscriptionManager getSubscriptionManager();

    <K, V> V getContextProperty(K key);
}
