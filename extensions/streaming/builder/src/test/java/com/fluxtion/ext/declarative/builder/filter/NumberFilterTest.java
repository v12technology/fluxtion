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
package com.fluxtion.ext.declarative.builder.filter;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.declarative.builder.stream.StreamInProcessTest;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.gt;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.num;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import com.fluxtion.ext.streaming.builder.factory.FilterBuilder;
import static com.fluxtion.ext.streaming.builder.factory.StreamFunctionsBuilder.count;
import static com.fluxtion.ext.streaming.builder.factory.StreamFunctionsBuilder.cumSum;
import com.fluxtion.generator.compiler.InprocessSepCompiler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class NumberFilterTest extends StreamInProcessTest {

    public static void main(String[] args) throws Exception {
        InprocessSepCompiler.reuseOrBuild(c ->{
        
        });
    }
    
    @Test
    public void numberFilter() {
        fixedPkg = true;
//        reuseSep = true;
        sep((c) -> {
            select(DataEvent::getIntVal).id("intValStream")
                    .filter(gt(10)).id("filteredIntStream");
            
            select(Double.class).id("doubleStream")
                    .filter(gt(10)).id("filteredDoubleStream");
            
            UserDoubleHandler userDoubleHandler = new UserDoubleHandler();
//            UserDoubleHandler userDoubleHandler = c.addNode(new UserDoubleHandler(), "userDoubleHandler");
            
            FilterBuilder.filter(userDoubleHandler::getMyVal, gt(10)).id("filteredDoubleUserHandler");
            FilterBuilder.filter(userDoubleHandler::getMyIntVal, gt(10)).id("filteredIntUserHandler");
        });
        
        onEvent(12.2);
    }
    


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataEvent {

        private int intVal;
    }

    public static <T extends Number> SerializableFunction<T, Boolean> gt(double test) {
        return new TestingPredicate(test)::greaterThan;
    }

    @Data
    public static class TestingPredicate {

        private final double doubleLimit_0;

        public boolean greaterThan(Number subject) {
            return subject.doubleValue() > doubleLimit_0;
        }
    }
    
    @Data
    public static class UserDoubleHandler{
    
        private double myVal;
        private int myIntVal;
        
        @EventHandler
        public boolean processDouble(Double dIn){
            myVal = dIn;
            myIntVal = (int) myVal;
            return true;
        }
    }
}
