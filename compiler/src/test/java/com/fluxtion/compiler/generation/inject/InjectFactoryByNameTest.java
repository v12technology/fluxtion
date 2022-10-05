package com.fluxtion.compiler.generation.inject;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.google.auto.service.AutoService;
import lombok.Value;
import org.junit.Test;

import java.util.Map;

public class InjectFactoryByNameTest extends MultipleSepTargetInProcessTest {


    public InjectFactoryByNameTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void multipleFactoryTest(){
        writeOutputsToFile(true);
        sep(c -> {
            c.addNode(new ChildClass());
        });
        onEvent("test");
    }

    public static class ChildClass{

        @Inject(factoryName = "green")
        public MyUniqueData greenData;

        @Inject(factoryName = "blue")
        public MyUniqueData blueData;

        @OnEventHandler
        public void onEvent(String in){
            System.out.println("greenData -> " + greenData.key);
            System.out.println("blueData -> " + blueData.key);
        }
    }


    public static class MyUniqueData{
        public String key;

        public MyUniqueData(String key) {
            this.key = key;
        }

        public MyUniqueData() {
        }
    }

    @AutoService(NodeFactory.class)
    public static class MyUniqueDataGreenFactory implements NodeFactory<MyUniqueData>{
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
    public static class MyUniqueDataBlueFactory implements NodeFactory<MyUniqueData>{
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
