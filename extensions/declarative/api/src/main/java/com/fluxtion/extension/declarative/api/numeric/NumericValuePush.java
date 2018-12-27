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

import com.fluxtion.api.annotations.AfterEvent;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;

/**
 * An abstract class that can be extended to push a numeric value to a target instance.
 * 
 * @author Greg Higgins
 * @param <T> Push target type
 */
public abstract class NumericValuePush<T>{
    public NumericValue source;
    public T target;
    protected transient boolean pushUpdate;
    
    @OnParentUpdate("source")
    public void onNumericUpdated(NumericValue source){
        pushUpdate = true;
    }

    @OnEvent
    public abstract void pushNumericValue();
    
    @AfterEvent
    public void afterEvent(){
        pushUpdate = false;
    }
}
