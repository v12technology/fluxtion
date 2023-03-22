package com.fluxtion.compiler.generation.serialiser;

import java.time.Duration;

public class TimeSerializer {

    public static String durationToSource(FieldContext fieldContext) {
        fieldContext.getImportList().add(Duration.class);
        Duration duration = (Duration) fieldContext.getInstanceToMap();
        return "Duration.ofSeconds(%N)".replace("%N", duration.getSeconds() + "," + duration.getNano());
    }
}
