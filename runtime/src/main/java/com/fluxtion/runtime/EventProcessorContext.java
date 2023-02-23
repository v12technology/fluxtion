package com.fluxtion.runtime;

import com.fluxtion.runtime.callback.CallbackDispatcher;
import com.fluxtion.runtime.callback.DirtyStateMonitor;
import com.fluxtion.runtime.callback.EventDispatcher;
import com.fluxtion.runtime.node.NodeNameLookup;

public interface EventProcessorContext {
    String DEFAULT_NODE_NAME = "context";

    NodeNameLookup getNodeNameLookup();

    EventDispatcher getEventDispatcher();

    CallbackDispatcher getCallBackDispatcher();

    DirtyStateMonitor getDirtyStateMonitor();

    <K, V> V getContextProperty(K key);
}
