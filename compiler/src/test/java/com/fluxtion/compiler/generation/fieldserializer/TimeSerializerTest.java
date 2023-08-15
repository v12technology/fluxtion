package com.fluxtion.compiler.generation.fieldserializer;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import lombok.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.*;
import java.util.Date;

public class TimeSerializerTest extends MultipleSepTargetInProcessTest {
    private Date date;
    private LocalDateTime localDateTime;
    private LocalTime localTime;
    private ZonedDateTime zonedDateTime;

    public TimeSerializerTest(SepTestConfig testConfig) {
        super(testConfig);
    }

    @Before
    public void setTimes() {
        localDateTime = LocalDateTime.now();
        localTime = LocalTime.now();
        zonedDateTime = ZonedDateTime.now();
        date = new Date(1258, 05, 06);
    }

    @Test
    public void testTimeSerializersConstructor() {
        sep(c -> c.addNode(buildTimeAsConstructor(), "timeNode"));
        Assert.assertEquals(buildTimeAsConstructor(), getField("timeNode"));
    }

    @Test
    public void testTimeSerializersProperty() {
        sep(c -> c.addNode(buildTimeAsProperty(), "timeNode"));
        Assert.assertEquals(buildTimeAsProperty(), getField("timeNode"));
    }

    private TimeHolderProperty buildTimeAsProperty() {
        return TimeHolderProperty.builder()
                .duration(Duration.ofSeconds(100))
                .instant(Instant.ofEpochSecond(400, 90))
                .localDate(LocalDate.of(1989, 10, 25))
                .localDateTime(localDateTime)
                .localTime(localTime)
                .period(Period.of(10, 3, 4))
                .zonedDateTime(zonedDateTime)
                .date(date)
                .build();
    }

    private TimeHolder buildTimeAsConstructor() {
        return TimeHolder.builder()
                .duration(Duration.ofSeconds(100))
                .instant(Instant.ofEpochSecond(400, 90))
                .localDate(LocalDate.of(1989, 10, 25))
                .localDateTime(localDateTime)
                .localTime(localTime)
                .period(Period.of(10, 3, 4))
                .zonedDateTime(zonedDateTime)
                .date(date)
                .build();
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
