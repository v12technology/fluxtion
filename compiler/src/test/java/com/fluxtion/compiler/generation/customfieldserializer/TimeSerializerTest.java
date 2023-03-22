package com.fluxtion.compiler.generation.customfieldserializer;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.Date;

public class TimeSerializerTest extends MultipleSepTargetInProcessTest {
    public TimeSerializerTest(SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void testTimeSerializersConstructor() {
        sep(c -> {
            c.addNode(
                    TimeHolder.builder()
                            .duration(Duration.ofSeconds(100))
                            .instant(Instant.ofEpochSecond(400, 90))
                            .localDate(LocalDate.of(1989, 10, 25))
                            .localDateTime(LocalDateTime.now())
                            .localTime(LocalTime.now())
                            .period(Period.of(10, 3, 4))
                            .zonedDateTime(ZonedDateTime.now())
                            .date(new Date())
                            .build()
            );
        });
    }

    @Test
    public void testTimeSerializersProperty() {
        sep(c -> {
            c.addNode(
                    TimeHolderProperty.builder()
                            .duration(Duration.ofSeconds(100))
                            .instant(Instant.ofEpochSecond(400, 90))
                            .localDate(LocalDate.of(1989, 10, 25))
                            .localDateTime(LocalDateTime.now())
                            .localTime(LocalTime.now())
                            .period(Period.of(10, 3, 4))
                            .zonedDateTime(ZonedDateTime.now())
                            .date(new Date())
                            .build()
            );
        });
    }

    @Builder
    @AllArgsConstructor
    @Value
    public static class TimeHolder {
        Instant instant;
        Duration duration;
        LocalDate localDate;
        LocalTime localTime;
        LocalDateTime localDateTime;
        ZonedDateTime zonedDateTime;
        Period period;
        Date date;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class TimeHolderProperty {
        Instant instant;
        Duration duration;
        LocalDate localDate;
        LocalTime localTime;
        LocalDateTime localDateTime;
        ZonedDateTime zonedDateTime;
        Period period;
        Date date;
    }
}
