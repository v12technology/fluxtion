package com.fluxtion.compiler.generation.exportfunction;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.CompiledOnlySepTest;
import com.fluxtion.runtime.annotations.ExportFunction;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.callback.ExportFunctionNode;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ExportFunctionTest extends CompiledOnlySepTest {
    public ExportFunctionTest(SepTestConfig compile) {
        super(compile);
    }

    @Test
    public void exportTest() {
        writeSourceFile = true;
        MyExportedInterface myExportedInterface = (MyExportedInterface) sep(c -> {
            c.addNode(new Aggregator(
                    new ExportingNode("export1"),
                    new ExportingNode("export2")
            ), "aggregator");
            c.addInterfaceImplementation(MyExportedInterface.class);
        });
        Aggregator aggregator = getField("aggregator");
        myExportedInterface.updatedDetails("hello", 300);
        Assert.assertEquals(2, aggregator.updateCount);
        Assert.assertEquals(1, aggregator.triggerCount);
        Assert.assertEquals("ExportingNode{name='export1', result='updatedDetails s:hello y:300'}", aggregator.parent.toString());
        Assert.assertEquals("ExportingNode{name='export2', result='updatedDetails s:hello y:300'}", aggregator.parent2.toString());

        //
        myExportedInterface.complexCallBack(Arrays.asList("", "", ""), 33);
        Assert.assertEquals(4, aggregator.updateCount);
        Assert.assertEquals(2, aggregator.triggerCount);
        Assert.assertEquals("ExportingNode{name='export1', result='complexCallBack s:3 y:33'}", aggregator.parent.toString());
        Assert.assertEquals("ExportingNode{name='export2', result='complexCallBack s:3 y:33'}", aggregator.parent2.toString());

        myExportedInterface.complexCallBackDouble(Arrays.asList(24.5), 344);
        Assert.assertEquals(4, aggregator.updateCount);
        Assert.assertEquals(2, aggregator.triggerCount);
        Assert.assertEquals("ExportingNode{name='export1', result='complexCallBack s:3 y:33'}", aggregator.parent.toString());
        Assert.assertEquals("ExportingNode{name='export2', result='complexCallBack s:3 y:33'}", aggregator.parent2.toString());
    }


    public static class ExportingNode extends ExportFunctionNode {

        private final String name;
        private String result;

        public ExportingNode(String name) {
            this.name = name;
        }


        @ExportFunction("updatedDetails")
        public boolean myfunction(String s, int y) {
            result = "updatedDetails s:" + s + " y:" + y;
            return true;
        }

        @ExportFunction("complexCallBack")
        public boolean myfunction(List<String> s, int y) {
            result = "complexCallBack s:" + s.size() + " y:" + y;
            return true;
        }

        @ExportFunction("complexCallBackDouble")
        public boolean myfunctionDouble(List<Double> s, int y) {
            return false;
        }

        @Override
        public String toString() {
            return "ExportingNode{" +
                    "name='" + name + '\'' +
                    ", result='" + result + '\'' +
                    '}';
        }
    }


    public static class Aggregator {

        public final ExportingNode parent;
        public final ExportingNode parent2;
        public int updateCount;
        public int triggerCount;

        public Aggregator(@AssignToField("parent") ExportingNode parent,
                          @AssignToField("parent2") ExportingNode parent2) {
            this.parent = parent;
            this.parent2 = parent2;
        }

        @OnParentUpdate
        public void parentUpdated(ExportingNode updatedNode) {
            updateCount++;
        }

        @OnTrigger
        public boolean triggered() {
            triggerCount++;
            return true;
        }
    }
}
