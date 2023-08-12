package com.fluxtion.compiler.generation.inject;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import com.google.auto.service.AutoService;

import java.util.Map;

@AutoService(NodeFactory.class)
public class MyUniqueDataBlueFactory implements NodeFactory<InjectFactoryByNameTest.MyUniqueData> {
    @Override
    public InjectFactoryByNameTest.MyUniqueData createNode(Map<String, Object> config, NodeRegistry registry) {
        return new InjectFactoryByNameTest.MyUniqueData("blue");
    }

    @Override
    public String factoryName() {
        return "blue";
    }
}
