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
package com.fluxtion.ext.futext.builder.math;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.generation.GenerationContext;
import com.fluxtion.ext.declarative.api.numeric.NumericValue;


/**
 *
 * @author greg
 */
public class CountFunction implements NumericValue{
    public Object tracked;
    private int count;
    
    public static NumericValue count(Object tracked){
        CountFunction count = new CountFunction();
        count.tracked = tracked;
        GenerationContext.SINGLETON.getNodeList().add(count);
        return count;
    }
    
    @OnEvent 
    public void increment(){
        count++;
    }
    
    @Initialise
    public void init(){
        count = 0;
    }

    @Override
    public int intValue() {
        return count;
    }

    @Override
    public long longValue() {
        return count;
    }

    @Override
    public double doubleValue() {
        return count;
    }
    
}
