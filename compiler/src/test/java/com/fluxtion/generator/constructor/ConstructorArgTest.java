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
package com.fluxtion.generator.constructor;

import com.fluxtion.builder.annotation.ConstructorArg;
import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.generator.util.MultipleSepTargetInProcessTest;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class ConstructorArgTest extends MultipleSepTargetInProcessTest {

    public ConstructorArgTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testArgs(){
    
        sep((c) -> {
            final Handler handler = new Handler();
            handler.setName("myhandler");
            c.addPublicNode(handler, "handler");
        });
        
        Handler handler = getField("handler");
        assertThat(handler.getName(), is("myhandler"));
    }
    
    
    @Data
    @NoArgsConstructor
    public static class Handler{

        private String in;
    
        @ConstructorArg
        String name;

        public Handler(String name) {
            this.name = name;
        }
    
        @EventHandler
        public void stringUpdate(String in){
            this.in = in;
        }
    }
}
