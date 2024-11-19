package com.fluxtion.compiler.generation.eventfeed;

import com.fluxtion.compiler.builder.dataflow.DataFlow;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.event.NamedFeedEvent;
import com.fluxtion.runtime.node.NamedFeedEventHandlerNode;
import com.fluxtion.runtime.node.NamedFeedTopicFilteredEventHandlerNode;
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

        NamedFeedEvent<String> feedEvent = new NamedFeedEvent<>("myFeed");
        feedEvent.setData("myData");
        onEvent(feedEvent);
        Assert.assertEquals("myData", processorHandler.getFeedEvent().getData());

        feedEvent.setData("myDataNew");
        onEvent(feedEvent);
        Assert.assertEquals("myDataNew", processorHandler.getFeedEvent().getData());

        //should ignore events from a different feed
        NamedFeedEvent<String> feedBEvent = new NamedFeedEvent<>("feed_B");
        feedBEvent.setData("myDataXXX");
        onEvent(feedBEvent);
        Assert.assertEquals("myDataNew", processorHandler.getFeedEvent().getData());
    }

    @Test
    public void handleNamedFeedEvent_withFilter_Test() {
        NamedFeedTopicFilteredEventHandlerNode<String> handler = new NamedFeedTopicFilteredEventHandlerNode<>("myFeed", "filter_A");
        sep(handler);
        NamedFeedEventHandlerNode<String> processorHandler = getField(handler.getName());

        //no match on filter
        NamedFeedEvent<String> feedEvent = new NamedFeedEvent<>("myFeed");
        feedEvent.setData("myData");
        onEvent(feedEvent);
        Assert.assertNull(processorHandler.getFeedEvent());

        //should now match with topic
        feedEvent.setTopic("filter_A");
        onEvent(feedEvent);
        Assert.assertEquals("myData", processorHandler.getFeedEvent().getData());

        //ignore different topic
        feedEvent = new NamedFeedEvent<>("myFeed", "filter_B");
        feedEvent.setData("myData_filter_B");
        onEvent(feedEvent);
        Assert.assertEquals("myData", processorHandler.getFeedEvent().getData());

        //match
        feedEvent = new NamedFeedEvent<>("myFeed", "filter_A");
        feedEvent.setData("myData_filter_A");
        onEvent(feedEvent);
        Assert.assertEquals("myData_filter_A", processorHandler.getFeedEvent().getData());
    }

    @Test
    public void subscribeToFeed() {
        sep(c -> {
            DataFlow.subscribeToFeed("myFeed", NamedFeedHandlerTest::byteBufferToString)
                    .mapToList()
                    .id("results");
        });

        NamedFeedEvent<ByteBuffer> feedEvent = new NamedFeedEvent<>("myFeed");
        feedEvent.setData(ByteBuffer.wrap("myData_1".getBytes()));
        onEvent(feedEvent);
        feedEvent.setData(ByteBuffer.wrap("myData_2".getBytes()));
        onEvent(feedEvent);

        MatcherAssert.assertThat(getStreamed("results"), Matchers.contains("myData_1", "myData_2"));
    }

    @Test
    public void subscribeToTopicFilteredFeed() {
        writeSourceFile = true;
        sep(c -> {
            DataFlow.subscribeToFeed("myFeed", "topic_A", NamedFeedHandlerTest::byteBufferToString)
                    .mapToList()
                    .id("results");
        });

        NamedFeedEvent<ByteBuffer> feedEvent = new NamedFeedEvent<>("myFeed");
        feedEvent.setData(ByteBuffer.wrap("myData_1".getBytes()));
        onEvent(feedEvent);
        feedEvent.setData(ByteBuffer.wrap("myData_2".getBytes()));
        onEvent(feedEvent);

        feedEvent.setTopic("topic_A");
        feedEvent.setData(ByteBuffer.wrap("myData_A1".getBytes()));
        onEvent(feedEvent);
        feedEvent.setData(ByteBuffer.wrap("myData_A2".getBytes()));
        onEvent(feedEvent);


        feedEvent.setTopic("topic_B");
        feedEvent.setData(ByteBuffer.wrap("myData_B1".getBytes()));
        onEvent(feedEvent);
        feedEvent.setData(ByteBuffer.wrap("myData_B2".getBytes()));
        onEvent(feedEvent);

        MatcherAssert.assertThat(getStreamed("results"), Matchers.contains("myData_A1", "myData_A2"));
    }

    public static String byteBufferToString(ByteBuffer buffer) {
        return new String(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining());
    }
}
