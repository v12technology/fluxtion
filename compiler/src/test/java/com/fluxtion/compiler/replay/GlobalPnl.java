package com.fluxtion.compiler.replay;

import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.Start;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.stream.SinkPublisher;
import com.fluxtion.runtime.time.Clock;

import java.util.ArrayList;
import java.util.List;

public class GlobalPnl {
    public static final String GLOBAL_PNL_SINK_NAME = "globalPnl";
    public SinkPublisher<String> publisher = new SinkPublisher<>(GLOBAL_PNL_SINK_NAME);
    @Inject
    public Clock clock;
    private final List<BookPnl> bookPnlList;

    public GlobalPnl(List<BookPnl> bookPnlList) {
        this.bookPnlList = new ArrayList<>();
        this.bookPnlList.addAll(bookPnlList);
    }

    @Start
    public void start() {
        publisher.publish("time,globalPnl\n");
    }

    @OnTrigger
    public boolean calculate() {
        publisher.publish(
                clock.getProcessTime() + "," + bookPnlList.stream().mapToInt(BookPnl::getPnl).sum() + "\n");
        return true;
    }
}
