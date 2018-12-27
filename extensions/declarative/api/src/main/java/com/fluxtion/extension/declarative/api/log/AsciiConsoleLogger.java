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
package com.fluxtion.extension.declarative.api.log;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnParentUpdate;
import java.util.Arrays;

/**
 * Writes log messages to console.
 * 
 * @author greg
 */
public class AsciiConsoleLogger {

    private static final int MIN_CAPACITY_BUFFER = 512;
    public int initCapacity = MIN_CAPACITY_BUFFER;
    private byte[] buffer;
    private byte[] metaBuffer;
    public MsgBuilder[] msgBuilders;
    private boolean logName;
    private StringBuilder metaSb;
    private boolean logTime;
    private boolean logLevel;
    private boolean logThread;

    @Initialise
    public void init() {
        int multiplier = msgBuilders == null ? 1 : msgBuilders.length;
        initCapacity = Math.max(initCapacity, multiplier * MIN_CAPACITY_BUFFER);
        buffer = new byte[initCapacity];
        metaBuffer = new byte[MIN_CAPACITY_BUFFER];
        logName = true;
        logTime = true;
        logLevel = true;
        logThread = true;
        metaSb = new StringBuilder(64);
    }

    public void addMsgBuilder(MsgBuilder sink) {
        if (msgBuilders == null) {
            msgBuilders = new MsgBuilder[0];
        }
        msgBuilders = Arrays.copyOf(msgBuilders, msgBuilders.length + 1);
        msgBuilders[msgBuilders.length - 1] = sink;
    }

    @EventHandler(filterString = LogControlEvent.LOG_NAME)
    public void controlIdLogging(LogControlEvent control) {
        logName = control.isEnabled();
    }

    @EventHandler(filterString = LogControlEvent.LOG_TIME)
    public void controlTimeLogging(LogControlEvent control) {
        logTime = control.isEnabled();
    }

    @EventHandler(filterString = LogControlEvent.LOG_LEVEL)
    public void controlLevelLogging(LogControlEvent control) {
        logLevel = control.isEnabled();
    }

    @OnParentUpdate("msgBuilders")
    public void publishMessage(MsgBuilder msg) {
//    @OnEvent
//    public void publishMessage() {
//        msg.name.charAt(0);
        logTime();
        logThread();
        logLevel(msg);
        logName(msg);
        int msgSize = msg.length();// + (logName?(msg.name.length()+4):0);
        if (buffer.length < msgSize) {
            buffer = new byte[msgSize + MIN_CAPACITY_BUFFER];
        }
        msg.copyAsAscii(buffer);
        writeMetaMessage();
        System.out.write(buffer, 0, msgSize);
        metaSb.delete(0, metaSb.length());
    }

    public void writeMetaMessage() {
        int msgSize = metaSb.length();
        if(msgSize<1)
            return;
        if (metaBuffer.length < msgSize) {
            metaBuffer = new byte[msgSize * 2];
        }
        for (int i = 0; i < msgSize; i++) {
            metaBuffer[i] = (byte) metaSb.charAt(i);
        }
        System.out.write(metaBuffer, 0, msgSize);
    }

    private void logTime() {
        if (logTime) {
            long now = System.currentTimeMillis();
            long millis = now % 1000;
            long seconds = now / 1000;
            long s = seconds % 60;
            long m = (seconds / 60) % 60;
            long h = (seconds / (60 * 60)) % 24;
            add2dig(h).append(':');
            add2dig(m).append(':');
            add2dig(s).append('.');
            add3dig(millis).append(' ');
        }
    }

    private StringBuilder add3dig(long num) {
        if (num < 100) {
            metaSb.append('0');
        }
        return add2dig(num);
    }

    private StringBuilder add2dig(long num) {
        if (num < 10) {
            metaSb.append('0');
        }
        return metaSb.append(num);
    }

    private void logName(MsgBuilder msg) {
        if (logName & msg.name != null) {
            metaSb.append('[').append(msg.name).append(']').append(' ');
        }
    }

    private void logLevel(MsgBuilder msg) {
        if (!logLevel) {
            return;
        }
        switch (msg.logLevel) {
            case 0:
                metaSb.append("FATAL ");
                break;
            case 1:
                metaSb.append("ERROR ");
                break;
            case 2:
                metaSb.append("WARNING ");
                break;
            case 3:
                metaSb.append("INFO ");
                break;
            case 4:
                metaSb.append("DEBUG ");
                break;
            default:
                metaSb.append("TRACE ");
        }
    }

    private void logThread() {
        if (logThread) {
            metaSb.append('[').append(Thread.currentThread().getName()).append("] ");
        }
    }

}
