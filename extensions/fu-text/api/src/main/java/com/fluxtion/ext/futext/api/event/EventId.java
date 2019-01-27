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
package com.fluxtion.ext.futext.api.event;

/**
 *
 * @author Greg Higgins
 */
public interface EventId {

    public static final int CHAR_EVENT_ID = 1;
    public static final int EOF_EVENT_ID = 2;
    public static final int LOG_CONTROL_ID = 3;
    public static final int REGISTER_EVENTHANDLER_ID = 4;
    
}
