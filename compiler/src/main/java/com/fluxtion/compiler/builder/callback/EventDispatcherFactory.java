package com.fluxtion.compiler.builder.callback;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import com.fluxtion.runtime.callback.EventDispatcher;
import com.fluxtion.runtime.callback.EventDispatcherImpl;
import com.google.auto.service.AutoService;

import java.util.Map;

@AutoService(NodeFactory.class)
public class EventDispatcherFactory implements NodeFactory<EventDispatcher> {
    private static final EventDispatcherImpl SINGLETON = new EventDispatcherImpl();

    @Override
    public EventDispatcher createNode(Map<?, ?> config, NodeRegistry registry) {
        return SINGLETON;
    }
}
