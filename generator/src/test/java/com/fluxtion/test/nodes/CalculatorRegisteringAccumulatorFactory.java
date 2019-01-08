/* 
 * Copyright (C) 2018 V12 Technology Ltd.
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
package com.fluxtion.test.nodes;

import com.fluxtion.builder.node.NodeFactory;
import com.fluxtion.builder.node.NodeRegistry;
import java.util.Map;

/**
 *
 * @author Greg Higgins
 */
public class CalculatorRegisteringAccumulatorFactory implements NodeFactory<Calculator>{

    private Calculator calc;
    
    @Override
    public Calculator createNode(Map config, NodeRegistry registry) {
        if(calc==null){
            calc = new Calculator();
            calc.accumulator = registry.registerNode(new Accumulator(), "accumulator");
        }
        return calc;
    }
    
}
