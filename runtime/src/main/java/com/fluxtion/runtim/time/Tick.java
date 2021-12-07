/*
 * Copyright (c) 2019, V12 Technology Ltd.
 * All rights reserved.
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
package com.fluxtion.runtim.time;

import com.fluxtion.runtim.event.Event;
import lombok.Data;

/**
 * A tick event notifies a clock to update its wall clock time. Any nodes that
 * depend upon clock will check their state and fire events as necessary.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Data
public class Tick implements Event {

    private long eventTime;
}
