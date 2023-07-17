package com.fluxtion.compiler.generation.customfieldserializer;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.callback.InstanceCallbackEvent;
import com.fluxtion.runtime.node.SingleNamedNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.junit.Test;

public class MetaSerializerTest extends MultipleSepTargetInProcessTest {
    public MetaSerializerTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void serializeFieldTest() {
        writeSourceFile = true;
        sep(new ClassFieldHolder(String.class));
    }

    @Test
    public void serializeConstructorTest() {
        writeSourceFile = true;
        sep(new ClassFieldHolder(String.class));
    }

    @Test
    public void serializeInnerClassConstructorTest() {
        writeSourceFile = true;
        sep(new ClassFieldHolder(MyInnerClass.class));
    }

    @Test
    public void serializeField_WithSingleNamedNodeTest() {
        writeSourceFile = true;
        InstanceCallbackEvent.reset();
        sep(new CbSample("test"));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClassFieldHolder {
        private Class<?> myClass;
    }

    @Value
    public static class ClassConstructorHolder {
        private Class<?> myClass;
    }

    public static class MyInnerClass {
    }

    public static class CbSample extends SingleNamedNode {

        private Class<?> cbClass;

        public CbSample(String name) {
            super(name);
            cbClass = InstanceCallbackEvent.cbClassList.remove(0);
        }


        public Class<?> getCbClass() {
            return cbClass;
        }

        public void setCbClass(Class<?> cbClass) {
            this.cbClass = cbClass;
        }
    }
}
