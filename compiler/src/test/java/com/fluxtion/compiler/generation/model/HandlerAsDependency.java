package com.fluxtion.compiler.generation.model;

import com.fluxtion.runtim.annotations.EventHandler;
import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.compiler.generation.util.BaseModelTest;
import lombok.Data;
import org.junit.Test;

import static com.fluxtion.runtim.partition.LambdaReflection.getMethod;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

public class HandlerAsDependency extends BaseModelTest {

    @Test
    public void childNotEventHandler() {
        buildModel(
                new ChildNoEventHandler(new StringHandler())
        );
        assertThat(
                getCallbackMethods(),
                hasItems(
                        getMethod(StringHandler::handleStringEvent),
                        getMethod(ChildNoEventHandler::parentUpdated)
                )
        );
    }

    @Test
    public void childIsEventHandler() {
        StringHandler stringHandler = new StringHandler();
        IntegerHandler intHandler = new IntegerHandler(stringHandler);
        IntegerHandlerNoOnEvent handlerNoOnEvent = new IntegerHandlerNoOnEvent(stringHandler);
        buildModel(intHandler, handlerNoOnEvent);
        assertThat(
                getCallbackMethods(),
                hasItems(
                        getMethod(StringHandler::handleStringEvent),
                        getMethod(IntegerHandler::handleIntEvent),
                        getMethod(IntegerHandlerNoOnEvent::handleIntEvent),
                        getMethod(IntegerHandler::parentUpdated)
                )
        );

        assertThat(
                eventProcessorModel.getOnEventDependenciesForNode(stringHandler),
                hasItems(intHandler)
        );
    }

    @Data
    public static class StringHandler {

        boolean notified = false;

        @EventHandler
        public boolean handleStringEvent(String s) {
            notified = true;
            return true;
        }
    }

    @Data
    public static class IntegerHandler {
        final StringHandler parent;
        boolean notified = false;
        boolean parentUpdated = false;

        @EventHandler
        public boolean handleIntEvent(Integer s) {
            notified = true;
            return true;
        }

        @OnEvent
        public void parentUpdated(){
            parentUpdated = true;
        }
    }

    @Data
    public static class IntegerHandlerNoOnEvent {
        final StringHandler parent;
        boolean notified = false;
        boolean parentUpdated = false;

        @EventHandler
        public boolean handleIntEvent(Integer s) {
            notified = true;
            return true;
        }

    }

    @Data
    public static class ChildNoEventHandler {
        final StringHandler parent;
        boolean notified = false;

        @OnEvent
        public boolean parentUpdated() {
            notified = true;
            return true;
        }

    }
}
