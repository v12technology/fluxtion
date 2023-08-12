package com.fluxtion.compiler.generation.inject;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;

import java.util.Map;

public class CharHandlerFactory implements NodeFactory<InjectionTest.CharHandler> {

    @Override
    public InjectionTest.CharHandler createNode(Map<String, ? super Object> arg0, NodeRegistry arg1) {
        if (arg0.containsKey("char")) {
            return new InjectionTest.CharHandler(((String) arg0.get("char")).charAt(0));
        }
        return new InjectionTest.CharHandler();
    }

}
