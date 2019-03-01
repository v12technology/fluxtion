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
package com.fluxtion.ext.declarative.builder;

import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.ext.declarative.api.SepContext;
import com.google.auto.service.AutoService;

/**
 *
 * @author V12 Technology Ltd.
 */
@AutoService(SepContext.class)
public class SepContextService implements SepContext {

    @Override
    public <T> T add(T node) {
        GenerationContext.SINGLETON.getNodeList().add(node);
        return node;
    }

    @Override
    public <T> T addPublic(T node, String publicId) {
        GenerationContext.SINGLETON.getPublicNodes().put(node, publicId);
        return node;
    }

    @Override
    public <T> T add(T node, String privateId) {
        GenerationContext.SINGLETON.getNodeList().add(node);
        GenerationContext.SINGLETON.nameNode(node, privateId);
        return node;
    }

    @Override
    public <T> T addOrReuse(T node) {
        return GenerationContext.SINGLETON.addOrUseExistingNode(node);
    }

    @Override
    public <T> T addOrReuse(T node, String privateId) {
        node = GenerationContext.SINGLETON.addOrUseExistingNode(node);
        GenerationContext.SINGLETON.nameNode(node, privateId);
        return node;
    }

    @Override
    public <T> T addPublicOrReuse(T node, String publicId) {
        node = GenerationContext.SINGLETON.addOrUseExistingNode(node);
        GenerationContext.SINGLETON.getPublicNodes().put(node, publicId);
        return node;
    }

}
