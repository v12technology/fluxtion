/* 
 * Copyright (C) 2018 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.compiler.builder.event;

import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.event.EventPublisher;
import com.fluxtion.compiler.builder.generation.GenerationContext;

/**
 * Builder used to add a {@link EventPublisher} via static helper functions. The
 * generated
 * EventPublsher will be automatically added to the graph context as will any
 * supplied {@link Event} source.
 *
 * @author V12 Technology Ltd.
 */
public class EventPublisherBuilder {

    public static <T extends Event> EventPublisher eventSource(T source) {
        EventPublisher publisher = GenerationContext.SINGLETON.addOrUseExistingNode(new EventPublisher());
        publisher.addEventSource(source);
        GenerationContext.SINGLETON.addOrUseExistingNode(source);
        return publisher;
    }

    public static <T extends Event> EventPublisher eventSource(T source, String name) {
        EventPublisher publisher = GenerationContext.SINGLETON.addOrUseExistingNode(new EventPublisher());
        GenerationContext.SINGLETON.nameNode(publisher, name);
        publisher.addEventSource(source);
        GenerationContext.SINGLETON.addOrUseExistingNode(source);
        return publisher;
    }
}
