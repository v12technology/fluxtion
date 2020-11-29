/*
 * Copyright (c) 2020, V12 Technology Ltd.
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
package com.fluxtion.ext.streaming.api.window;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnEventComplete;
import com.fluxtion.api.annotations.SepNode;
import com.fluxtion.ext.streaming.api.Stateful;
import lombok.Data;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Data
public class CountReset {

    @SepNode
    private final Stateful source;
    private final int bucketSize;
    private int count;

    @OnEvent
    public boolean newBucket() {
        count++;
        if(count >= bucketSize){
            return true;
        }
        return false;
    }

    @OnEventComplete
    public void resetIfNecessary() {
        if (count >= bucketSize) {
            count = 0;
            source.reset();
        }
    }
    
    @Initialise
    public void init(){
        count = 0;
    }

}
