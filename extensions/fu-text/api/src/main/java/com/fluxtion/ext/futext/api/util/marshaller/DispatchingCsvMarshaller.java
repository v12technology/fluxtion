package com.fluxtion.ext.futext.api.util.marshaller;

import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.ext.futext.api.ascii.Csv2ByteBufferTemp;
import com.fluxtion.ext.futext.api.event.CharEvent;

public class DispatchingCsvMarshaller implements EventHandler, BatchHandler, Lifecycle {

    //Node declarations
    private final Csv2ByteBufferTemp csv2ByteBufferTemp_1 = new Csv2ByteBufferTemp();
    public final CsvMultiTypeMarshaller dispatcher = new CsvMultiTypeMarshaller();
    //Dirty flags
    private boolean isDirty_csv2ByteBufferTemp_1 = false;
    //Filter constants

    public DispatchingCsvMarshaller() {
        csv2ByteBufferTemp_1.fieldNumber = (int) 0;
        csv2ByteBufferTemp_1.headerLines = (int) 0;
        dispatcher.type = csv2ByteBufferTemp_1;
    }

    public void addMarshaller(Class wrapper, EventHandler handler) {
        dispatcher.addMarshaller(wrapper, handler);
    }

    public void addSink(EventHandler handler) {
        dispatcher.setSink(handler);
    }

    @Override
    public void onEvent(com.fluxtion.api.event.Event event) {
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
            //Event Class:[com.fluxtion.ext.futext.api.event.CharEvent] filterId:[10]
            case (10):
                isDirty_csv2ByteBufferTemp_1 = csv2ByteBufferTemp_1.onEol(typedEvent);
                if (isDirty_csv2ByteBufferTemp_1) {
                    dispatcher.onTypeUpdated(csv2ByteBufferTemp_1);
                }
                dispatcher.pushCharToMarshaller(typedEvent);
                afterEvent();
                return;
            //Event Class:[com.fluxtion.ext.futext.api.event.CharEvent] filterId:[44]
            case (44):
                isDirty_csv2ByteBufferTemp_1 = csv2ByteBufferTemp_1.onDelimiter(typedEvent);
                if (isDirty_csv2ByteBufferTemp_1) {
                    dispatcher.onTypeUpdated(csv2ByteBufferTemp_1);
                }
                dispatcher.pushCharToMarshaller(typedEvent);
                afterEvent();
                return;
        }
        //Default, no filter methods
        isDirty_csv2ByteBufferTemp_1 = csv2ByteBufferTemp_1.appendToBuffer(typedEvent);
        dispatcher.pushCharToMarshaller(typedEvent);
        //event stack unwind callbacks
        afterEvent();
    }

    @Override
    public void afterEvent() {
        csv2ByteBufferTemp_1.onEventComplete();
        isDirty_csv2ByteBufferTemp_1 = false;
    }

    @Override
    public void init() {
        csv2ByteBufferTemp_1.init();
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
