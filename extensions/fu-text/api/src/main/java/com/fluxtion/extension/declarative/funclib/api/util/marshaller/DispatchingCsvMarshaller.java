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
package com.fluxtion.extension.declarative.funclib.api.util.marshaller;

import com.fluxtion.extension.declarative.api.Wrapper;
import com.fluxtion.runtime.lifecycle.BatchHandler;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.extension.declarative.funclib.api.ascii.Csv2ByteBuffer;
import com.fluxtion.extension.declarative.funclib.api.event.CharEvent;

/**
 * The generated SEP that implements the execution graph for
 * CsvMultiTypeMarshaller.
 *
 * @author V12 Technology Limited
 */
public class DispatchingCsvMarshaller implements EventHandler, BatchHandler, Lifecycle {

    //Node declarations
    private final Csv2ByteBuffer csv2ByteBuffer_2 = new Csv2ByteBuffer();
    public final CsvMultiTypeMarshaller dispatcher = new CsvMultiTypeMarshaller();
    private final AsciiAnyCharMatcher_1 asciiAnyCharMatcher_1_4 = new AsciiAnyCharMatcher_1();
    private final AsciiAnyCharMatcher_0 asciiAnyCharMatcher_0_3 = new AsciiAnyCharMatcher_0();
    //Dirty flags
    private boolean isDirty_csv2ByteBuffer_2 = false;
    private boolean isDirty_asciiAnyCharMatcher_1_4 = false;
    private boolean isDirty_asciiAnyCharMatcher_0_3 = false;
    //Filter constants

    public DispatchingCsvMarshaller() {
        //csv2ByteBuffer_2
        csv2ByteBuffer_2.fieldNumber = (int) 0;
        csv2ByteBuffer_2.headerLines = (int) 0;
        csv2ByteBuffer_2.eolNotifier = asciiAnyCharMatcher_1_4;
        csv2ByteBuffer_2.delimiterNotifier = asciiAnyCharMatcher_0_3;
        //dispatcher
        dispatcher.type = csv2ByteBuffer_2;
        //asciiAnyCharMatcher_1_4
        //asciiAnyCharMatcher_0_3
    }

    public void addMarshaller(Wrapper wrapper, EventHandler handler) {
        dispatcher.addMarshaller(wrapper, handler);
    }

    public void addSink(EventHandler handler) {
        dispatcher.sink = handler;
    }

    @Override
    public void onEvent(com.fluxtion.runtime.event.Event event) {
        switch (event.eventId()) {
            case (CharEvent.ID): {
                CharEvent typedEvent = (CharEvent) event;
                handleEvent(typedEvent);
                break;
            }
        }
    }

    public void handleEvent(CharEvent typedEvent) {
        switch (typedEvent.filterId()) {
            //Event Class:[com.fluxtion.extension.declarative.funclib.api.event.CharEvent] filterId:[10]
            case (10):
                isDirty_asciiAnyCharMatcher_1_4 = asciiAnyCharMatcher_1_4.onChar_newLine(typedEvent);
                if (isDirty_asciiAnyCharMatcher_1_4) {
                    csv2ByteBuffer_2.onEol(asciiAnyCharMatcher_1_4);
                }
                isDirty_csv2ByteBuffer_2 = csv2ByteBuffer_2.appendToBuffer(typedEvent);
                if (isDirty_csv2ByteBuffer_2) {
                    dispatcher.onTypeUpdated(csv2ByteBuffer_2);
                }
                if (isDirty_asciiAnyCharMatcher_0_3 || isDirty_asciiAnyCharMatcher_1_4) {
                    isDirty_csv2ByteBuffer_2 = csv2ByteBuffer_2.onEvent();
                    if (isDirty_csv2ByteBuffer_2) {
                        dispatcher.onTypeUpdated(csv2ByteBuffer_2);
                    }
                }
                dispatcher.pushCharToMarshaller(typedEvent);
                dispatcher.onEolChar(typedEvent);
                //event stack unwind callbacks
                if (isDirty_asciiAnyCharMatcher_0_3 || isDirty_asciiAnyCharMatcher_1_4) {
                    csv2ByteBuffer_2.onEventComplete();
                }
                afterEvent();
                return;
            //Event Class:[com.fluxtion.extension.declarative.funclib.api.event.CharEvent] filterId:[44]
            case (44):
                isDirty_asciiAnyCharMatcher_0_3 = asciiAnyCharMatcher_0_3.onChar_44(typedEvent);
                if (isDirty_asciiAnyCharMatcher_0_3) {
                    csv2ByteBuffer_2.onDelimiter(asciiAnyCharMatcher_0_3);
                }
                isDirty_csv2ByteBuffer_2 = csv2ByteBuffer_2.appendToBuffer(typedEvent);
                if (isDirty_csv2ByteBuffer_2) {
                    dispatcher.onTypeUpdated(csv2ByteBuffer_2);
                }
                if (isDirty_asciiAnyCharMatcher_0_3 || isDirty_asciiAnyCharMatcher_1_4) {
                    isDirty_csv2ByteBuffer_2 = csv2ByteBuffer_2.onEvent();
                    if (isDirty_csv2ByteBuffer_2) {
                        dispatcher.onTypeUpdated(csv2ByteBuffer_2);
                    }
                }
                dispatcher.pushCharToMarshaller(typedEvent);
                //event stack unwind callbacks
                if (isDirty_asciiAnyCharMatcher_0_3 || isDirty_asciiAnyCharMatcher_1_4) {
                    csv2ByteBuffer_2.onEventComplete();
                }
                afterEvent();
                return;
        }
        //Default, no filter methods
        isDirty_csv2ByteBuffer_2 = csv2ByteBuffer_2.appendToBuffer(typedEvent);
        if (isDirty_csv2ByteBuffer_2) {
            dispatcher.onTypeUpdated(csv2ByteBuffer_2);
        }
        if (isDirty_asciiAnyCharMatcher_0_3 || isDirty_asciiAnyCharMatcher_1_4) {
            isDirty_csv2ByteBuffer_2 = csv2ByteBuffer_2.onEvent();
            if (isDirty_csv2ByteBuffer_2) {
                dispatcher.onTypeUpdated(csv2ByteBuffer_2);
            }
        }
        dispatcher.pushCharToMarshaller(typedEvent);
        //event stack unwind callbacks
        if (isDirty_asciiAnyCharMatcher_0_3 || isDirty_asciiAnyCharMatcher_1_4) {
            csv2ByteBuffer_2.onEventComplete();
        }
        afterEvent();
    }

    @Override
    public void afterEvent() {

        isDirty_csv2ByteBuffer_2 = false;
        isDirty_asciiAnyCharMatcher_1_4 = false;
        isDirty_asciiAnyCharMatcher_0_3 = false;
    }

    @Override
    public void init() {
        csv2ByteBuffer_2.init();
        dispatcher.init();
    }

    @Override
    public void tearDown() {
    }

    @Override
    public void batchPause() {
    }

    @Override
    public void batchEnd() {
    }
}
