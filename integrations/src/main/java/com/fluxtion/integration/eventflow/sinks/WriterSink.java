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
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author V12 Technology Ltd.
 */
public class WriterSink implements EventSink{

    private final Writer writer;

    public WriterSink(Writer writer) {
        this.writer = writer;
    }
    
    @Override
    public String id() {
        return "writer-sink";
    }

    @Override
    public void publish(Object o) {
        try {
            writer.write(o.toString());
            writer.write("\n");
        } catch (IOException ex) {
            Logger.getLogger(WriterSink.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
