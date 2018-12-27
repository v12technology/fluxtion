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
package com.fluxtion.extension.declarative.api.numeric;

import com.fluxtion.runtime.event.Event;

/**
 * Will hold a MutableNumeric value
 * 
 * 
 * @author greg
 */
public class NumericResultTarget extends Event {

    public static final int ID = 500;
    
    private final MutableNumericValue target;

    public NumericResultTarget(String filterKey) {
        this(new MutableNumericValue(), filterKey);
    }
    
    public NumericResultTarget(MutableNumericValue target, String filterKey) {
        super(ID);
        filterString = filterKey;
        this.target = target;
    }

    public MutableNumericValue getTarget() {
        return target;
    }

}
