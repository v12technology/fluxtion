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
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.test.event;

import com.fluxtion.api.lifecycle.FilteredEventHandler;
import java.io.Serializable;

/**
 * Implements EventHandler interface
 * @author Greg Higgins
 */
public class TimeHandlerImpl implements Serializable, FilteredEventHandler<TimeEvent>{

    public int filterId;

    public TimeHandlerImpl(int filterId) {
        this.filterId = filterId;
    }
    
    @Override
    public int filterId() {
        return filterId;
    }

    @Override
    public void onEvent(TimeEvent e) {
    }
    
}
