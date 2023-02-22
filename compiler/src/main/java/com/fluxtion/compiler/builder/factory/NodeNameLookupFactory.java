package com.fluxtion.compiler.builder.factory;

import com.fluxtion.runtime.audit.NodeNameAuditor;
import com.fluxtion.runtime.node.NodeNameLookup;
import com.google.auto.service.AutoService;

import java.util.Map;

@AutoService(NodeFactory.class)
public class NodeNameLookupFactory implements NodeFactory<NodeNameLookup> {

    public static NodeNameAuditor SINGLETON = new NodeNameAuditor();

    @Override
    public NodeNameLookup createNode(Map<String, Object> config, NodeRegistry registry) {
        registry.registerAuditor(SINGLETON, NodeNameLookup.DEFAULT_NODE_NAME);
        return SINGLETON;
    }
}
