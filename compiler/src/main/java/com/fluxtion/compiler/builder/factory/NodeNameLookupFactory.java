package com.fluxtion.compiler.builder.factory;

import com.fluxtion.runtime.audit.NodeNameLookup;
import com.google.auto.service.AutoService;

import java.util.Map;

@AutoService(NodeFactory.class)
public class NodeNameLookupFactory implements NodeFactory<NodeNameLookup> {

    static NodeNameLookup SINGLETON = new NodeNameLookup();

    @Override
    public NodeNameLookup createNode(Map<String, Object>config, NodeRegistry registry) {
        registry.registerAuditor(SINGLETON, "nodeNameLookup");
        return SINGLETON;
    }
}
