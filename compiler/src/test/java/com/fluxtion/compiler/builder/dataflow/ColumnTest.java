package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.dataflow.column.Column;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.Test;

public class ColumnTest extends MultipleSepTargetInProcessTest {
    public ColumnTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void buildColumn() {
        writeSourceFile = true;
        sep(c -> {
            DataFlow.buildColumn(Data.class).map(Data::getName).console("column:{}");
        });
        onEvent(new Data(12, "Steve H"));
        onEvent(new Data(65, "Steve G"));
        onEvent(new Data(7, "Bob"));
    }

    @Test
    public void buildColumnFromProperty() {
        writeSourceFile = true;
        sep(c -> {
            DataFlow.buildColumn(Data::getName).console("column:{}");
        });
        onEvent(new Data(12, "Steve H"));
        onEvent(new Data(65, "Steve G"));
        onEvent(new Data(7, "Bob"));
    }

    @Test
    public void buildColumnFromPropertyAndMap() {
        writeSourceFile = true;
        sep(c -> {
            DataFlow.buildColumn(Data::getAge)
                    .map(ColumnTest::multiply10x)
                    .console("column:{}");
        });
        onEvent(new Data(12, "Steve H"));
        onEvent(new Data(65, "Steve G"));
        onEvent(new Data(7, "Bob"));
    }

    @Test
    public void buildColumnPublishToSink() {
        writeSourceFile = true;
        sep(c -> {
            DataFlow.buildColumn(Data::getName).sink("xxx");
        });
        addSink("xxx", (Column<Data> o) -> {
            System.out.println("sink -> " + o.values());
        });
        onEvent(new Data(12, "Steve H"));
        onEvent(new Data(65, "Steve G"));
        onEvent(new Data(7, "Bob"));
    }

    @Test
    public void buildColumnFilterPublishToSink() {
        writeSourceFile = true;
        sep(c -> {
            DataFlow.buildColumn(Data::getName).filter(ColumnTest::startsWithSteve).sink("xxx");
        });
        addSink("xxx", (Column<Data> o) -> {
            System.out.println("sink -> " + o.values());
        });
        onEvent(new Data(12, "Steve H"));
        onEvent(new Data(7, "Bob"));
        onEvent(new Data(65, "Steve G"));
    }

    public static boolean startsWithSteve(String in) {
        return in.startsWith("Steve");
    }

    public static int multiply10x(int in) {
        return in * 10;
    }

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {
        private int age;
        private String name;
    }

}
