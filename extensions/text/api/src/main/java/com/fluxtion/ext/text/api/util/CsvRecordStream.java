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
package com.fluxtion.ext.text.api.util;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.ext.text.api.csv.RowProcessor;
import com.fluxtion.ext.text.api.util.marshaller.CsvMultiTypeMarshaller;
import com.fluxtion.ext.text.api.util.marshaller.DispatchingCsvMarshaller;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

/**
 * Streams records from a CSV stream to set of StaticEventProcessor. The input
 * is parsed using the provided decoders, each successfully parsed record is
 * passed to each registered StaticEventProcessor's for further processing.</P>
 *
 * Multiple record types and decoders are supported, for the syntax of the input
 * file see {@link CsvMultiTypeMarshaller}.
 *
 * The following inputs are supported:
 * <ul>
 * <li>{@link String}</li>
 * <li>{@link File}</li>
 * <li>{@link Reader}</li>
 * </ul>
 *
 * The decoders will have been generated before using this class.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class CsvRecordStream {

    private final RowProcessor[] decoders;
    private StaticEventProcessor[] sinks;
    private StaticEventProcessor[] sinksNoInit;
    private Class[] noDecode;

    public CsvRecordStream(RowProcessor[] decoders) {
        this.decoders = decoders;
        sinks = new StaticEventProcessor[0];
        sinksNoInit = new StaticEventProcessor[0];
        noDecode = new Class[0];
    }

    public static <R extends RowProcessor> CsvRecordStream decoders(Class<? extends RowProcessor>... decoderClasses) {
        RowProcessor[] decoders = new RowProcessor[decoderClasses.length];
        for (int i = 0; i < decoderClasses.length; i++) {
            try {
                Class decoderClass = decoderClasses[i];
                decoders[i] = (RowProcessor) decoderClass.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException("cannot instantiate RowProcessor instance", ex);
            }
        }
        return new CsvRecordStream(decoders);
    }

    public static CsvRecordStream decoders(RowProcessor... decoders) {
        return new CsvRecordStream(decoders);
    }

    public CsvRecordStream sinks(StaticEventProcessor... sinks) {
        this.sinks = sinks;
        return this;
    }

    public CsvRecordStream sinksNoInit(StaticEventProcessor... sinksNoInit) {
        this.sinksNoInit = sinksNoInit;
        return this;
    }
    
    public CsvRecordStream noDecoding(Class... noDecode){
        this.noDecode = noDecode;
        return this;
    }

    public void stream(String input) {
        DispatchingCsvMarshaller csvDispatch = dispatcher();
        StringDriver.streamChars(input, csvDispatch, false);
    }

    public void stream(Reader reader) throws IOException {
        DispatchingCsvMarshaller csvDispatch = dispatcher();
        CharStreamer.stream(reader, csvDispatch).sync().noInit().stream();
    }

    public void stream(File file) throws IOException {
        DispatchingCsvMarshaller csvDispatch = dispatcher();
        CharStreamer.stream(file, csvDispatch).sync().noInit().stream();
    }

    private DispatchingCsvMarshaller dispatcher() {
        DispatchingCsvMarshaller csvDispatch = new DispatchingCsvMarshaller();
        for (RowProcessor decoder : decoders) {
            csvDispatch.addMarshaller(decoder);
        }
        for (int i = 0; i < sinks.length; i++) {
            StaticEventProcessor sink = sinks[i];
            csvDispatch.addSink(sink);
        }
        for (int i = 0; i < sinksNoInit.length; i++) {
            StaticEventProcessor sink = sinksNoInit[i];
            csvDispatch.addSink(sink, false);
        }
        for (int i = 0; i < noDecode.length; i++) {
            csvDispatch.addNoMarshaller(noDecode[i]);
        }
        return csvDispatch;
    }

}
