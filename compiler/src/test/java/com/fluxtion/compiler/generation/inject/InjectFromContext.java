package com.fluxtion.compiler.generation.inject;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.node.ContextValueSupplier;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class InjectFromContext extends MultipleSepTargetInProcessTest {
    public InjectFromContext(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void injectIntoContext() {
        sep(c -> {
            c.addNode(new InjectDataFromContext("newKey"), "ctxtLookup");
        });
        InjectDataFromContext ctxtLookup = getField("ctxtLookup");
        Assert.assertNull(ctxtLookup.getContextValue());
        Map<Object, Object> ctxtMap = new HashMap<>();
        ctxtMap.put("newKey", "newValue");
        sep.setContextParameterMap(ctxtMap);
        Assert.assertEquals("newValue", ctxtLookup.getContextValue());
    }

    @Test(expected = RuntimeException.class)
    public void injectIntoContextFailFast() {
        sep(c -> {
            c.addNode(new FailFastInjectDataFromContext("newKey"), "ctxtLookup");
        });
        FailFastInjectDataFromContext ctxtLookup = getField("ctxtLookup");
        ctxtLookup.getContextValue();
    }


    public static class InjectDataFromContext {

        private final ContextValueSupplier<String> dateSupplier;

        public InjectDataFromContext(String key) {
            this(ContextValueSupplier.build(key));
        }

        public InjectDataFromContext(ContextValueSupplier<String> dateSupplier) {
            this.dateSupplier = dateSupplier;
        }

        @OnEventHandler
        public boolean update(String in) {
            return true;
        }

        public String getContextValue() {
            return dateSupplier.get();
        }
    }

    public static class FailFastInjectDataFromContext {

        private final ContextValueSupplier<String> dateSupplier;

        public FailFastInjectDataFromContext(String key) {
            this(ContextValueSupplier.buildFailFast(key));
        }

        public FailFastInjectDataFromContext(ContextValueSupplier<String> dateSupplier) {
            this.dateSupplier = dateSupplier;
        }

        @OnEventHandler
        public boolean update(String in) {
            return true;
        }

        public String getContextValue() {
            return dateSupplier.get();
        }
    }
}
