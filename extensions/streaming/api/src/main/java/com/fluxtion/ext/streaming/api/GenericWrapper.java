/*
 * Copyright (c) 2020, V12 Technology Ltd.
 * All rights reserved.
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
package com.fluxtion.ext.streaming.api;

import com.fluxtion.api.annotations.ConfigVariable;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.time.Clock;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * Creates a {@link Wrapper} from a {@link GenericEventHandler} wrapping the 
 * value inside the GenericEvent.
 * 
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"wrappedClass"})
@Data
public class GenericWrapper<W> implements Wrapper<W> {
    
    private final Class<W> wrappedClass;
    private final GenericEventHandler<?, W> handler;

    public GenericWrapper(Class<W> wrappedClass) {
        this(wrappedClass, new GenericEventHandler<>(wrappedClass.getCanonicalName()));
    }

    @OnEvent
    public boolean onEvent() {
        return true;
    }

    @Override
    public W event() {
        return handler.event();
    }

    @Override
    public Class<W> eventClass() {
        return wrappedClass;
    }
    
}
