package com.fluxtion.compiler.generation.inject;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import com.google.auto.service.AutoService;

import java.util.Map;

@AutoService(NodeFactory.class)
public class MyUniqueDataGreenFactory implements NodeFactory<InjectFactoryByNameTest.MyUniqueData> {
    @Override
    public InjectFactoryByNameTest.MyUniqueData createNode(Map<String, Object> config, NodeRegistry registry) {
        return new InjectFactoryByNameTest.MyUniqueData("green");
    }

    @Override
    public String factoryName() {
        return "green";
    }
}
