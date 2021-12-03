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
package com.fluxtion.test.nodegen;

import com.fluxtion.builder.node.DeclarativeNodeConiguration;
import com.fluxtion.builder.node.NodeFactory;
import com.fluxtion.generator.util.BaseSepInProcessTest;
import net.vidageek.mirror.dsl.Mirror;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.fluxtion.test.nodegen.AveragingNodeFactory.AVERAGING_NODE_NAME;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 *
 * @author Greg Higgins
 */
public class AveragingNodeFactoryTest extends BaseSepInProcessTest {

    @Test
    public void newTest() {
        sep((c) ->{
            SampleNode sampleNode = c.addNode(new SampleNode());
            //factory config
            Set<Class<? extends NodeFactory<?>>> factoryList = new HashSet<>();
            factoryList.add(AveragingNodeFactory.class);
            Map<Object, Object> config = new HashMap<>();
            config.put(AveragingNodeFactory.DATA_SOURCE, sampleNode);
            config.put(AveragingNodeFactory.WINDOW_SIZE, 50);
            config.put(AveragingNodeFactory.DATA_SOURCE_FIELD, "sampleValue");
            //root nodes
            Map<Class<?>, String> rootNodeMappings = new HashMap<>();
            rootNodeMappings.put(AveragingNode.class, "averagingNode");
            c.declarativeConfig = new DeclarativeNodeConiguration(rootNodeMappings, factoryList, config);
        });
        Object avgNode = getField(AVERAGING_NODE_NAME);
        int windowSize = (int)new Mirror().on(avgNode).get().field("windowSize");
        SampleNode dataSource =(SampleNode) new Mirror().on(avgNode).get().field("dataSource");
        assertThat(windowSize, CoreMatchers.is(50));
    }


}
