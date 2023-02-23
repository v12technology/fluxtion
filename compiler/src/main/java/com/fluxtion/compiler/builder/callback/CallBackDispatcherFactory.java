package com.fluxtion.compiler.builder.callback;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import com.fluxtion.runtime.callback.CallbackDispatcher;
import com.fluxtion.runtime.callback.CallbackDispatcherImpl;
import com.google.auto.service.AutoService;

import java.util.Map;

@AutoService(NodeFactory.class)
public class CallBackDispatcherFactory implements NodeFactory<CallbackDispatcher> {
    public static final CallbackDispatcherImpl SINGLETON = new CallbackDispatcherImpl();

    @Override
    public CallbackDispatcher createNode(Map<String, Object> config, NodeRegistry registry) {
        return registry.registerNode(SINGLETON, CallbackDispatcher.DEFAULT_NODE_NAME);
    }
}
