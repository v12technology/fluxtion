/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.ext.text.api.util;

import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.ext.text.api.event.CharEvent;
import com.fluxtion.ext.text.api.event.EofEvent;
import com.fluxtion.ext.text.api.util.ReadEvent.ReadEventFactory;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads files and streams pushing {@link CharEvent} to an {@link EventHandler}.
 * Can be configured to be a synchronous or an asynchronous reader from the
 * input.
 *
 * @author gregp
 */
public class CharStreamer {

    public static CharStreamer stream(File inputFile, EventHandler handler) {
        return new CharStreamer(handler, inputFile);
    }

    public static CharStreamer stream(Reader input, EventHandler handler) {
        return new CharStreamer(handler, input);
    }

    public static CharStreamer stream(Reader input, Class<EventHandler> handler) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return new CharStreamer((EventHandler) handler.getConstructors()[0].newInstance(), input);
    }

    public static CharStreamer stream(File inputFile, Class<EventHandler> handler) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return new CharStreamer((EventHandler) handler.getConstructors()[0].newInstance(), inputFile);
    }

    private boolean asynch = true;
    private MappedByteBuffer mappedBuffer;
    private Disruptor<ReadEvent> disruptor;
    private final EventHandler handler;
    private boolean init = true;
    private final File inputFile;
    private final Reader inputStream;
    private boolean tearDown = true;
    private boolean eof = true;

    private CharStreamer(EventHandler handler, Reader input) {
        this.handler = handler;
        this.inputStream = input;
        this.inputFile = null;
    }

    public CharStreamer(EventHandler handler, File inputFile) {
        this.handler = handler;
        this.inputStream = null;
        this.inputFile = inputFile;
    }

    public CharStreamer async() {
        this.asynch = true;
        return this;
    }

    public CharStreamer sync() {
        this.asynch = false;
        return this;
    }

    public CharStreamer init() {
        this.init = true;
        return this;
    }

    public CharStreamer noInit() {
        this.init = false;
        return this;
    }
    
    public CharStreamer eof(){
        this.eof = true;
        return this;
    }
    
    public CharStreamer noEof(){
        this.eof = true;
        return this;
    }

    public void stream() throws IOException {
        if (init && handler instanceof Lifecycle) {
            ((Lifecycle) handler).init();
        }
        if (asynch) {
            ReadEventFactory factory = new ReadEvent.ReadEventFactory();
            int bufferSize = 16;
//            disruptor = new Disruptor<>(factory, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new BlockingWaitStrategy());
            disruptor = new Disruptor<>(factory, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new BusySpinWaitStrategy());
            disruptor.handleEventsWith(new com.lmax.disruptor.EventHandler<ReadEvent>() {
                @Override
                public void onEvent(ReadEvent event, long sequence, boolean endOfBatch) throws Exception {
                    event.pushToHandler(handler);
                }
            });
            disruptor.start();
            if (inputFile == null) {
                streamAsyncReader();
            } else {
                streamAsyncFile();
            }
        } else {
            if (inputFile == null) {
                streamSyncReader();
            } else {
                streamFile();
            }
        }
        if(eof){
           handler.onEvent(EofEvent.EOF);
        }
        if (tearDown && handler instanceof Lifecycle) {
            ((Lifecycle) handler).tearDown();
        }
    }

    private void streamAsyncFile() throws FileNotFoundException, IOException {
        if (inputFile.exists() && inputFile.isFile()) {
            FileChannel fileChannel = new FileInputStream(inputFile).getChannel();
            long size = inputFile.length();
            mappedBuffer = fileChannel.map(
                    FileChannel.MapMode.READ_ONLY, 0, size);

            RingBuffer<ReadEvent> ringBuffer = disruptor.getRingBuffer();
            final EventTranslatorChannel eventXlator = new EventTranslatorChannel();
            while (!eventXlator.eof) {
                ringBuffer.publishEvent(eventXlator);
            }
            disruptor.shutdown();
        }
    }

    private void streamAsyncReader() {
        RingBuffer<ReadEvent> ringBuffer = disruptor.getRingBuffer();
        final EventTranslatorImpl eventXlator = new EventTranslatorImpl();
        while (!eventXlator.eof) {
            ringBuffer.publishEvent(eventXlator);
        }
        disruptor.shutdown();

    }

    private void streamFile() throws FileNotFoundException, IOException {
        if (inputFile.exists() && inputFile.isFile()) {
            FileChannel fileChannel = new FileInputStream(inputFile).getChannel();
            long size = inputFile.length();
            MappedByteBuffer buffer = fileChannel.map(
                    FileChannel.MapMode.READ_ONLY, 0, size);
            CharEvent charEvent = new CharEvent(' ');
            while (buffer.hasRemaining()) {
                charEvent.setCharacter((char) buffer.get());
                handler.onEvent(charEvent);
            }
            fileChannel.close();
        }
//        handler.onEvent(EofEvent.EOF);
    }

    private void streamSyncReader() throws IOException {
        boolean eof = false;
        ReadEvent event = new ReadEvent(4096);
        while (!eof) {
            int readCount = inputStream.read(event.array);
            event.setLimit(readCount);
            if (readCount < 0) {
                eof = true;
            }
            event.pushToHandler(handler);
        }
    }

    public CharStreamer teardown() {
        this.tearDown = true;
        return this;
    }

    public CharStreamer noTeardown() {
        this.tearDown = false;
        return this;
    }

    private class EventTranslatorChannel implements EventTranslator<ReadEvent> {

        boolean eof = false;

        @Override
        public void translateTo(ReadEvent event, long sequence) {
            if (mappedBuffer.remaining() < 1) {
                event.setLimit(-1);
            } else if (mappedBuffer.remaining() < event.array.length) {
                int i = 0;
                while (mappedBuffer.remaining() > 0) {
                    event.array[i] = (char) mappedBuffer.get();
                    i++;
                }
                eof = true;
                event.setLimit(i);
            } else {
                for (int i = 0; i < event.array.length; i++) {
                    event.array[i] = (char) mappedBuffer.get();
                }
                event.setLimit(event.array.length);
            }
        }
    }

    private class EventTranslatorImpl implements EventTranslator<ReadEvent> {

        boolean eof = false;

        @Override
        public void translateTo(ReadEvent event, long sequence) {
            try {
                int readCount = inputStream.read(event.array);
                event.setLimit(readCount);
                if (readCount < 0) {
                    eof = true;
                }
            } catch (IOException ex) {
                Logger.getLogger(CharStreamer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
