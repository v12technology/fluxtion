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
package com.fluxtion.ext.streaming.api.enrich;

import com.fluxtion.api.annotations.Initialise;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Data
public class SimpleLookup<V> implements LookUp<String, V> {
    
    private final String[] keys;
    private final V[] values;
    private transient Map map;

    @Initialise
    public void init(){
        map = new HashMap();
        if(keys.length != values.length){
            throw new RuntimeException("keys and values are different lengtths key:" 
                    + keys.length + " values:" + values.length);
        }
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
    }
    
    @Override
    public V lookup(String key) {
        return (V) map.get(key);
    }
    
}
