/*
 * Copyright (C) 2019 V12 Technology Ltd.
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

/**
 * Simple console implementation of LogService. 
 * 
 * @author V12 Technology Ltd.
 */
public class ConsoleLogProvider implements LogService {
    
    @Override
    public void trace(CharSequence msg) {
        log("[TRACE] ", msg);
    }

    @Override
    public void debug(CharSequence msg) {
        log("[DEBUG] ", msg);
    }

    @Override
    public void info(CharSequence msg) {
        log("[INFO] ", msg);
    }

    @Override
    public void warn(CharSequence msg) {
        log("[WARN] ", msg);
    }

    @Override
    public void error(CharSequence msg) {
        log("[ERROR] ", msg);
    }

    @Override
    public void fatal(CharSequence msg) {
        log("[FATAL] ", msg);
    }

    private void log(CharSequence prefix, CharSequence msg) {
        System.out.print(prefix);
        System.out.println(msg);
    }
    
}
