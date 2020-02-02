/*
 * Copyright (C) 2019 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.streaming.builder.event;

import com.fluxtion.api.lifecycle.FilteredEventHandler;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.builder.generation.NodeNameProducer;
import com.fluxtion.ext.streaming.api.GenericEventHandler;
import com.fluxtion.ext.streaming.api.GenericWrapper;
import com.fluxtion.generator.targets.JavaGenHelper;
import com.google.auto.service.AutoService;

/**
 * A Service that implements node naming strategy for EventHandler nodes, see
 * {@link NodeNameProducer}
 *
 * @author V12 Technology Ltd.
 */
@AutoService(NodeNameProducer.class)
public class EventHandlerNamer implements NodeNameProducer {

    @Override
    public String mappedNodeName(Object nodeToMap) {
        String name = null;
        if (nodeToMap instanceof GenericEventHandler) {
            GenericEventHandler g = (GenericEventHandler) nodeToMap;
            String filterString = g.filterString();
            name = "handlerClass_" + filterString.substring(filterString.lastIndexOf("."));
        } else if (nodeToMap instanceof EventHandler) {
            name = "handler" + ((EventHandler) nodeToMap).eventClass().getSimpleName();
            if (nodeToMap instanceof FilteredEventHandler
                    && ((FilteredEventHandler) nodeToMap).filterId() != Integer.MAX_VALUE) {
                name += "_" + ((FilteredEventHandler) nodeToMap).filterId();
            } else if (nodeToMap instanceof FilteredEventHandler
                    && ((FilteredEventHandler) nodeToMap).filterString() != null) {
                name += "_" + ((FilteredEventHandler) nodeToMap).filterString();
            }
        }else if(nodeToMap instanceof GenericWrapper) {
            GenericWrapper g = (GenericWrapper) nodeToMap;
            name = "genericWrapper_" +  g.getWrappedClass().getSimpleName();
        }
        if (name != null) {
            name = JavaGenHelper.getIdentifier(name);
        }
        return name;
    }

}
