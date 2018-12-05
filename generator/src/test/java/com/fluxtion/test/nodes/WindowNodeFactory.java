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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.test.nodes;

import com.fluxtion.api.node.NodeFactory;
import com.fluxtion.api.node.NodeRegistry;
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
