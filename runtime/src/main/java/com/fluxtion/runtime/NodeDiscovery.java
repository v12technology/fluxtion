package com.fluxtion.runtime;

public interface NodeDiscovery {

    <T> T getNodeById(String id) throws NoSuchFieldException;
}
