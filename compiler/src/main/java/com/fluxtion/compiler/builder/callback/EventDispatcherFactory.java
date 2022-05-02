package com.fluxtion.compiler.builder.callback;

import com.fluxtion.compiler.builder.node.NodeFactory;
import com.fluxtion.compiler.builder.node.NodeRegistry;
import com.fluxtion.runtime.callback.CallbackImpl;
import com.fluxtion.runtime.callback.EventDispatcher;
import com.google.auto.service.AutoService;

import java.util.Map;

@AutoService(NodeFactory.class)
public class EventDispatcherFactory implements NodeFactory<EventDispatcher> {
    private static final CallbackImpl SINGLETON =  new CallbackImpl(0);
    @Override
    public EventDispatcher createNode(Map<?, ?> config, NodeRegistry registry) {
        return SINGLETON;
    }
}
