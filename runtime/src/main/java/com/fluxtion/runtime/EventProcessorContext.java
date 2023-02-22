package com.fluxtion.runtime;

import com.fluxtion.runtime.audit.NodeNameAuditor;
import com.fluxtion.runtime.callback.CallbackDispatcher;
import com.fluxtion.runtime.callback.DirtyStateMonitor;
import com.fluxtion.runtime.callback.EventDispatcher;

public interface EventProcessorContext {
    String DEFAULT_NODE_NAME = "context";

    NodeNameAuditor getNodeNameLookup();

    EventDispatcher getEventDispatcher();

    CallbackDispatcher getCallBackDispatcher();

    DirtyStateMonitor getDirtyStateMonitor();

    <K, V> V getContextProperty(K key);
}
