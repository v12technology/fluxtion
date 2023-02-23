package com.fluxtion.compiler.builder.callback;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import com.fluxtion.runtime.callback.CallbackDispatcher;
import com.fluxtion.runtime.callback.DirtyStateMonitor;

import java.util.Map;

public class DirtyStateMonitorFactory implements NodeFactory<DirtyStateMonitor> {

    @Override
    public DirtyStateMonitor createNode(Map<String, Object> config, NodeRegistry registry) {
        return registry.registerNode(CallBackDispatcherFactory.SINGLETON, CallbackDispatcher.DEFAULT_NODE_NAME);
    }
}
