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
package com.fluxtion.compiler.builder.time;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import com.fluxtion.runtime.time.Clock;

import java.util.Map;

/**
 * @author V12 Technology Ltd.
 */
public class ClockFactory implements NodeFactory<Clock> {

    public static final Clock SINGLETON = new Clock();

    @Override
    public Clock createNode(Map<String, ? super Object> config, NodeRegistry registry) {
        registry.registerAuditor(SINGLETON, "clock");
        return SINGLETON;
    }

}
