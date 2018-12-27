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

import com.fluxtion.runtime.event.Event;

/**
 * Control message for simple logger.
 * 
 * @author Greg Higgins
 */
public final class LogControlEvent extends Event {
    
    public static final String FILTER = "CHANGE_FILTER";
    public static final String LEVEL = "CHANGE_LEVEL";
    public static final String LOG_TIME = "RECORD_TIME";
    public static final String LOG_NAME = "RECORD_NAME";
    public static final String LOG_LEVEL = "RECORD_LEVEL";
    
    private LogControlEvent(String controlType){
        super();
        filterString = controlType;
    }
    
    private LogControlEvent(int controlType){
        super();
        filterId = controlType;
    }
    
    private boolean enabled;
    private int level;
    private String[] filter;
    
    public static LogControlEvent recordMsgBuilderId(boolean enabled){
        LogControlEvent logControlEvent = new LogControlEvent(LOG_NAME); 
        logControlEvent.enabled = enabled;
        return logControlEvent;
    } 
    
    public static LogControlEvent recordMsgBuildTime(boolean enabled){
        LogControlEvent logControlEvent = new LogControlEvent(LOG_TIME); 
        logControlEvent.enabled = enabled;
        return logControlEvent;
    } 
    
    public static LogControlEvent recordMsgLogLevel(boolean enabled){
        LogControlEvent logControlEvent = new LogControlEvent(LOG_LEVEL); 
        logControlEvent.enabled = enabled;
        return logControlEvent;
    } 
    
    public static LogControlEvent disableIdFiltering(){
        LogControlEvent logControlEvent = new LogControlEvent(FILTER); 
        logControlEvent.enabled = false;
        return logControlEvent;
    }
    
    public static LogControlEvent enableIdFiltering(String[] filters){
        LogControlEvent logControlEvent = new LogControlEvent(FILTER); 
        logControlEvent.enabled = true;
        logControlEvent.filter = filters;
        return logControlEvent;
    }
    
    public static LogControlEvent disableLevelFiltering(){
        LogControlEvent logControlEvent = new LogControlEvent(LEVEL); 
        logControlEvent.enabled = false;
        return logControlEvent;
    }
    
    public static LogControlEvent enableLevelFiltering(int level){
        LogControlEvent logControlEvent = new LogControlEvent(LEVEL); 
        logControlEvent.enabled = true;
        logControlEvent.level = level;
        return logControlEvent;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getLevel() {
        return level;
    }

    public String[] getFilter() {
        return filter;
    }
    
    
}
