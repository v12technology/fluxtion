/*
 * Copyright (C) 2019 V12 Technology Limited
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxtion.example.core.events;

import com.fluxtion.builder.annotation.Disabled;
import com.fluxtion.builder.annotation.SepBuilder;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.example.core.events.batch.BatchNode;
import com.fluxtion.example.core.events.batch.DataHandler;
import com.fluxtion.example.core.events.clean.CleanListener;
import com.fluxtion.example.core.events.clean.ConditioningHandler;
import com.fluxtion.example.core.events.clean.DirtyCleanListener;
import com.fluxtion.example.core.events.clean.DirtyListener;
import com.fluxtion.example.core.events.collections.Aggregator;
import com.fluxtion.example.core.events.collections.ConfigHandler;
import com.fluxtion.example.core.events.dirty.DirtyAggregator;
import com.fluxtion.example.core.events.dirty.DirtyNode;
import com.fluxtion.example.core.events.filtering.MyEventProcessor;
import com.fluxtion.example.core.events.graph.CombinerNode;
import com.fluxtion.example.core.events.parent.ParentIdentifier;
import com.fluxtion.example.core.events.postevent.ResetAfterEvent;
import com.fluxtion.example.core.events.postevent.ResetDataEvent;
import com.fluxtion.example.core.events.postevent.ResetGlobal;
import com.fluxtion.example.core.events.propagation.PropagateControlledNode;
import com.fluxtion.example.core.events.propagation.PropagateControlledhandler;
import com.fluxtion.example.core.events.push.Cache;
import com.fluxtion.example.core.events.push.CacheReader;
import com.fluxtion.example.core.events.push.CacheWriter;
import com.fluxtion.example.shared.ChildNode;
import com.fluxtion.example.shared.DataEventHandler;
import com.fluxtion.example.shared.MyEventHandler;
import com.fluxtion.example.shared.PipelineNode;
import java.util.Arrays;
import java.util.List;

/**
 * A set of SEP builder methods that accompany the Fluxtion reference. The builders
 * here demonstrate the event processing capabilities of Fluxtion SEP.
 * documentation.
 *
 * @author greg higgins
 */
