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
        feedEvent.setData("myData");
        onEvent(feedEvent);
        MatcherAssert.assertThat(processorHandler.getFeedEvent().getData(), CoreMatchers.hasItems("myData"));

        feedEvent.setData("myDataNew");
        onEvent(feedEvent);
        MatcherAssert.assertThat(processorHandler.getFeedEvent().getData(), CoreMatchers.hasItems("myDataNew"));

        //should ignore events from a different feed
        NamedFeedEventImpl<String> feedBEvent = new NamedFeedEventImpl<>("feed_B");
        feedBEvent.setData("myDataXXX");
        onEvent(feedBEvent);
        MatcherAssert.assertThat(processorHandler.getFeedEvent().getData(), CoreMatchers.hasItems("myDataNew"));
    }

    @Test
    public void handleNamedFeedEvent_withFilter_Test() {
        NamedFeedTopicFilteredEventHandlerNode<String> handler = new NamedFeedTopicFilteredEventHandlerNode<>("myFeed", "filter_A");
        sep(handler);
        NamedFeedEventHandlerNode<String> processorHandler = getField(handler.getName());

        //no match on filter
        NamedFeedEventImpl<String> feedEvent = new NamedFeedEventImpl<>("myFeed");
        feedEvent.setData("myData");
        onEvent(feedEvent);
        Assert.assertNull(processorHandler.getFeedEvent());

        //should now match with topic
        feedEvent.setTopic("filter_A");
        onEvent(feedEvent);
        MatcherAssert.assertThat(processorHandler.getFeedEvent().getData(), CoreMatchers.hasItems("myData"));

        //ignore different topic
        feedEvent = new NamedFeedEventImpl<>("myFeed", "filter_B");
        feedEvent.setData("myData_filter_B");
        onEvent(feedEvent);
        MatcherAssert.assertThat(processorHandler.getFeedEvent().getData(), CoreMatchers.hasItems("myData"));

        //match
        feedEvent = new NamedFeedEventImpl<>("myFeed", "filter_A");
        feedEvent.setData("myData_filter_A");
        onEvent(feedEvent);
        MatcherAssert.assertThat(processorHandler.getFeedEvent().getData(), CoreMatchers.hasItems("myData_filter_A"));
    }

    @Test
    public void subscribeToFeed() {
        sep(c -> {
            DataFlow.subscribeToFeed("myFeed", NamedFeedHandlerTest::byteBufferToString)
                    .mapToList()
                    .id("results");
        });

        NamedFeedEventImpl<ByteBuffer> feedEvent = new NamedFeedEventImpl<>("myFeed");
        feedEvent.setData(ByteBuffer.wrap("myData_1".getBytes()));
        onEvent(feedEvent);
        feedEvent.setData(ByteBuffer.wrap("myData_2".getBytes()));
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

    @Test
    public void nodeSubscribeToTopicFilteredFeed() {
        sep(c -> {
            c.addNode(new EventFeedListenerNode(), "node");
        });

        NamedFeedEventImpl<String> feedEvent = new NamedFeedEventImpl<>("myFeed");
        feedEvent.setData("myData_1");
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
            data = event.getData().get(0);
            return false;
        }
    }
}
