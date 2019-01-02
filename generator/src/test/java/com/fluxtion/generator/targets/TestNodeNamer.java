/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.generator.targets;

import com.fluxtion.builder.generation.NodeNameProducer;
import com.fluxtion.test.event.DirtyNotifierNode;
import com.fluxtion.test.event.EventHandlerCb;
import com.fluxtion.test.event.InitCB;

/**
 *
 * @author greg
 */
class TestNodeNamer implements NodeNameProducer {
    
    @Override
    public String mappedNodeName(Object nodeToMap) {
        String name = null;
        if (nodeToMap instanceof EventHandlerCb) {
            EventHandlerCb ecb = (EventHandlerCb) nodeToMap;
            name = "handler_" + ecb.id;
        } else if (nodeToMap instanceof DirtyNotifierNode) {
            DirtyNotifierNode dirty = (DirtyNotifierNode) nodeToMap;
            name = "filter_" + dirty.id;
        } else if (nodeToMap instanceof InitCB) {
            InitCB dirty = (InitCB) nodeToMap;
            name = "init_" + dirty.id;
        }
        return name;
    }
    
}
