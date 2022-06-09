package com.fluxtion.compiler;

import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.Inject;
import org.junit.Test;

public class DataDrivenTest {

    @Test
    public void testData() throws ClassNotFoundException {
        Class.forName(MyHandler.class.getName());
        EventProcessor eventProcessor = Fluxtion.compile(MyHandler.class);
        eventProcessor.init();
        eventProcessor.onEvent("TEST");
        eventProcessor.onEvent(25);
    }

    public static class MyHandler {

        @Inject
        public ParentHandler parent;

        @OnEventHandler
        public boolean newString(String s) {
            System.out.println("received:" + s);
            return true;
        }

        @OnParentUpdate
        public void parentUpdated(ParentHandler parent){
            System.out.println("parent updated notification");
        }

        @OnTrigger
        public boolean triggered(){
            System.out.println("triggered");
            return true;
        }
    }

    public static class ParentHandler{
        @OnEventHandler
        public void newInteger(Integer s) {
            System.out.println("Integer received:" + s);
//            return true;
        }

        @OnTrigger
        public boolean parentTriggered(){
            System.out.println("ParentHandler::parentTriggered");
            return true;
        }
    }
}
