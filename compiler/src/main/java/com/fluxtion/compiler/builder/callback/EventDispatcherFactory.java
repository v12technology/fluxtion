package com.fluxtion.compiler.builder.callback;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import com.fluxtion.runtime.callback.CallbackDispatcher;
import com.fluxtion.runtime.callback.EventDispatcher;
import com.google.auto.service.AutoService;

import java.util.Map;

@AutoService(NodeFactory.class)
public class EventDispatcherFactory implements NodeFactory<EventDispatcher> {

    @Override
    public EventDispatcher createNode(Map<String, Object> config, NodeRegistry registry) {
        return registry.registerNode(CallBackDispatcherFactory.SINGLETON, CallbackDispatcher.DEFAULT_NODE_NAME);
    }
}
