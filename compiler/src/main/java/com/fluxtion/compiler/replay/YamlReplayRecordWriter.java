package com.fluxtion.compiler.replay;

import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.time.Clock;
import lombok.SneakyThrows;
import org.yaml.snakeyaml.Yaml;

import java.util.Set;

public class YamlReplayRecordWriter implements Auditor {

    @Inject
    private final Clock clock;
    private Set<Class<?>> classWhiteList;
    private Set<Class<?>> classBlackList;
    private final transient Yaml yaml = new Yaml();
    private final transient ReplayRecord replayRecord = new ReplayRecord();
    private final transient StringBuilder audiLogAsString = new StringBuilder();

    public YamlReplayRecordWriter(Clock clock) {
        this.clock = clock;
    }

    public static YamlReplayRecordWriter build() {
        return new YamlReplayRecordWriter(null);
    }

    @Override
    public void nodeRegistered(Object node, String nodeName) {
    }

    @Override
    public void eventReceived(Event event) {
        this.eventReceived((Object) event);
    }

    @SneakyThrows
    @Override
    public void eventReceived(Object event) {
//        if (event instanceof PnlUpdate) {
//            replayRecord.setEvent(event);
//            replayRecord.setWallClockTime(clock.getWallClockTime());
//            audiLogAsString.append("---\n" + yaml.dump(replayRecord));
//        }
    }

    public StringBuilder getAudiLogAsString() {
        return audiLogAsString;
    }

}
