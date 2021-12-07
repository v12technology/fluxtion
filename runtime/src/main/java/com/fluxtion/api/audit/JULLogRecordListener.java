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

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Process {@link LogRecord}'s and publishes using java commons logging either to:
 * <ul>
 * <li>Console {@link ConsoleHandler}
 * </li>File - using {@link FileHandler}
 * </ul>
 *
 * @author greg
 */
public class JULLogRecordListener implements LogRecordListener {

    private static Logger logger = Logger.getLogger("fluxtion.eventLog");
    private static Level level = Level.INFO;

    {
        logger.setUseParentHandlers(false);
        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }        
    }
    
    public JULLogRecordListener() {
        ConsoleHandler console = new ConsoleHandler();
        console.setFormatter(new FormatterImpl());
        logger.addHandler(console);
    }

    public JULLogRecordListener(File file) throws IOException {
        FileHandler fileHandler = new FileHandler(file.getCanonicalPath());
        fileHandler.setFormatter(new FormatterImpl());
        logger.addHandler(fileHandler);
    }

    @Override
    public void processLogRecord(LogRecord logRecord) {
        logger.log(level, logRecord.toString() + "\n---\n");
    }

    private static class FormatterImpl extends Formatter {

        @Override
        public String format(java.util.logging.LogRecord record) {
            return record.getMessage();
        }
    }

}
