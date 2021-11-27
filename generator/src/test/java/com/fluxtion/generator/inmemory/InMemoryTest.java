package com.fluxtion.generator.inmemory;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.TearDown;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.generator.Generator;
import com.fluxtion.generator.targets.InMemoryEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class InMemoryTest {

    @Test
    public void test1() throws Exception {
        SEPConfig cfg = new SEPConfig();
        cfg.addNode(new ChildNode(new StringHandler()));
        Generator generator = new Generator();
        InMemoryEventProcessor inMemoryEventProcessor = generator.inMemoryProcessor(cfg);
        inMemoryEventProcessor.init();

        inMemoryEventProcessor.onEvent("HelloWorld");

        inMemoryEventProcessor.tearDown();
    }

    static class StringHandler{

        @Initialise
        public void init(){
            log.warn("StringHandler::init");
        }

        @TearDown
        public void tearDown(){
            log.warn("StringHandler::tearDown");
        }

        @EventHandler
        public void inString(String in){
            log.warn("StringHandler::invoke - {}", in);
        }
    }

    @RequiredArgsConstructor
    static class ChildNode{
        final StringHandler stringHandler;

        @Initialise
        public void init(){
            log.warn("ChildNode::init");
        }

        @TearDown
        public void tearDown(){
            log.warn("ChildNode::tearDown");
        }

        @OnEvent
        public void updated(){
            log.warn("ChildNode::updated");
        }


    }
}
