/*
 * Copyright (C) Copyright (C) 2020 2018 V12 Technology Ltd.
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
package com.fluxtion.integration.eventflow.sinks;

import com.fluxtion.integration.eventflow.EventSink;

/**
 *
 * @author V12 Technology Ltd.
 */
public class ConsoleSink implements EventSink{

    @Override
    public String id() {
        return "console-sink";
    }

    @Override
    public void publish(Object o) {
        System.out.println("SINK console -> " + o);
    }

    @Override
    public boolean publishSync(Object o) {
        System.out.println("ASYNC SINK console -> " + o);
        return true;
    }
}
