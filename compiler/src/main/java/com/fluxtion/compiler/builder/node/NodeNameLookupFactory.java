package com.fluxtion.compiler.builder.node;

import com.fluxtion.runtim.audit.NodeNameLookup;
import com.google.auto.service.AutoService;

import java.util.Map;

@AutoService(NodeFactory.class)
public class NodeNameLookupFactory implements NodeFactory<NodeNameLookup> {

    static NodeNameLookup SINGLETON = new NodeNameLookup();

    @Override
    public NodeNameLookup createNode(Map<?, ?> config, NodeRegistry registry) {
        registry.registerAuditor(SINGLETON, "nodeNameLookup");
        return SINGLETON;
    }
}
