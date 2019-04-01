/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.ext.futext.api.util;

import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.ext.futext.api.event.CharEvent;
import com.fluxtion.ext.futext.api.event.EofEvent;
import com.lmax.disruptor.EventFactory;
import java.nio.CharBuffer;

/**
 *
 * @author gregp
 */
public class ReadEvent {

    public final char[] array;
    private  int limit;
    private boolean eof;
    CharEvent ce = new CharEvent(' ');

    public ReadEvent(int capacity) {
        array = new char[capacity];
    }

    public boolean isEof() {
        return eof;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
        eof = limit < 0;
    }
    
    public void pushToHandler(EventHandler handler) {
        if (isEof()) {
            handler.onEvent(EofEvent.EOF);
        } else {
            for (int i = 0; i < limit; i++) {
                ce.setCharacter(array[i]);
                handler.onEvent(ce);
            }
        }
    }

    public static class ReadEventFactory implements EventFactory<ReadEvent> {

        @Override
        public ReadEvent newInstance() {
            return new ReadEvent(4 * 256 * 1024);
        }
    }

}
