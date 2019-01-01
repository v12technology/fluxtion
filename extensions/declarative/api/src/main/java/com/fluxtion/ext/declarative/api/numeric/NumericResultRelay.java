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
package com.fluxtion.ext.declarative.api.numeric;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.FilterId;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnParentUpdate;
import java.util.HashSet;

/**
 * Listens to a NumericValue parent and pushes the result to a MutableNumeric
 * target. The target is provided by an event, MumericResultTarget. Filtering 
 * is provided by filterKey so only MumericResultTarget with matching keys are 
 * registered.
 * 
 * This class is useful for testing
 * 
 * @author greg
 */
public class NumericResultRelay {
    
    @FilterId
    private String filterKey;

    public NumericResultRelay() {
    }

    public NumericResultRelay(String filterKey, NumericValue source) {
        this.filterKey = filterKey;
        this.source = source;
    }
    
    private HashSet<MutableNumericValue> targets;
    
    public NumericValue source;
    
    @EventHandler
    public void addTarget(NumericResultTarget target){
        targets.add(target.getTarget());
    }
    
    @OnParentUpdate
    public void storeResult(NumericValue value){
        targets.forEach(val -> val.copyFrom(value));
    }
    
    @Initialise
    public void init(){
        targets = new HashSet<>();
    }
    
}
