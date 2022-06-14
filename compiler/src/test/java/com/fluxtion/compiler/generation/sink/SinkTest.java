package com.fluxtion.compiler.generation.sink;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.stream.SinkPublisher;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;

public class SinkTest extends MultipleSepTargetInProcessTest {
    public SinkTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void addSinkNode(){
        List<String> myList = new ArrayList<>();
        sep(c -> c.addNode(new MyNode("sinkA")));
        addSink("sinkA", (Consumer<String>) myList::add);
        onEvent("hello world");
        assertThat(myList, Matchers.is(Matchers.contains("hello world")));
    }

    public static class MyNode{

        private final SinkPublisher<String> publisher;// = new SinkPublisher("sinkA");

        public MyNode(String sinkFilter){
            this(new SinkPublisher<>(sinkFilter));
        }

        public MyNode(SinkPublisher<String> publisher){
            this.publisher = publisher;
        }

        @OnEventHandler
        public void newString(String in){
            publisher.publish(in);
        }

    }
}
