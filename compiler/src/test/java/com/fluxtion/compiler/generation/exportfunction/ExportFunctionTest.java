package com.fluxtion.compiler.generation.exportfunction;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.ExportFunction;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.callback.ExportFunctionNode;
import com.fluxtion.runtime.callback.ExportFunctionTrigger;
import lombok.Getter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ExportFunctionTest extends MultipleSepTargetInProcessTest {

    public ExportFunctionTest(SepTestConfig compile) {
        super(compile);
    }


    @Test
    public void addTriggerNode() {
        sep(c -> {
            c.addNode(new ExportFunctionTrigger());
        });
    }

    @Test
    public void addExportingNodeX() {
        sep(c -> {
            c.addNode(new ExportingNode("iodauhf"));
        });
    }

    @Test
    public void exportTest() {
        sep(c -> {
            c.addNode(new Aggregator(
                    new ExportingNode("export1"),
                    new ExportingNode("export2")
            ), "aggregator");
            c.addInterfaceImplementation(MyExportedInterface.class);
        });
        MyExportedInterface myExportedInterface = sep.asInterface();
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

    @Test
    public void exportTestNoOverride() {
//        writeSourceFile = true;
        sep(c -> {
            c.addNode(new Aggregator2(
                    new ExportingNodeNoOverride("export1"),
                    new ExportingNodeNoOverride("export2")
            ), "aggregator");
            c.addInterfaceImplementation(MyExportedInterfaceNoOverride.class);
        });
        MyExportedInterfaceNoOverride myExportedInterface = sep.asInterface();
        Aggregator2 aggregator = getField("aggregator");
        myExportedInterface.myfunctionString("hello", 300);
        Assert.assertEquals(2, aggregator.updateCount);
        Assert.assertEquals(1, aggregator.triggerCount);
        Assert.assertEquals("ExportingNode{name='export1', result='myfunction s:hello y:300'}", aggregator.parent.toString());
        Assert.assertEquals("ExportingNode{name='export2', result='myfunction s:hello y:300'}", aggregator.parent2.toString());

        //
        myExportedInterface.myfunctionList(Arrays.asList("", "", ""), 33);
        Assert.assertEquals(4, aggregator.updateCount);
        Assert.assertEquals(2, aggregator.triggerCount);
        Assert.assertEquals("ExportingNode{name='export1', result='myfunction s:3 y:33'}", aggregator.parent.toString());
        Assert.assertEquals("ExportingNode{name='export2', result='myfunction s:3 y:33'}", aggregator.parent2.toString());

        myExportedInterface.myfunctionDouble(Arrays.asList(24.5), 344);
        Assert.assertEquals(4, aggregator.updateCount);
        Assert.assertEquals(2, aggregator.triggerCount);
        Assert.assertEquals("ExportingNode{name='export1', result='myfunction s:3 y:33'}", aggregator.parent.toString());
        Assert.assertEquals("ExportingNode{name='export2', result='myfunction s:3 y:33'}", aggregator.parent2.toString());
    }


    public static class ExportingNode extends ExportFunctionNode {

        private final String name;
        private String result;

        public ExportingNode(String name) {
            this.name = name;
        }


        @ExportFunction("updatedDetails")
        public boolean myfunctionString(String s, int y) {
            result = "updatedDetails s:" + s + " y:" + y;
            return true;
        }

        @ExportFunction("complexCallBack")
        public boolean myfunctionList(List<String> s, int y) {
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

    public static class ExportingNodeNoOverride extends ExportFunctionNode {

        private final String name;
        private String result;

        public ExportingNodeNoOverride(String name) {
            this.name = name;
        }


        @ExportFunction()
        public boolean myfunctionString(String s, int y) {
            result = "myfunction s:" + s + " y:" + y;
            return true;
        }

        @ExportFunction()
        public boolean myfunctionList(List<String> s, int y) {
            result = "myfunction s:" + s.size() + " y:" + y;
            return true;
        }

        @ExportFunction()
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

    public static class Aggregator2 {

        public final ExportingNodeNoOverride parent;
        public final ExportingNodeNoOverride parent2;
        public int updateCount;
        public int triggerCount;

        public Aggregator2(@AssignToField("parent") ExportingNodeNoOverride parent,
                           @AssignToField("parent2") ExportingNodeNoOverride parent2) {
            this.parent = parent;
            this.parent2 = parent2;
        }

        @OnParentUpdate
        public void parentUpdated(ExportingNodeNoOverride updatedNode) {
            updateCount++;
        }

        @OnTrigger
        public boolean triggered() {
            triggerCount++;
            return true;
        }
    }

    @Getter
    public static class AlwaysTrueExport extends ExportFunctionNode {
        private int result;

        @ExportFunction()
        public void addFunction(int x, int y) {
            result = x + y;
//            return true;
        }
    }
}
