/*
 * Copyright (C) 2018 2024 gregory higgins.
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
package com.fluxtion.runtime;

import java.util.ServiceLoader;

/**
 * Service providing buildtime access to constructing a SEP, use {@link #service() } to gain runtime access to the
 * context.
 *
 * @author 2024 gregory higgins.
 */
public interface EventProcessorBuilderService {

    <T> T add(T node);

    <T> T[] add(T... nodes);

    <T> T add(T node, String privateId);

    <T> T addPublic(T node, String publicId);

    <T> T addOrReuse(T node);

    <T> T[] addOrReuse(T... nodes);

    <T> T addOrReuse(T node, String privateId);

    <T> T addPublicOrReuse(T node, String publicId);

    <T> T getNodeById(String id);

    default boolean buildTime() {
        return false;
    }

    EventProcessorBuilderService NULL_CONTEXT = new EventProcessorBuilderService() {
        @Override
        public <T> T add(T node) {
            return node;
        }

        @Override
        public <T> T[] add(T... nodes) {
            return nodes;
        }

        @Override
        public <T> T[] addOrReuse(T... nodes) {
            return nodes;
        }

        @Override
        public <T> T addPublic(T node, String publicId) {
            return node;
        }

        @Override
        public <T> T add(T node, String privateId) {
            return node;
        }

        @Override
        public <T> T addOrReuse(T node) {
            return node;
        }

        @Override
        public <T> T addOrReuse(T node, String privateId) {
            return node;
        }

        @Override
        public <T> T addPublicOrReuse(T node, String publicId) {
            return node;
        }

        @Override
        public <T> T getNodeById(String id) {
            return null;
        }

    };

    static EventProcessorBuilderService service() {
        ServiceLoader<EventProcessorBuilderService> load = ServiceLoader.load(EventProcessorBuilderService.class, EventProcessorBuilderService.class.getClassLoader());
        EventProcessorBuilderService service = NULL_CONTEXT;
        if (load.iterator().hasNext()) {
            service = load.iterator().next();
            return service.buildTime() ? service : NULL_CONTEXT;
        } else {
            load = ServiceLoader.load(EventProcessorBuilderService.class);
            if (load.iterator().hasNext()) {
                service = load.iterator().next();
                return service.buildTime() ? service : NULL_CONTEXT;
            } else {
                return NULL_CONTEXT;
            }
        }
    }
}
