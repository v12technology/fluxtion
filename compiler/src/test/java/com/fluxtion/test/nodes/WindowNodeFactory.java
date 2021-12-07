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

import com.fluxtion.compiler.builder.node.NodeFactory;
import com.fluxtion.compiler.builder.node.NodeRegistry;
import java.util.Map;

/**
 *
 * @author Greg Higgins
 */
public class WindowNodeFactory implements NodeFactory<WindowNode>{

    @Override
    public WindowNode createNode(Map config, NodeRegistry registry) {
        return new WindowNode();
    }
    
}
