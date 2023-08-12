package com.fluxtion.compiler.generation.inject;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;

import java.util.Map;

public class Char2IntFactory implements NodeFactory<InjectionTest.Char2Int> {

    @Override
    public InjectionTest.Char2Int createNode(Map<String, ? super Object> config, NodeRegistry registry) {
        return new InjectionTest.Char2Int();
    }
}
