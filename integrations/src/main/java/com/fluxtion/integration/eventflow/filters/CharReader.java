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
package com.fluxtion.integration.eventflow.filters;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.ext.text.api.event.CharEvent;
import com.fluxtion.ext.text.api.util.CharStreamer;
import com.fluxtion.ext.text.api.util.marshaller.CharProcessor;
import com.fluxtion.integration.eventflow.PipelineFilter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
public class CharReader extends PipelineFilter implements CharProcessor, StaticEventProcessor{
    
    private CharStreamer streamer;
    
    private CharReader(File file){
        streamer = CharStreamer.stream(file, this);
    }
    
    private CharReader(Reader reader){
        streamer = CharStreamer.stream(reader, this);
    }
    
    public static CharReader of(File file){
        return new CharReader(file);
    }
    
    public static CharReader of(Reader reader){
        return new CharReader(reader);
    }
    
    public CharReader synch(){
        streamer.sync();
        return this;
    }
    
    public CharReader asynch(){
        streamer.async();
        return this;
    }

    @Override
    public void handleEvent(CharEvent charEvent) {
        propagate(charEvent);
    }

    @Override
    public void onEvent(Object event) {
        propagate(event);
    }

    @Override
    public void processEvent(Object o) {
    }

    @Override
    protected void stopHandler() {
        super.stopHandler(); 
    }

    @Override
    protected void startHandler() {
        try {
            streamer.noInit().noTeardown().stream();
        } catch (IOException ex) {
            log.error("porblem while streaming from reader", ex);
        }
    }
    
    
    
    
    
}
