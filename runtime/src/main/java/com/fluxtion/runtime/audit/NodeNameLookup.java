package com.fluxtion.runtime.audit;

import java.util.HashMap;
import java.util.Map;

public class NodeNameLookup implements Auditor{

    private final Map<Object, String> name2NodeMap = new HashMap<>();

    @Override
    public void nodeRegistered(Object node, String nodeName) {
        name2NodeMap.put(node, nodeName);
    }

    public String lookup(Object node){
        return name2NodeMap.getOrDefault(node, "???");
    }

    @Override
    public void init() {
        name2NodeMap.clear();
    }
}
