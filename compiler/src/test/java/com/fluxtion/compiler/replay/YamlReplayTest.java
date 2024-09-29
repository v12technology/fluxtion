package com.fluxtion.compiler.replay;

import com.fluxtion.compiler.EventProcessorConfig;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

public class YamlReplayTest extends MultipleSepTargetInProcessTest {
    private static final String replayEventLog = "---\n" +
                                                 "!!com.fluxtion.runtime.event.ReplayRecord\n" +
                                                 "event: !!com.fluxtion.compiler.replay.PnlUpdate {amount: 200, bookName: book1}\n" +
                                                 "wallClockTime: 500\n" +
                                                 "---\n" +
                                                 "!!com.fluxtion.runtime.event.ReplayRecord\n" +
                                                 "event: !!com.fluxtion.compiler.replay.PnlUpdate {amount: 25, bookName: book1}\n" +
                                                 "wallClockTime: 650\n" +
                                                 "---\n" +
                                                 "!!com.fluxtion.runtime.event.ReplayRecord\n" +
                                                 "event: !!com.fluxtion.compiler.replay.PnlUpdate {amount: 3500, bookName: bookAAA}\n" +
                                                 "wallClockTime: 700\n" +
                                                 "---\n" +
                                                 "!!com.fluxtion.runtime.event.ReplayRecord\n" +
                                                 "event: !!com.fluxtion.compiler.replay.PnlUpdate {amount: 200, bookName: unknown_BOOK}\n" +
                                                 "wallClockTime: 940\n";

    private static String expectedFullRun = "time,globalPnl\n" +
                                            "500,200\n" +
                                            "650,25\n" +
                                            "700,3525\n";

    public YamlReplayTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void replayValidateNodeOutput() {
        sep(this::buildGraph);
        StringWriter stringWriter = new StringWriter();
        sep.<String>addSink(GlobalPnl.GLOBAL_PNL_SINK_NAME, stringWriter::append);
        YamlReplayRunner.newSession(new StringReader(replayEventLog), eventProcessor())
                .callStart()
                .runReplay();
        Assert.assertEquals(expectedFullRun, stringWriter.toString());
    }

    @Test
    public void replayValidateNodeOutput_WithFromTime() {
        sep(this::buildGraph);
        StringWriter stringWriter = new StringWriter();
        sep.<String>addSink(GlobalPnl.GLOBAL_PNL_SINK_NAME, stringWriter::append);
        YamlReplayRunner.newSession(new StringReader(replayEventLog), eventProcessor())
                .afterTime(600)
                .callStart()
                .runReplay();

        Assert.assertEquals("time,globalPnl\n" +
                            "650,25\n" +
                            "700,3525\n", stringWriter.toString());
    }

    @Test
    public void replayValidateNodeOutput_WithStopime() {
        sep(this::buildGraph);
        StringWriter stringWriter = new StringWriter();
        sep.<String>addSink(GlobalPnl.GLOBAL_PNL_SINK_NAME, stringWriter::append);
        YamlReplayRunner.newSession(new StringReader(replayEventLog), eventProcessor())
                .beforeTime(690)
                .callStart()
                .runReplay();

        Assert.assertEquals("time,globalPnl\n" +
                            "500,200\n" +
                            "650,25\n", stringWriter.toString());
    }

    @Test
    public void replayValidateNodeOutput_BetweenTimes() {
        sep(this::buildGraph);
        StringWriter stringWriter = new StringWriter();
        sep.<String>addSink(GlobalPnl.GLOBAL_PNL_SINK_NAME, stringWriter::append);
        YamlReplayRunner.newSession(new StringReader(replayEventLog), eventProcessor())
                .betweenTimes(600, 680)
                .callStart()
                .runReplay();

        Assert.assertEquals("time,globalPnl\n" +
                            "650,25\n", stringWriter.toString());
    }


    @Test
    public void replayValidateRepeatableYamlLog() throws NoSuchFieldException, IllegalAccessException {
        sep(this::buildGraph);
        //record audit log
        StringWriter stringWriter = new StringWriter();
        YamlReplayRecordWriter yamlReplayRecordWriter = sep.getAuditorById(YamlReplayRecordWriter.DEFAULT_NAME);
        yamlReplayRecordWriter.setTargetWriter(stringWriter);
        //run session
        YamlReplayRunner.newSession(new StringReader(replayEventLog), eventProcessor())
                .callStart()
                .runReplay();

        Assert.assertEquals(replayEventLog, stringWriter.toString());
    }


    private void buildGraph(EventProcessorConfig c) {
        c.addNode(
                new GlobalPnl(Arrays.asList(
                        new BookPnl("book1"),
                        new BookPnl("bookAAA"),
                        new BookPnl("book_XYZ")
                ))
        );
        //Inject an auditor will see events before any node
        c.addAuditor(
                new YamlReplayRecordWriter().classWhiteList(PnlUpdate.class),
                YamlReplayRecordWriter.DEFAULT_NAME);
    }
}
