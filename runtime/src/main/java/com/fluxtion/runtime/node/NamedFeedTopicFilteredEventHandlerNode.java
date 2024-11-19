package com.fluxtion.runtime.node;

import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.event.NamedFeedEvent;

import java.util.Objects;

public class NamedFeedTopicFilteredEventHandlerNode<T>
        extends NamedFeedEventHandlerNode<T> {


    private final String topic;

    public NamedFeedTopicFilteredEventHandlerNode(
            @AssignToField("feedName") String feedName,
            @AssignToField("topic") String topic
    ) {
        super(feedName, "eventFeedHandler_" + feedName + "_" + topic);
        Objects.requireNonNull(topic, "topic cannot be null");
        this.topic = topic;
    }


    @Override
    public <E extends NamedFeedEvent<?>> boolean onEvent(E e) {
        if (e.getTopic() != null && topic.equals(e.getTopic())) {
            feedEvent = (NamedFeedEvent<T>) e;
            return true;
        }
        return false;
    }
}
