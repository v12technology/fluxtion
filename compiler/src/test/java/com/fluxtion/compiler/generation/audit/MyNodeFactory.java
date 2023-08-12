package com.fluxtion.compiler.generation.audit;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;

import java.util.Map;

public class MyNodeFactory implements NodeFactory<FactoryAuditorTest.MyNode> {

    @Override
    public FactoryAuditorTest.MyNode createNode(Map config, NodeRegistry registry) {
        final FactoryAuditorTest.MyNode myNode = new FactoryAuditorTest.MyNode();
        registry.registerAuditor(myNode, "myNode");
        return myNode;
    }

}
