package com.fluxtion.compiler.builder.callback;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import com.fluxtion.runtime.callback.Callback;
import com.fluxtion.runtime.callback.CallbackImpl;
import com.google.auto.service.AutoService;

import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

@AutoService(NodeFactory.class)
public class CallbackNodeFactory implements NodeFactory<Callback> {
    private static final LongAdder idGenerator = new LongAdder();

    @Override
    public Callback<?> createNode(Map<String, Object> config, NodeRegistry registry) {
        idGenerator.increment();
        return new CallbackImpl<>(idGenerator.intValue());
    }
}
