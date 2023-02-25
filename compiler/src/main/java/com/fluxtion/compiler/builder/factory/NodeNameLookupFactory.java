package com.fluxtion.compiler.builder.factory;

import com.fluxtion.compiler.generation.GenerationContext;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.audit.NodeNameAuditor;
import com.fluxtion.runtime.node.NodeNameLookup;

import java.util.Map;

public class NodeNameLookupFactory implements NodeFactory<NodeNameLookup> {

    private static NodeNameAuditor SINGLETON;

    @Override
    public NodeNameLookup createNode(Map<String, Object> config, NodeRegistry registry) {
        registry.registerAuditor(SINGLETON, NodeNameLookup.DEFAULT_NODE_NAME);
        return SINGLETON;
    }

    @Override
    public void preSepGeneration(GenerationContext context, Map<String, Auditor> auditorMap) {
        SINGLETON = new NodeNameAuditor();
        auditorMap.put(NodeNameLookup.DEFAULT_NODE_NAME, NodeNameLookupFactory.SINGLETON);
    }
}
