package com.fluxtion.compiler.builder.factory;

import com.fluxtion.compiler.RootNodeConfig;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.ExcludeNode;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.node.NamedNode;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;

public class RootNodeWithoutFactoryTest extends MultipleSepTargetInProcessTest {

    public RootNodeWithoutFactoryTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testRootCompiled() {
        sep(new RootNodeConfig("root", MyHandler.class, new HashMap<>(), null));
        MyHandler myHandler = getField("root");
        onEvent(25);
        assertThat(myHandler.intValue, Matchers.is(25));
        assertNull(myHandler.stringValue);

        onEvent("TEST");
        assertThat(myHandler.intValue, is(25));
        assertThat(myHandler.stringValue, is("TEST"));
    }

    @Test
    public void noRootNode() {
        sep(new RootNodeConfig("root", ExcludeMeNode.class, new HashMap<>(), null));
        onEvent("test");
        boolean failed = false;
        try {
            getField("excluded");
        } catch (Exception e) {
            failed = true;
        }
        if (!failed)
            throw new RuntimeException("Lookup for excluded node should fail");
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
        public void parentUpdated(ParentHandler parent) {
            intValue = parent.intValue;
        }

        @OnTrigger
        public boolean triggered() {
            return true;
        }
    }

    public static class ParentHandler implements NamedNode {
        int intValue;

        @OnEventHandler
        public boolean newInteger(Integer s) {
            intValue = s;
            return true;
        }

        @OnTrigger
        public boolean parentTriggered() {
            return true;
        }

        @Override
        public String getName() {
            return "parentHandler";
        }
    }

    @ExcludeNode
    public static class ExcludeMeNode implements NamedNode {
        @NoTriggerReference
        public ParentHandler parentHandler = new ParentHandler();

        @Override
        public String getName() {
            return "excluded";
        }
    }
}
