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
package com.fluxtion.ext.declarative.api.log;

/**
 * An interface for a log provider to implement and transform messages to an
 * underlying service provider. Logging implementations can be late bound at
 * runtime using the {@link LogControlEvent#setLogService(LogService) } to
 * create a control message and publish to the SEP. 
 *
 * @author V12 Technology Ltd.
 */
public interface LogService {

    void trace(CharSequence msg);

    void debug(CharSequence msg);

    void info(CharSequence msg);

    void warn(CharSequence msg);

    void error(CharSequence msg);

    void fatal(CharSequence msg);

}
