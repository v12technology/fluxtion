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
package com.fluxtion.test.event;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnBatchEnd;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnEventComplete;
import com.fluxtion.api.annotations.TearDown;
import java.lang.reflect.Method;

/**
 *
 * @author Greg Higgins
 */
public class InitCB {

    public Object[] parents;
    public String id;
    public double[] myDoublerr;
    
    public int intVal;
    public double doubleVal;
    public boolean booleanVal;

    public InitCB(String id, Object... parents) {
        this.parents = parents;
        this.id = id;
        myDoublerr = new double[]{1.4,6.7};
    }
    
    public InitCB(String id) {
        this.id = id;
        myDoublerr = new double[]{1.4,6.7};
    }

    public InitCB() {
    }
    
    @OnEvent
    public void onEvent() {

    }

    @OnEventComplete
    public void onEventComplete(){
        
    }
    
    @Initialise
    public void init() {

    }
    
    @TearDown
    public void tearDown(){
        
    }
    
    @OnBatchEnd
    public void onBatchEnd(){
        
    }
    
    @Override
    public String toString() {
        return "InitCB{" + "id=" + id + '}';
    }
}
