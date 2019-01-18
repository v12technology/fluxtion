package com.fluxtion.ext.futext.api.util;


import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.ext.futext.api.event.CharEvent;
import com.fluxtion.ext.futext.api.event.EofEvent;
import com.fluxtion.ext.futext.api.util.AsciiCharEventFileStreamer.CharSink;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gregp
 */
public class CharEventStreamer {

    private int chunkSize = 4 * 256 * 1024;
    final Exchanger<ByteChunk> exchanger = new Exchanger();
    final Exchanger<CharChunk> charExchanger = new Exchanger();
    private ExecutorService threadPool = Executors.newSingleThreadExecutor();

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public void streamFromReader(final Reader reader, EventHandler eventHandler) throws FileNotFoundException, IOException, InterruptedException {
        CharEventStreamer.this.streamFromReader(reader, eventHandler, true);
    }

    public void streamFromReader(final Reader reader, EventHandler eventHandler, boolean callLifeCycleMethods) throws FileNotFoundException, IOException, InterruptedException {
        boolean isCharSink = eventHandler instanceof AsciiCharEventFileStreamer.CharSink;
        CharSink charSink = null;
        if (isCharSink) {
            charSink = (CharSink) eventHandler;
        }
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    CharChunk writeChunk = new CharChunk(chunkSize);
                    int numCharsRead;
                    while ((numCharsRead = reader.read(writeChunk.data, 0, writeChunk.data.length)) != -1) {
                        if (numCharsRead == chunkSize) {
                            writeChunk.full();
                        } else if (numCharsRead > 0) {
                            writeChunk.eof(numCharsRead);
                        }
                        writeChunk = charExchanger.exchange(writeChunk);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(CharEventStreamer.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    shutDown();
                }
            }
        });

        if (isCharSink) {
            pushCharChunkToHandler(charSink, callLifeCycleMethods);
        } else {
            pushCharChunkToHandler(eventHandler, callLifeCycleMethods);
        }

    }

    public void streamFromFile(final File file, EventHandler eventHandler) throws FileNotFoundException, IOException, InterruptedException {
        streamFromFile(file, eventHandler, true);
    }

    public void streamFromFile(final File file, EventHandler eventHandler, boolean callLifeCycleMethods) throws FileNotFoundException, IOException, InterruptedException {
        boolean isCharSink = eventHandler instanceof AsciiCharEventFileStreamer.CharSink;
        CharSink charSink = null;
        if (isCharSink) {
            charSink = (CharSink) eventHandler;
        }
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    FileChannel fileChannel = new FileInputStream(file).getChannel();
                    long size = file.length();
                    MappedByteBuffer buffer = fileChannel.map(
                            FileChannel.MapMode.READ_ONLY, 0, size);
                    ByteChunk writeChunk = new ByteChunk(chunkSize);
                    while (buffer.hasRemaining()) {
                        if (buffer.remaining() > chunkSize) {
                            buffer.get(writeChunk.data);
                            writeChunk.full();
                        } else {
                            //eof
                            writeChunk.eof(buffer.remaining());
                            buffer.get(writeChunk.data, 0, writeChunk.limit);
                        }
                        writeChunk = exchanger.exchange(writeChunk);
                    }

                } catch (Exception ex) {
                    Logger.getLogger(CharEventStreamer.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    shutDown();
                }
            }
        });
        try {
            if (isCharSink) {
                pushToHandler(charSink, callLifeCycleMethods);
            } else {
                pushToHandler(eventHandler, callLifeCycleMethods);
            }
        } catch (Exception e) {
            threadPool.shutdownNow();
        }
    }

    public void shutDown() {
        threadPool.shutdown();
    }

    private void pushCharChunkToHandler(EventHandler eventHandler, boolean callLifeCycleMethods) throws InterruptedException {
        if (callLifeCycleMethods) {
            initSep(eventHandler);
        }
        CharChunk readChunk = new CharChunk(chunkSize);
        CharEvent charEvent = new CharEvent(' ');
        while (!readChunk.eofChunk) {
            readChunk = charExchanger.exchange(readChunk);
            for (int i = 0; i < readChunk.limit; i++) {
                charEvent.setCharacter(readChunk.data[i]);
                eventHandler.onEvent(charEvent);
            }
        }
        eventHandler.onEvent(new EofEvent());
        if (callLifeCycleMethods) {
            tearDownSep(eventHandler);
        }
    }

    private void pushCharChunkToHandler(CharSink charSink, boolean callLifeCycleMethods) throws InterruptedException {
        if (callLifeCycleMethods) {
            initSep((EventHandler) charSink);
        }
        CharChunk readChunk = new CharChunk(chunkSize);
        CharEvent charEvent = new CharEvent(' ');
        while (!readChunk.eofChunk) {
            readChunk = charExchanger.exchange(readChunk);
            for (int i = 0; i < readChunk.limit; i++) {
                charEvent.setCharacter(readChunk.data[i]);
                charSink.handleCharEvent(charEvent);
            }
        }
        charSink.handleEofEvent(new EofEvent());
        if (callLifeCycleMethods) {
            tearDownSep((EventHandler) charSink);
        }
    }

    private void pushToHandler(EventHandler eventHandler, boolean callLifeCycleMethods) throws InterruptedException {
        if (callLifeCycleMethods) {
            initSep(eventHandler);
        }
        ByteChunk readChunk = new ByteChunk(chunkSize);
        CharEvent charEvent = new CharEvent(' ');
        while (!readChunk.eofChunk) {
            readChunk = exchanger.exchange(readChunk);
            for (int i = 0; i < readChunk.limit; i++) {
                charEvent.setCharacter((char) readChunk.data[i]);
                eventHandler.onEvent(charEvent);
            }
        }
        eventHandler.onEvent(new EofEvent());
        if (callLifeCycleMethods) {
            tearDownSep(eventHandler);
        }
    }

    private void pushToHandler(CharSink charSink, boolean callLifeCycleMethods) throws InterruptedException {
        if (callLifeCycleMethods) {
            initSep((EventHandler) charSink);
        }
        ByteChunk readChunk = new ByteChunk(chunkSize);
        CharEvent charEvent = new CharEvent(' ');
        while (!readChunk.eofChunk) {
            readChunk = exchanger.exchange(readChunk);
            for (int i = 0; i < readChunk.limit; i++) {
                charEvent.setCharacter((char) readChunk.data[i]);
                charSink.handleCharEvent(charEvent);
            }
        }
        
        ((EventHandler)charSink).onEvent(new EofEvent());
        if (callLifeCycleMethods) {
            tearDownSep((EventHandler) charSink);
        }
    }

    private static void initSep(EventHandler sep) {
        if (sep instanceof Lifecycle) {
            ((Lifecycle) sep).init();
        }
    }

    private static void tearDownSep(EventHandler sep) {
        if (sep instanceof Lifecycle) {
            ((Lifecycle) sep).tearDown();
        }
    }

    private static class ByteChunk {

        final byte[] data;
        boolean eofChunk = false;
        int limit;

        public ByteChunk(int size) {
            data = new byte[size];
        }

        void reset() {
            limit = 0;
            eofChunk = false;
        }

        void full() {
            limit = data.length;
            eofChunk = false;
        }

        void eof(int limit) {
            this.limit = limit;
            eofChunk = true;
        }

    }

    private static class CharChunk {

        final char[] data;
        boolean eofChunk = false;
        int limit;

        public CharChunk(int size) {
            data = new char[size];
        }

        void reset() {
            limit = 0;
            eofChunk = false;
        }

        void full() {
            limit = data.length;
            eofChunk = false;
        }

        void eof(int limit) {
            this.limit = limit;
            eofChunk = true;
        }

    }

}
