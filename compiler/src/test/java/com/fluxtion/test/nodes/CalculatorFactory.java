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
package com.fluxtion.test.nodes;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import java.util.Map;

/**
 *
 * @author Greg Higgins
 */
public class CalculatorFactory implements NodeFactory<Calculator>{

    private Calculator calc;
    
    @Override
    public Calculator createNode(Map config, NodeRegistry registry) {
        if(calc==null){
            calc = new Calculator();
            int count = 10;            
            calc.accumulator = registry.findOrCreateNode(Accumulator.class, config, null);
            config.put(KeyProcessorFactory.KEY_CHAR, (char) ('='));
            config.put(KeyProcessorFactory.KEY_NOTIFY_ACCUM, "false");
            calc.calculateKey = registry.findOrCreateNode(KeyProcessor.class, config, null);
        }
        return calc;
    }
    
}
