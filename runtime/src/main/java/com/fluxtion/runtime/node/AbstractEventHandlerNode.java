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
package com.fluxtion.runtime.node;


/**
 * @param <T> The type of event processed by this handler
 * @author Greg Higgins
 */
public abstract class AbstractEventHandlerNode<T> implements EventHandlerNode<T> {

    protected int filterId;

    public AbstractEventHandlerNode(int filterId) {
        this.filterId = filterId;
    }

    public AbstractEventHandlerNode() {
        this(0);
    }

    @Override
    public final int filterId() {
        return filterId;
    }

    public void setFilterId(int filterId) {
        this.filterId = filterId;
    }
}