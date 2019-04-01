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
package com.fluxtion.ext.futext.builder.util;

import com.fluxtion.ext.futext.api.event.CharEvent;
import com.fluxtion.ext.futext.api.event.EofEvent;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Synchronous char streamer, reads bytes from a file and pushes CharEvent for
 * each byte read into a registered EventHandler. An EofEvent is published when
 * the end of the file is reached.
 *
 * @author Greg Higgins
 */
public class AsciiCharEventFileStreamer {

    public static void streamFromFile(File file, CharSink sink) throws FileNotFoundException, IOException {
        streamFromFile(file, sink, true);
    }

    public static void streamFromFile(File file, CharSink sink, boolean callLifeCycleMethods) throws FileNotFoundException, IOException {
        if (file.exists() && file.isFile()) {
            if (callLifeCycleMethods) {
                initSep(sink);
            }
            FileChannel fileChannel = new FileInputStream(file).getChannel();
            long size = file.length();
            MappedByteBuffer buffer = fileChannel.map(
                    FileChannel.MapMode.READ_ONLY, 0, size);
            CharEvent charEvent = new CharEvent(' ');
            while (buffer.hasRemaining()) {
                charEvent.setCharacter((char) buffer.get());
                sink.handleCharEvent(charEvent);
            }
            sink.handleEofEvent(EofEvent.EOF);
            if (callLifeCycleMethods) {
                tearDownSep(sink);
            }
        }
    }

    public static <E extends EventHandler> E streamFromFile(File file, Class<E> eventHandler) throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException {
        final E handler = eventHandler.newInstance();
        streamFromFile(file, handler, true);
        return handler;
    }

    public static void streamFromFile(File file, EventHandler eventHandler) throws FileNotFoundException, IOException {
        streamFromFile(file, eventHandler, false);
    }

    public static void streamFromFile(File file, EventHandler eventHandler, boolean callLifeCycleMethods) throws FileNotFoundException, IOException {
        if (callLifeCycleMethods) {
            initSep(eventHandler);
        }
        if (file.exists() && file.isFile()) {
            FileChannel fileChannel = new FileInputStream(file).getChannel();
            long size = file.length();
            MappedByteBuffer buffer = fileChannel.map(
                    FileChannel.MapMode.READ_ONLY, 0, size);
            CharEvent charEvent = new CharEvent(' ');
            while (buffer.hasRemaining()) {
                charEvent.setCharacter((char) buffer.get());
                eventHandler.onEvent(charEvent);
            }
        }
        eventHandler.onEvent(EofEvent.EOF);
        if (callLifeCycleMethods) {
            tearDownSep(eventHandler);
        }
    }

    private static void initSep(Object sep) {
        if (sep instanceof Lifecycle) {
            ((Lifecycle) sep).init();
        }
    }

    private static void tearDownSep(Object sep) {
        if (sep instanceof Lifecycle) {
            ((Lifecycle) sep).tearDown();
        }
    }

    public static interface CharSink {

        public void handleCharEvent(CharEvent event);

        default void handleEofEvent(EofEvent event) {
        }
    }

}
