package com.fluxtion.compiler.generation.inject;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.google.auto.service.AutoService;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class InjectFactoryByNameTest extends MultipleSepTargetInProcessTest {


    public InjectFactoryByNameTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void multipleNamedFactoriesOfSameTypeTest() {
        sep(c -> {
            c.addNode(new ChildClass(), "child");
        });
        Assert.assertFalse(((ChildClass) getField("child")).matched);
        onEvent("test");
        Assert.assertTrue(((ChildClass) getField("child")).matched);
    }

    public static class ChildClass {
        public boolean matched = false;

        @Inject(factoryName = "green")
        public MyUniqueData greenData;

        @Inject(factoryName = "blue")
        public MyUniqueData blueData;

        @OnEventHandler
        public void onEvent(String in) {
            matched = greenData.key.equals("green") && blueData.key.equals("blue");
        }
    }


    public static class MyUniqueData {
        public String key;

        public MyUniqueData(String key) {
            this.key = key;
        }

        public MyUniqueData() {
        }
    }

    @AutoService(NodeFactory.class)
    public static class MyUniqueDataGreenFactory implements NodeFactory<MyUniqueData> {
        @Override
        public MyUniqueData createNode(Map<String, Object> config, NodeRegistry registry) {
            return new MyUniqueData("green");
        }

        @Override
        public String factoryName() {
            return "green";
        }
    }

    @AutoService(NodeFactory.class)
    public static class MyUniqueDataBlueFactory implements NodeFactory<MyUniqueData> {
        @Override
        public MyUniqueData createNode(Map<String, Object> config, NodeRegistry registry) {
            return new MyUniqueData("blue");
        }

        @Override
        public String factoryName() {
            return "blue";
        }
    }
}
