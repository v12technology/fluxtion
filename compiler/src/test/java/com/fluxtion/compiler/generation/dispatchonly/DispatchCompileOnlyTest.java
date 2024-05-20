package com.fluxtion.compiler.generation.dispatchonly;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.DispatchOnlySepTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.SepNode;
import lombok.Getter;
import lombok.Setter;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DispatchCompileOnlyTest extends DispatchOnlySepTest {
    public DispatchCompileOnlyTest(CompiledAndInterpretedSepTest.SepTestConfig compile) {
        super(compile);
    }

    @Test
    public void noFieldsSet() {
        //user references to manipulate
        PropertyHolder propertyHolder = new PropertyHolder();
        HashMap<String, String> lookupMap = new HashMap<>();
        propertyHolder.setMap(lookupMap);

        //build
        sep(propertyHolder);
        onEvent("test");
        Assert.assertEquals("no value", propertyHolder.lookup);

        //update values outside of event processor
        lookupMap.put("test", "success");
        onEvent("test");
        Assert.assertEquals("success", propertyHolder.lookup);
    }

    //    @Test
    public void dispatchOnlyTest() {
        MyStringHandler myStringHandler = new MyStringHandler();
        myStringHandler.dataHolder = new DataHolder();
        myStringHandler.intHandler = new MyIntegerHandler();
        myStringHandler.intHandler.x = 100;
        sep(myStringHandler);

        onEvent("TEST");
        Assert.assertEquals("TEST", myStringHandler.in);
        Assert.assertEquals(100, myStringHandler.intHandler.x);
        Assert.assertEquals(0, myStringHandler.intHandler.y);

        onEvent(200);
        Assert.assertEquals(200, myStringHandler.intHandler.y);
    }

    public static class MyStringHandler {
        public DataHolder dataHolder;
        public MyIntegerHandler intHandler;
        public String in;

        @OnEventHandler
        public boolean onString(String in) {
            System.out.println("received - " + in);
            this.in = in;
            return true;
        }
    }

    public static class MyIntegerHandler {

        public int x;
        public int y;

        @OnEventHandler
        public boolean onInteger(Integer in) {
            y = in;
            return true;
        }
    }

    @SepNode
    public static class DataHolder {


    }

    public static class PropertyHolder {
        @Getter
        private int count = 30;
        @Getter
        @Setter
        private Map<String, String> map;
        String lookup;

        @OnEventHandler
        public boolean onString(String in) {
            lookup = map.getOrDefault(in, "no value");
            return true;
        }

        public void setCount(int count) {
            throw new RuntimeException("should not be set");
        }
    }
}
