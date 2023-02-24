package com.fluxtion.compiler.builder.callback;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import com.fluxtion.compiler.generation.GenerationContext;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.callback.CallbackDispatcher;
import com.fluxtion.runtime.callback.CallbackDispatcherImpl;

import java.util.Map;

public class CallBackDispatcherFactory implements NodeFactory<CallbackDispatcher> {
    static CallbackDispatcherImpl SINGLETON;

    @Override
    public CallbackDispatcher createNode(Map<String, Object> config, NodeRegistry registry) {
        return registry.registerNode(SINGLETON, CallbackDispatcher.DEFAULT_NODE_NAME);
    }

    @Override
    public void preSepGeneration(GenerationContext context, Map<String, Auditor> auditorMap) {
        SINGLETON = new CallbackDispatcherImpl();
        context.addOrUseExistingNode(SINGLETON);
    }
}
