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
package com.fluxtion.ext.streaming.api.log;

import com.fluxtion.api.annotations.Initialise;

/**
 * Holds the contents of the log message
 * 
 * @author greg
 */
public class MsgSink {

    private static final int MIN_CAPACITY = 256;
    public int initCapacity = MIN_CAPACITY;
    private StringBuilder sb;

    public int length() {
        return sb.length();
    }

    public void copyAsAscii(byte[] target) {
        int msgSize = sb.length();
        //TODO change to sb.getChars 
        for (int i = 0; i < msgSize; i++) {
            target[i] = (byte) sb. charAt(i);
        }
    }

    @Initialise
    public void init() {
        sb = new StringBuilder(Math.max(MIN_CAPACITY, initCapacity));
    }

    //@AfterEvent
    public void resetLogBuffer() {
        sb.delete(0, sb.length());
    }

    public MsgSink append(Object obj) {
        sb.append(obj);
        return this;
    }

    public MsgSink append(String str) {
        sb.append(str);
        return this;
    }

    public MsgSink append(StringBuffer sb) {
        sb.append(sb);
        return this;
    }

    public MsgSink append(CharSequence s) {
        sb.append(s);
        return this;
    }

    public MsgSink append(CharSequence s, int start, int end) {
        sb.append(s, start, end);
        return this;
    }

    public MsgSink append(char[] str) {
        sb.append(str);
        return this;
    }

    public MsgSink append(char[] str, int offset, int len) {
        sb.append(str, offset, len);
        return this;
    }

    public MsgSink append(boolean b) {
        sb.append(b);
        return this;
    }

    public MsgSink append(char c) {
        sb.append(c);
        return this;
    }

    public MsgSink append(int i) {
        sb.append(i);
        return this;
    }

    public MsgSink append(long lng) {
        sb.append(lng);
        return this;
    }

    public MsgSink append(float f) {
        sb.append(f);
        return this;
    }

    public MsgSink append(double d) {
        sb.append(d);
        return this;
    }

    public MsgSink appendCodePoint(int codePoint) {
        sb.appendCodePoint(codePoint);
        return this;
    }
}
