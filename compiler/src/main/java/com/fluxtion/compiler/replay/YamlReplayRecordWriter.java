package com.fluxtion.compiler.replay;

import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.time.Clock;
import lombok.SneakyThrows;
import org.yaml.snakeyaml.Yaml;

import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Added as an {@link Auditor} to an EventProcessor will trap all events and translate to yaml and push the result to
 * a registered reader.
 * <p>
 * Sample code to add the auditor:
 * <pre>
 * c.addNode(
 *         new GlobalPnl(Arrays.asList(
 *                 new BookPnl("book1"),
 *                 new BookPnl("bookAAA"),
 *                 new BookPnl("book_XYZ")
 *         ))
 * );
 * //Inject an auditor will see events before any node
 * c.addAuditor(
 *         new YamlReplayRecordWriter().classWhiteList(PnlUpdate.class),
 *         YamlReplayRecordWriter.DEFAULT_NAME);
 * </pre>
 * <p>
 * Once the graph is built a writer must be registered with the YamlReplayRecordWriter otherwise all the records will
 * be sent to a null writer.
 * <p>
 * Add a target for the yaml serialised {@link ReplayRecord} :
 *
 * <pre>
 * EventProcessor sep = new MyEventProcessor();;
 * //record audit log
 * StringWriter stringWriter = new StringWriter();
 * var yamlReplayRecordWriter = sep.getAuditorById(YamlReplayRecordWriter.DEFAULT_NAME);
 * yamlReplayRecordWriter.setTargetWriter(stringWriter);
 * </pre>
 * <p>
 * Filtering:
 * <p>
 * The YamlReplayRecordWriter supports both white list filter and black list filters. Where the filters are the class
 * type accepted or rejected.
 */
public class YamlReplayRecordWriter implements Auditor {

    public static final String DEFAULT_NAME = "yamlReplayRecordWriter";
    @Inject
    private final Clock clock;
    private final transient Yaml yaml = new Yaml();
    private final transient ReplayRecord replayRecord = new ReplayRecord();
    private transient Writer targetWriter = NullWriter.NULL_WRITER;
    private Set<Class<?>> classWhiteList = new HashSet<>();
    private Set<Class<?>> classBlackList = new HashSet<>();

    public YamlReplayRecordWriter(Clock clock) {
        this.clock = clock;
    }

    public YamlReplayRecordWriter() {
        this(null);
    }

    public Set<Class<?>> getClassWhiteList() {
        return classWhiteList;
    }

    public void setClassWhiteList(Set<Class<?>> classWhiteList) {
        this.classWhiteList.clear();
        this.classWhiteList.addAll(classWhiteList);
    }

    public YamlReplayRecordWriter classWhiteList(Class<?>... classes) {
        setClassWhiteList(new HashSet<>(Arrays.asList(classes)));
        return this;
    }

    public Set<Class<?>> getClassBlackList() {
        return classBlackList;
    }

    public void setClassBlackList(Set<Class<?>> classBlackList) {
        this.classBlackList.clear();
        this.classBlackList.addAll(classBlackList);
    }

    public YamlReplayRecordWriter classBlackList(Class<?>... classes) {
        setClassBlackList(new HashSet<>(Arrays.asList(classes)));
        return this;
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
        boolean whitePass = classWhiteList.isEmpty() || classWhiteList.contains(event.getClass());
        boolean blackPass = !classBlackList.contains(event.getClass());
        if (whitePass & blackPass & targetWriter != NullWriter.NULL_WRITER) {
            replayRecord.setEvent(event);
            replayRecord.setWallClockTime(clock.getWallClockTime());
            targetWriter.append("---\n");
            yaml.dump(replayRecord, targetWriter);
        }
    }

    public Writer getTargetWriter() {
        return targetWriter;
    }

    public void setTargetWriter(Writer targetWriter) {
        this.targetWriter = targetWriter;
    }
}
