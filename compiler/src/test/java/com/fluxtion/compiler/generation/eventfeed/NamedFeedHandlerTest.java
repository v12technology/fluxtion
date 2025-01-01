/*
 * Copyright (c) 2019-2025 gregory higgins.
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

package com.fluxtion.compiler.generation.eventfeed;

import com.fluxtion.compiler.builder.dataflow.DataFlow;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.EventProcessorContext;
import com.fluxtion.runtime.EventProcessorContextListener;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.event.NamedFeedEventImpl;
import com.fluxtion.runtime.node.NamedFeedEventHandlerNode;
import com.fluxtion.runtime.node.NamedFeedTopicFilteredEventHandlerNode;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

public class NamedFeedHandlerTest extends MultipleSepTargetInProcessTest {
    public NamedFeedHandlerTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void handleNamedFeedEvent_Test() {
        NamedFeedEventHandlerNode<String> handler = new NamedFeedEventHandlerNode<>("myFeed");
        sep(handler);
        NamedFeedEventHandlerNode<String> processorHandler = getField(handler.getName());

        NamedFeedEventImpl<String> feedEvent = new NamedFeedEventImpl<>("myFeed");
        feedEvent.data("myData");
        onEvent(feedEvent);
        MatcherAssert.assertThat(processorHandler.getFeedEvent().data(), CoreMatchers.is("myData"));

        feedEvent.data("myDataNew");
        onEvent(feedEvent);
        MatcherAssert.assertThat(processorHandler.getFeedEvent().data(), CoreMatchers.is("myDataNew"));

        //should ignore events from a different feed
        NamedFeedEventImpl<String> feedBEvent = new NamedFeedEventImpl<>("feed_B");
        feedBEvent.data("myDataXXX");
        onEvent(feedBEvent);
        MatcherAssert.assertThat(processorHandler.getFeedEvent().data(), CoreMatchers.is("myDataNew"));
    }

    @Test
    public void handleNamedFeedEvent_withFilter_Test() {
        NamedFeedTopicFilteredEventHandlerNode<String> handler = new NamedFeedTopicFilteredEventHandlerNode<>("myFeed", "filter_A");
        sep(handler);
        NamedFeedEventHandlerNode<String> processorHandler = getField(handler.getName());

        //no match on filter
        NamedFeedEventImpl<String> feedEvent = new NamedFeedEventImpl<>("myFeed");
        feedEvent.data("myData");
        onEvent(feedEvent);
        Assert.assertNull(processorHandler.getFeedEvent());

        //should now match with topic
        feedEvent.topic("filter_A");
        onEvent(feedEvent);
        MatcherAssert.assertThat(processorHandler.getFeedEvent().data(), CoreMatchers.is("myData"));

        //ignore different topic
        feedEvent = new NamedFeedEventImpl<>("myFeed", "filter_B");
        feedEvent.data("myData_filter_B");
        onEvent(feedEvent);
        MatcherAssert.assertThat(processorHandler.getFeedEvent().data(), CoreMatchers.is("myData"));

        //match
        feedEvent = new NamedFeedEventImpl<>("myFeed", "filter_A");
        feedEvent.data("myData_filter_A");
        onEvent(feedEvent);
        MatcherAssert.assertThat(processorHandler.getFeedEvent().data(), CoreMatchers.is("myData_filter_A"));
    }

    @Test
    public void subscribeToFeed() {
        sep(c -> {
            DataFlow.subscribeToFeed("myFeed", NamedFeedHandlerTest::byteBufferToString)
                    .mapToList()
                    .id("results");
        });

        NamedFeedEventImpl<ByteBuffer> feedEvent = new NamedFeedEventImpl<>("myFeed");
        feedEvent.data(ByteBuffer.wrap("myData_1".getBytes()));
        onEvent(feedEvent);
        feedEvent.data(ByteBuffer.wrap("myData_2".getBytes()));
        onEvent(feedEvent);

        MatcherAssert.assertThat(getStreamed("results"), Matchers.contains("myData_1", "myData_2"));
    }

    @Test
    public void subscribeToTopicFilteredFeed() {
        sep(c -> {
            DataFlow.subscribeToFeed("myFeed", "topic_A", NamedFeedHandlerTest::byteBufferToString)
                    .mapToList()
                    .id("results");
        });

        NamedFeedEventImpl<ByteBuffer> feedEvent = new NamedFeedEventImpl<>("myFeed");
        feedEvent.data(ByteBuffer.wrap("myData_1".getBytes()));
        onEvent(feedEvent);
        feedEvent.data(ByteBuffer.wrap("myData_2".getBytes()));
        onEvent(feedEvent);

        feedEvent.topic("topic_A");
        feedEvent.data(ByteBuffer.wrap("myData_A1".getBytes()));
        onEvent(feedEvent);
        feedEvent.data(ByteBuffer.wrap("myData_A2".getBytes()));
        onEvent(feedEvent);


        feedEvent.topic("topic_B");
        feedEvent.data(ByteBuffer.wrap("myData_B1".getBytes()));
        onEvent(feedEvent);
        feedEvent.data(ByteBuffer.wrap("myData_B2".getBytes()));
        onEvent(feedEvent);

        MatcherAssert.assertThat(getStreamed("results"), Matchers.contains("myData_A1", "myData_A2"));
    }

    @Test
    public void nodeSubscribeToTopicFilteredFeed() {
        sep(c -> {
            c.addNode(new EventFeedListenerNode(), "node");
        });

        NamedFeedEventImpl<String> feedEvent = new NamedFeedEventImpl<>("myFeed");
        feedEvent.data("myData_1");
        onEvent(feedEvent);

        Assert.assertEquals("myData_1", getField("node", EventFeedListenerNode.class).data);
    }

    public static String byteBufferToString(ByteBuffer buffer) {
        return new String(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining());
    }

    public static class EventFeedListenerNode implements EventProcessorContextListener {

        public static final String MY_EVENT_FEED = "myFeed";
        public String data;

        @Override
        public void currentContext(EventProcessorContext currentContext) {
            currentContext.subscribeToNamedFeed(MY_EVENT_FEED);
        }

        @OnEventHandler(filterString = MY_EVENT_FEED)
        public boolean onEvent(NamedFeedEventImpl<String> event) {
            data = event.data();
            return false;
        }
    }
}
