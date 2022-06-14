package com.fluxtion.compiler.builder.factory;

import com.fluxtion.compiler.RootNodeConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.Inject;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;

public class RootClassDrivenTest extends MultipleSepTargetInProcessTest {

    public RootClassDrivenTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testRootCompiled() {
        sep(new RootNodeConfig("root", MyHandler.class, new HashMap<>()));
        MyHandler myHandler = getField("root");
        onEvent(25);
        assertThat(myHandler.intValue, Matchers.is(25));
        assertNull(myHandler.stringValue);

        onEvent("TEST");
        assertThat(myHandler.intValue, is(25));
        assertThat(myHandler.stringValue, is("TEST"));
    }

    public static class MyHandler {

        @Inject
        public ParentHandler parent;
        int intValue;
        String stringValue;

        @OnEventHandler
        public boolean newString(String s) {
            stringValue = s;
            return true;
        }

        @OnParentUpdate
        public void parentUpdated(ParentHandler parent){
            intValue = parent.intValue;
        }

        @OnTrigger
        public boolean triggered(){
            return true;
        }
    }

    public static class ParentHandler{
        int intValue;
        @OnEventHandler
        public void newInteger(Integer s) {
            intValue = s;
        }

        @OnTrigger
        public boolean parentTriggered(){
            return true;
        }
    }
}
