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

import com.fluxtion.api.annotations.AfterEvent;
import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import java.util.Arrays;

/**
 * Base class for building log messages. Stores the actual log message in a MsgSink.
 *
 * @author greg
 */
public abstract class LogMsgBuilder {

    private LogService logService;

    public String name;
    public int logLevel = 3;
    private String _name;
    private boolean logPrefix;
    private boolean filterMatched;
    private boolean levelMatched;
    private boolean goodToLog;
    public MsgSink msgSink;
    private static final int MIN_CAPACITY = 256;
    public int initCapacity = MIN_CAPACITY;

    public String getName() {
        return _name;
    }

    public boolean isGoodToLog() {
        return goodToLog;
    }

    public int length() {
        return msgSink.length();
    }

    public void copyAsAscii(byte[] target) {
        msgSink.copyAsAscii(target);
    }

    public void setMsgSink(MsgSink msgSink) {
        this.msgSink = msgSink;
    }

    public LogMsgBuilder name(String name){
        this.name = name;
        return this;
    }
    
    public LogMsgBuilder level(int level){
        this.logLevel = level;
        return this;
    }

    public boolean isLogPrefix() {
        return logPrefix;
    }

    public void setLogPrefix(boolean logPrefix) {
        this.logPrefix = logPrefix;
    }
    
    protected void log(){
        switch (logLevel) {
            case 0:
                logService.fatal(msgSink.asCharSequence());
                break;
            case 1:
                logService.error(msgSink.asCharSequence());
                break;
            case 2:
                logService.warn(msgSink.asCharSequence());
                break;
            case 3:
                logService.info(msgSink.asCharSequence());
                break;
            case 4:
                logService.debug(msgSink.asCharSequence());
                break;
            case 5:
                logService.trace(msgSink.asCharSequence());
                break;
            default:
                logService.debug(msgSink.asCharSequence());
        }   
        msgSink.resetLogBuffer();
    }
    
    @EventHandler(filterString = LogControlEvent.PROVIDER, propagate = false)
    public void controlLogProvider(LogControlEvent control) {
        logService = control.getLogService();
    }
    
    @EventHandler(filterString = LogControlEvent.FILTER, propagate = false)
    public boolean controlLogIdFilter(LogControlEvent control) {
        control.getFilter();
        filterMatched = Arrays.stream(control.getFilter()).anyMatch(s -> _name.startsWith(s));
        if (filterMatched & levelMatched) {
            goodToLog = true;
        } else {
            goodToLog = false;
        }
        return false;
    }

    @EventHandler(filterString = LogControlEvent.LEVEL, propagate = false)
    public boolean controlLogLevelFilter(LogControlEvent control) {
        levelMatched = logLevel < control.getLevel();
        if (filterMatched & levelMatched) {
            goodToLog = true;
        } else {
            goodToLog = false;
        }
        return false;
    }

//    @AfterEvent
    public void afterEvent() {
        goodToLog = filterMatched & levelMatched;
        if (goodToLog) {
            msgSink.resetLogBuffer();
        }
    }

    @Initialise
    public void init() {
        if (msgSink == null) {
            msgSink = new MsgSink();
            msgSink.initCapacity = Math.max(MIN_CAPACITY, initCapacity);
            msgSink.init();
        }
        if(logService == null){
            logService = new ConsoleLogProvider();
            ((ConsoleLogProvider)logService).logPrefix(logPrefix);
        }
        this._name = name;
        this.filterMatched = true;
        levelMatched = true;
        goodToLog = true;
    }

}
