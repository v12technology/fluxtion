package com.fluxtion.compiler.generation.customfieldserializer;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
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
        sep(c -> {
            c.addNode(FormatHolder.builder()
                    .dateFormat(SimpleDateFormat.getDateTimeInstance())
                    .simpleDateFormat(new SimpleDateFormat())
                    .decimalFormat(new DecimalFormat())
                    .numberFormat(NumberFormat.getCurrencyInstance())
                    .build());
        });
    }

    @Test
    public void testIpSerializersProperty() {
        sep(c -> {
            c.addNode(FormatHolderProperty.builder()
                    .dateFormat(SimpleDateFormat.getDateTimeInstance())
                    .simpleDateFormat(new SimpleDateFormat())
                    .decimalFormat(new DecimalFormat())
                    .numberFormat(NumberFormat.getCurrencyInstance())
                    .build());
        });
    }


    @Builder
    @Value
    public static class FormatHolder {

        SimpleDateFormat simpleDateFormat;
        DateFormat dateFormat;
        DecimalFormat decimalFormat;
        NumberFormat numberFormat;

        public FormatHolder(
                @AssignToField("simpleDateFormat") SimpleDateFormat simpleDateFormat,
                @AssignToField("dateFormat") DateFormat dateFormat,
                @AssignToField("decimalFormat") DecimalFormat decimalFormat,
                NumberFormat numberFormat) {
            this.simpleDateFormat = simpleDateFormat;
            this.dateFormat = dateFormat;
            this.decimalFormat = decimalFormat;
            this.numberFormat = numberFormat;
        }
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
