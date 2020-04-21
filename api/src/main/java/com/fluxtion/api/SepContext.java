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
package com.fluxtion.api;

import java.util.ServiceLoader;

/**
 * Service providing buildtime access to constructing a SEP.
 *
 * @author V12 Technology Ltd.
 */
public interface SepContext {

    <T> T add(T node);

    <T> T[] add(T... nodes);

    <T> T add(T node, String privateId);

    <T> T addPublic(T node, String publicId);

    <T> T addOrReuse(T node);

    <T> T[] addOrReuse(T... nodes);

    <T> T addOrReuse(T node, String privateId);

    <T> T addPublicOrReuse(T node, String publicId);

    SepContext NULL_CONTEXT = new SepContext() {
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

    };

    static SepContext service() {
        ServiceLoader<SepContext> load = ServiceLoader.load(SepContext.class, SepContext.class.getClassLoader());
        if (load.iterator().hasNext()) {
            return load.iterator().next();
        } else {
            load = ServiceLoader.load(SepContext.class);
            if (load.iterator().hasNext()) {
                return load.iterator().next();
            } else {
                return NULL_CONTEXT;
            }
        }
    }
}
