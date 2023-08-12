package com.fluxtion.compiler.builder.factory;

import com.google.auto.service.AutoService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AutoService(NodeFactory.class)
public class SignalGroupCalculatorFactory implements NodeFactory<RootNodeWithFactoryTest.SignalGroupCalculator> {

    @Override
    public RootNodeWithFactoryTest.SignalGroupCalculator createNode(Map<String, Object> config, NodeRegistry registry) {
        @SuppressWarnings("unchecked")
        List<String> keys = (List<String>) config.get("keys");
        return new RootNodeWithFactoryTest.SignalGroupCalculator(keys.stream().map(RootNodeWithFactoryTest.SignalHandler::new).collect(Collectors.toList()));
    }
}
