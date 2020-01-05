/*
 * Copyright (C) 2020 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.api.audit;

/**
 * No operation logger, has no side effects on any function call, ie no logging
 *
 * @author gregp
 */
public final class NullEventLogger extends EventLogger {

    public static final NullEventLogger INSTANCE = new NullEventLogger();

    private NullEventLogger() {
        super(null, null);
    }

    @Override
    public void log(String key, boolean value, EventLogControlEvent.LogLevel logLevel) {
    }

    @Override
    public void log(String key, CharSequence value, EventLogControlEvent.LogLevel logLevel) {
    }

    @Override
    public void log(String key, double value, EventLogControlEvent.LogLevel logLevel) {
    }

    @Override
    public void trace(String key, double value) {
    }

    @Override
    public void debug(String key, double value) {
    }

    @Override
    public void info(String key, double value) {
    }

    @Override
    public void warn(String key, double value) {
    }

    @Override
    public void error(String key, double value) {
    }
    
    @Override
    public void error(String key, char value) {
    }

    @Override
    public void warn(String key, char value) {
    }

    @Override
    public void info(String key, char value) {
    }

    @Override
    public void debug(String key, char value) {
    }

    @Override
    public void trace(String key, char value) {
    }
    
    @Override
    public void trace(String key, boolean value) {
    }

    @Override
    public void debug(String key, boolean value) {
    }

    @Override
    public void info(String key, boolean value) {
    }

    @Override
    public void warn(String key, boolean value) {
    }

    @Override
    public void error(String key, boolean value) {
    }

    @Override
    public void trace(String key, String value) {
    }

    @Override
    public void debug(String key, String value) {
    }

    @Override
    public void info(String key, String value) {
    }

    @Override
    public void warn(String key, String value) {
    }

    @Override
    public void error(String key, String value) {
    }

    @Override
    public void setLevel(EventLogControlEvent.LogLevel level) {
    }

}
