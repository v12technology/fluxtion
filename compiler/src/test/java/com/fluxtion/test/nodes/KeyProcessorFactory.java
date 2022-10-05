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
import com.google.auto.service.AutoService;

import java.util.Map;

/**
 *
 * @author Greg Higgins
 */
@AutoService(NodeFactory.class)
public class KeyProcessorFactory implements NodeFactory<KeyProcessor> {

//    public static final String KEY_CHAR = KeyProcessorFactory.class.getName() + ".charKey";
//    public static final String KEY_NOTIFY_ACCUM = KeyProcessorFactory.class.getName() + ".notifyAccumulator";
    public static final String KEY_CHAR =  "KeyProcessorFactory.charKey";
    public static final String KEY_NOTIFY_ACCUM = "KeyProcessorFactory.notifyAccumulator";

    @Override
    public KeyProcessor createNode(Map config, NodeRegistry registry) {
        KeyProcessor processor = new KeyProcessor();
        char keyChar;
        if(config.get(KEY_CHAR) instanceof String){
            keyChar = ((String)config.get(KEY_CHAR)).charAt(0);
        }else{
            keyChar = (char) config.get(KEY_CHAR);
        }
        processor.myChar = keyChar;
        processor.setFilterId(keyChar);
        processor.notifyAccumulator = Boolean.valueOf(config.computeIfAbsent(KEY_NOTIFY_ACCUM, (k)->"false").toString());
        if(processor.notifyAccumulator)
            processor.accumulator = registry.findOrCreateNode(Accumulator.class, config, null);
        return processor;
    }

}
