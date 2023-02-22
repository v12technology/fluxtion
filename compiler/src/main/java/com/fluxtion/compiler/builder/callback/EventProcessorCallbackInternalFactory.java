package com.fluxtion.compiler.builder.callback;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import com.fluxtion.runtime.callback.CallbackDispatcher;
import com.fluxtion.runtime.callback.EventProcessorCallbackInternal;
import com.google.auto.service.AutoService;

import java.util.Map;

import static com.fluxtion.compiler.builder.callback.CallBackDispatcherFactory.SINGLETON;

@AutoService(NodeFactory.class)
public class EventProcessorCallbackInternalFactory implements NodeFactory<EventProcessorCallbackInternal> {

    @Override
    public EventProcessorCallbackInternal createNode(Map<String, Object> config, NodeRegistry registry) {
        return registry.registerNode(SINGLETON, CallbackDispatcher.DEFAULT_NODE_NAME);
    }
}
