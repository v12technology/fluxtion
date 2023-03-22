package com.fluxtion.compiler.generation.serialiser;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public interface TimeSerializer {

    static String durationToSource(FieldContext<Duration> fieldContext) {
        fieldContext.getImportList().add(Duration.class);
        Duration duration = fieldContext.getInstanceToMap();
        return "Duration.ofSeconds(%N)".replace("%N", duration.getSeconds() + "," + duration.getNano());
    }

    static String instantToSource(FieldContext<Instant> fieldContext) {
        fieldContext.getImportList().add(Instant.class);
        Instant instant = fieldContext.getInstanceToMap();
        return "Instant.ofEpochSecond(%N)".replace("%N", instant.getEpochSecond() + "," + instant.getNano());
    }

    static String localTimeToSource(FieldContext<LocalTime> fieldContext) {
        fieldContext.getImportList().add(LocalTime.class);
        LocalTime localTime = fieldContext.getInstanceToMap();
        return "LocalTime.of(%N)".replace(
                "%N",
                localTime.getHour() + "," + localTime.getMinute() + "," + localTime.getSecond() + "," + localTime.getNano()
        );
    }

    static String localDateToSource(FieldContext<LocalDate> fieldContext) {
        fieldContext.getImportList().add(LocalDate.class);
        LocalDate localDate = fieldContext.getInstanceToMap();
        return "LocalDate.of(%N)".replace(
                "%N", localDate.getYear() + "," + localDate.getMonthValue() + "," + localDate.getDayOfMonth());
    }

    static String localDateTimeToSource(FieldContext<LocalDateTime> fieldContext) {
        fieldContext.getImportList().add(LocalDateTime.class);
        LocalDateTime localDateTime = fieldContext.getInstanceToMap();
        return "LocalDateTime.of(%N)".replace(
                "%N",
                localDateTime.getYear() + "," + localDateTime.getMonthValue() + "," + localDateTime.getDayOfMonth() + "," +
                        localDateTime.getHour() + "," + localDateTime.getMinute() + "," + localDateTime.getSecond() + "," + localDateTime.getNano()
        );
    }

    static String periodToSource(FieldContext<Period> fieldContext) {
        fieldContext.getImportList().add(Period.class);
        Period period = fieldContext.getInstanceToMap();
        return "Period.of(%N)".replace(
                "%N",
                period.getYears() + "," + period.getMonths() + "," + period.getDays()
        );
    }

    static String zoneIdToSource(FieldContext<ZoneId> fieldContext) {
        fieldContext.getImportList().add(ZoneId.class);
        ZoneId zoneId = fieldContext.getInstanceToMap();
        return "ZoneId.of(%N)".replace(
                "%N",
                zoneId.getId()
        );
    }

    static String zoneDateTimeToSource(FieldContext<ZonedDateTime> fieldContext) {
        fieldContext.getImportList().add(ZonedDateTime.class);
        fieldContext.getImportList().add(ZoneId.class);
        ZonedDateTime zoneDateTime = fieldContext.getInstanceToMap();

        return "ZonedDateTime.of(%N)".replace(
                "%N",
                zoneDateTime.getYear() + "," + zoneDateTime.getMonthValue() + "," + zoneDateTime.getDayOfMonth() + "," +
                        zoneDateTime.getHour() + "," + zoneDateTime.getMinute() + "," + zoneDateTime.getSecond() + "," + zoneDateTime.getNano()
                        + ", ZoneId.of(\"" + zoneDateTime.getZone().getId() + "\")"
        );
    }

    static String dateToSource(FieldContext<Date> fieldContext) {
        fieldContext.getImportList().add(Date.class);
        Date period = fieldContext.getInstanceToMap();
        return "new Date(" + period.getTime() + "L)";
    }
}
