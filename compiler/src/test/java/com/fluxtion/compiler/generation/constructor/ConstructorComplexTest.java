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
package com.fluxtion.compiler.generation.constructor;

import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.partition.LambdaReflection;
import lombok.Data;
import lombok.Value;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.function.Supplier;

/**
 *
 * @author V12 Technology Ltd.
 */
public class ConstructorComplexTest extends MultipleSepTargetInProcessTest {

    public ConstructorComplexTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testArgs(){
    
        fixedPkg = true;
        sep((c) -> {
//            final MyThing thing = c.addNode(new MyThing());
            c.addPublicNode(new Handler(new MyThing()), "handler");
        });
        
        Handler handler = getField("handler");
        Assert.assertNotNull(handler.getName());
    }

    @Test
    public void constructorMethodRefTest(){
        sep(c ->{
            c.addNode(new ConstructorMethodRef(Date::new));
        });
    }

    @Value
    public static class ConstructorMethodRef{
        LambdaReflection.SerializableSupplier<Date> dateSupplier;

        @OnEventHandler
        public void stringIn(String in){}
    }
    
    
    @Data
    public static class Handler{

        private final MyThing name;

        public Handler(MyThing name) {
            this.name = name;
        }
        
    
        @OnEventHandler
        public void stringUpdate(String in){
        }
    }
    
    public static class MyThing{}
}

