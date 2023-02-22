package com.fluxtion.runtime.node;

public interface NodeNameLookup {
    String DEFAULT_NODE_NAME = "nodeNameLookup";

    String lookupInstanceName(Object node);

    <T> T getInstanceById(String id) throws NoSuchFieldException;
}
