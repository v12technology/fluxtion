package com.fluxtion.compiler.generation.fieldserializer;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import lombok.*;
import org.junit.Assert;
import org.junit.Test;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class FormatSerializerTest extends MultipleSepTargetInProcessTest {
    public FormatSerializerTest(SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void testIpSerializersConstructor() {
        FormatHolder formatHolder = FormatHolder.builder()
                .dateFormat(SimpleDateFormat.getDateTimeInstance())
                .simpleDateFormat(new SimpleDateFormat())
                .decimalFormat(new DecimalFormat())
                .numberFormat(NumberFormat.getCurrencyInstance())
                .build();
        sep(c -> c.addNode(formatHolder, "formatHolder"));
        Assert.assertEquals(formatHolder, getField("formatHolder"));
    }

    @Test
    public void testIpSerializersProperty() {
        FormatHolderProperty formatHolder = FormatHolderProperty.builder()
                .dateFormat(SimpleDateFormat.getDateTimeInstance())
                .simpleDateFormat(new SimpleDateFormat())
                .decimalFormat(new DecimalFormat())
                .numberFormat(NumberFormat.getCurrencyInstance())
                .build();
        sep(c -> c.addNode(formatHolder, "formatHolder"));
        Assert.assertEquals(formatHolder, getField("formatHolder"));
    }

    @Builder
    @Value
    @AllArgsConstructor
    public static class FormatHolder {

        @AssignToField("simpleDateFormat")
        SimpleDateFormat simpleDateFormat;
        @AssignToField("dateFormat")
        DateFormat dateFormat;
        @AssignToField("decimalFormat")
        DecimalFormat decimalFormat;
        @AssignToField("numberFormat")
        NumberFormat numberFormat;

    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class FormatHolderProperty {
        private SimpleDateFormat simpleDateFormat;
        private DateFormat dateFormat;
        private DecimalFormat decimalFormat;
        private NumberFormat numberFormat;
    }

}
