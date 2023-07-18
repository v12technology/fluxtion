package com.fluxtion.compiler.builder.output;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import com.fluxtion.runtime.output.SinkPublisher;

import java.util.Map;

public class SinkPublisherFactory implements NodeFactory<SinkPublisher> {

    @Override
    public SinkPublisher<?> createNode(Map<String, Object> config, NodeRegistry registry) {
        final String instanceName = (String) config.get(NodeFactory.INSTANCE_KEY);
        return new SinkPublisher<>(instanceName);
    }

}