//@Disabled
public class SepBuilderEvents {

    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.events.batch.generated",
            outputDir = "src/main/java"
    )
    public void buildInjection(SEPConfig cfg) {
        DataHandler handler = cfg.addNode(new DataHandler());
        BatchNode node = cfg.addNode(new BatchNode(handler));
        cfg.addNode(new ChildNode(node));
    }

    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.events.clean.generated",
            outputDir = "src/main/java"
    )
    public void buildClean(SEPConfig cfg) {
        ConditioningHandler myEventHandler = cfg.addNode(new ConditioningHandler());
        cfg.addNode(new CleanListener(myEventHandler));
        cfg.addNode(new DirtyListener(myEventHandler));
        cfg.addNode(new DirtyCleanListener(myEventHandler));
    }

    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.events.collections.generated",
            outputDir = "src/main/java"
    )
    public void buildCollections(SEPConfig cfg) {
        MyEventHandler handler1 = cfg.addNode(new MyEventHandler());
        MyEventHandler handler2 = cfg.addNode(new MyEventHandler());
        DataEventHandler handler3 = cfg.addNode(new DataEventHandler());
        ConfigHandler cfg0 = cfg.addNode(new ConfigHandler());
        ConfigHandler cfg1 = cfg.addNode(new ConfigHandler());
        ConfigHandler cfg2 = cfg.addNode(new ConfigHandler());
        List<ConfigHandler> cfgHandlers = Arrays.asList(cfg1, cfg2);
        Aggregator agg = cfg.addNode(new Aggregator(new Object[]{handler1, handler2, handler3, cfg0}, cfgHandlers));
    }

    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.events.dirty.generated",
            outputDir = "src/main/java"
    )
    public void buildDirty(SEPConfig cfg) {
        DataEventHandler dataHandler_1 = cfg.addNode(new DataEventHandler());
        MyEventHandler myEventHandler_2 = cfg.addNode(new MyEventHandler());

        DirtyNode node1 = cfg.addNode(new DirtyNode(dataHandler_1));
        DirtyNode node2 = cfg.addNode(new DirtyNode(dataHandler_1));
        DirtyNode node3 = cfg.addNode(new DirtyNode(myEventHandler_2));

        DirtyAggregator aggregator = cfg.addNode(new DirtyAggregator(node1, node2, node3));
    }

    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.events.filtering.generated",
            outputDir = "src/main/java"
    )
    public void buildFiltering(SEPConfig cfg) {
        cfg.addNode(new MyEventProcessor("cfg.acl"));
        cfg.maxFiltersInline = 20;
    }

    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.events.graph.generated",
            outputDir = "src/main/java"
    )
    public void buildGraph(SEPConfig cfg) {
        DataEventHandler sharedHandler = cfg.addNode(new DataEventHandler());
        PipelineNode node = cfg.addNode(new PipelineNode(sharedHandler));
        ChildNode childNode = cfg.addNode(new ChildNode(node));
        MyEventHandler myHandler = cfg.addNode(new MyEventHandler());
        CombinerNode combiner = cfg.addNode(new CombinerNode(myHandler, sharedHandler));
    }

    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.events.multihandler.generated",
            outputDir = "src/main/java"
    )
    public void buildMultiHandler(SEPConfig cfg) {
        com.fluxtion.example.core.events.multihandler.MyEventProcessor handler = new com.fluxtion.example.core.events.multihandler.MyEventProcessor();
        cfg.addNode(handler);
    }

    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.events.parent.generated",
            outputDir = "src/main/java"
    )
    public void buildParent(SEPConfig cfg) {
        DataEventHandler datahandler_1 = cfg.addNode(new DataEventHandler());
        DataEventHandler datahandler_2 = cfg.addNode(new DataEventHandler());
        MyEventHandler myHandler = cfg.addNode(new MyEventHandler());
        ParentIdentifier identifier = cfg.addNode(new ParentIdentifier(datahandler_1, datahandler_2, myHandler));
    }

    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.events.pipeline.generated",
            outputDir = "src/main/java"
    )
    public void buildPipeline(SEPConfig cfg) {
        DataEventHandler handler = new DataEventHandler();
        PipelineNode node = new PipelineNode(handler);
        cfg.addNode(handler);
        cfg.addNode(node);
    }

    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.events.postevent.generated",
            outputDir = "src/main/java"
    )
    public void buildPostevent(SEPConfig cfg) {
        DataEventHandler handler = cfg.addNode(new DataEventHandler());
        ResetDataEvent node = cfg.addNode(new ResetDataEvent(handler));
        ResetGlobal resetAny = cfg.addNode(new ResetGlobal(node));
        ResetAfterEvent resetAfter = cfg.addNode(new ResetAfterEvent(resetAny));
    }

    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.events.propagation.generated",
            outputDir = "src/main/java"
    )
    @Disabled //fix bug on repeateable generation - graphml is undpredictable
    public void buildPropagation(SEPConfig cfg) {
        DataEventHandler dataHandler_1 = cfg.addNode(new DataEventHandler());
        PropagateControlledhandler myEventHandler_2 = cfg.addNode(new PropagateControlledhandler());
        PropagateControlledNode aggregator = cfg.addNode(new PropagateControlledNode(myEventHandler_2, dataHandler_1));
    }

    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.events.push.generated",
            outputDir = "src/main/java"
    )
    public void buildPush(SEPConfig cfg) {
        MyEventHandler myHandler = cfg.addNode(new MyEventHandler());
        Cache cache = cfg.addNode(new Cache());
        CacheReader cacheReader = cfg.addNode(new CacheReader(cache, myHandler));
        CacheWriter cacheWriter = cfg.addNode(new CacheWriter(cache, myHandler));
    }

    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.events.singlehandler.generated",
            outputDir = "src/main/java"
    )
    public void buildSinglehandler(SEPConfig cfg) {
        com.fluxtion.example.core.events.singlehandler.MyEventProcessor  handler = new com.fluxtion.example.core.events.singlehandler.MyEventProcessor();
        cfg.addNode(handler);
    }
}
