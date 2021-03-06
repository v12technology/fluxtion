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
package com.fluxtion.ext.streaming.api.numeric;

import com.fluxtion.api.event.Event;

/**
 *
 * @author V12 Technology Ltd.
 */
public class NumericSignal implements Event{

    private final String filter;
    private final MutableNumber mutableNumber;
    
    public NumericSignal(String filter){
        this.filter = filter;
        mutableNumber = new MutableNumber();
    
    }
    
    public NumericSignal(int value, String id){
        this(id);
        mutableNumber.set(value);
    }

    @Override
    public String filterString() {
        return filter;
    }

    public Number getNumber() {
        return mutableNumber;
    }
}
