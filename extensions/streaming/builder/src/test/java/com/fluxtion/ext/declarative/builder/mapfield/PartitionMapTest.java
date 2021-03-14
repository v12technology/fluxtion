/*
 * Copyright (C) 2021 V12 Technology Ltd.
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
package com.fluxtion.ext.declarative.builder.mapfield;

import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.streaming.api.stream.PartitioningFieldMapper;
import lombok.Value;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class PartitionMapTest extends StreamInprocessTest{
    
    @Test
    public void partioniningTest(){
        sep(c ->{
            c.addNode(new PartitioningFieldMapper(new MyFunctionFactory("hello")::buildFunction));
        });
    }
    
    @Value
    public static class MyFunctionFactory{
    
        String name;
        
        public String buildFunction(){
            return name;
        }
    }
}
