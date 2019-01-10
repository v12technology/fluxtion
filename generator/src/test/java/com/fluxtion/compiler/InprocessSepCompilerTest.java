/*
 * Copyright (C) 2019 V12 Technology Ltd.
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
package com.fluxtion.compiler;

import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.generator.compiler.InprocessSepCompiler;
import com.fluxtion.test.event.AnnotatedHandlerNoFilter;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class InprocessSepCompilerTest {
    
    @Test
    public void inProcessTestSimple() throws InstantiationException, IllegalAccessException, Exception{
        InprocessSepCompiler.buildSep(this::buildSepSingle, "com.gh.test", "GenNode_1");
    }
    
    
    public void buildSepSingle(SEPConfig cfg){
        cfg.addNode(new AnnotatedHandlerNoFilter());
    }
    

}
