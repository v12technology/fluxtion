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
package com.fluxtion.ext.streaming.api.records;

import java.util.Map;

/**
 *
 * @author V12 Technology Ltd.
 */
public class RecordMap<K,V> {
    public static <K,V> RecordMap<K, V> mapOf(Class<K> k,Class<V> c){
        return null;
    }
    
    public static void main(String[] args) {
        RecordMap<String, String> mt = mapOf(String.class, String.class);
        
        Class<String> s = String.class;
        Class<Map> m = Map.class;
//        RecordMap.mapOf( clazz);
    }
}
