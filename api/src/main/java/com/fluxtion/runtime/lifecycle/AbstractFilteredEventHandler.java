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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.runtime.lifecycle;

import com.fluxtion.runtime.event.Event;


/**
 * @author Greg Higgins
 */
public abstract class AbstractFilteredEventHandler<T extends Event> implements FilteredEventHandler<T>{

    protected int filterId;
    
    public AbstractFilteredEventHandler(int filterId) {
        this.filterId = filterId;
    }

    public AbstractFilteredEventHandler() {
        this(0);
    }
    
    @Override
    public final int filterId(){
        return filterId;
    }

    public void setFilterId(int filterId) {
        this.filterId = filterId;
    }
    
    @Override
    public abstract void onEvent(T e) ;


    
}
