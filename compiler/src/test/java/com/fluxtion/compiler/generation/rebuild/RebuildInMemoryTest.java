package com.fluxtion.compiler.generation.rebuild;

import com.fluxtion.runtim.annotations.EventHandler;
import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.event.Signal;
import com.fluxtion.compiler.generation.util.InMemoryOnlySepTest;
import lombok.Data;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RebuildInMemoryTest extends InMemoryOnlySepTest {
    public RebuildInMemoryTest(boolean compiledSep) {
        super(compiledSep);
    }

    final static String liveFeed = "liveFeed";
    final static String dropCopyFeed = "dropCopyFeed";

    @Test
    public void rebuildScalar() {
        Reconciler reconciler = new Reconciler();
        reconciler.liveHandler = new BookHandler(liveFeed);
        sep(c -> {
            c.addNode(reconciler, "reconciler");
//            c.addEventAudit(EventLogControlEvent.LogLevel.INFO);
        });
        onEvent(new Signal<Integer>(liveFeed, 100));
        assertThat(reconciler.getReconcileDifference(), is(100));
        onEvent(new Signal<Integer>(dropCopyFeed, 100));
        assertThat(reconciler.getReconcileDifference(), is(100));
        //add new reference, nothing should happen until a rebuild
        reconciler.dropCopyFeed = new BookHandler(dropCopyFeed);
        onEvent(new Signal<>(dropCopyFeed, 100));
        assertThat(reconciler.getReconcileDifference(), is(100));
        //rebuild
        sep(c -> {
            c.addNode(reconciler, "reconciler");
//            c.addEventAudit(EventLogControlEvent.LogLevel.INFO);
        });
        onEvent(new Signal<Integer>(liveFeed, 100));
        onEvent(new Signal<Integer>(dropCopyFeed, 200));
        assertThat(reconciler.getReconcileDifference(), is(0));

        // now add a new root node
        MaxDelta maxDelta = new MaxDelta(reconciler);
        sep(c -> {
            c.addNode(maxDelta, "maxDelta");
//            c.addEventAudit(EventLogControlEvent.LogLevel.INFO);
        });
        onEvent(new Signal<Integer>(dropCopyFeed, 200));
        onEvent(new Signal<Integer>(dropCopyFeed, 200));
        onEvent(new Signal<Integer>(dropCopyFeed, 200));
        onEvent(new Signal<Integer>(dropCopyFeed, 200));
        onEvent(new Signal<Integer>(liveFeed, 100));
        onEvent(new Signal<Integer>(liveFeed, 100));
        onEvent(new Signal<Integer>(liveFeed, 100_000));
    }

    @Test
    public void rebuildVector() {
        SummingVector bookSum = new SummingVector();
        BookHandler liveHandler = new BookHandler(liveFeed);
        BookHandler dropCopyHandler = new BookHandler(dropCopyFeed);
        bookSum.getMonitoredBooks().add(liveHandler);
        sep(c -> {
            c.addNode(bookSum);
        });
        onEvent(new Signal<Integer>(dropCopyFeed, 200));
        onEvent(new Signal<Integer>(liveFeed, 100));
        onEvent(new Signal<Integer>(liveFeed, 100));
        //add a new source and rebuild
        bookSum.getMonitoredBooks().add(dropCopyHandler);
        sep(c -> {
            c.addNode(bookSum);
//            c.addEventAudit(EventLogControlEvent.LogLevel.INFO);
        });
        onEvent(new Signal<Integer>(dropCopyFeed, 50));
        onEvent(new Signal<Integer>(liveFeed, 1000));
        onEvent(new Signal<Integer>(liveFeed, 100));
    }

    @Data
    public static class BookHandler {
        final String bookName;
        int sum;

        @EventHandler(filterVariable = "bookName")
        public void addPosition(Signal<Integer> delta) {
            sum += delta.getValue();
        }
    }

    @Data
    public static class Reconciler {
        BookHandler liveHandler;
        BookHandler dropCopyFeed;
        int reconcileDifference;

        @OnEvent
        public void reconcile() {
            int live = liveHandler == null ? 0 : liveHandler.getSum();
            int dropCopy = dropCopyFeed == null ? 0 : dropCopyFeed.getSum();
            reconcileDifference = Math.abs(live - dropCopy);
        }
    }

    @Data
    public static class SummingVector {
        final List<BookHandler> monitoredBooks = new ArrayList<>();
        int bookSum;

        @OnEvent
        public void reconcile() {
            bookSum = monitoredBooks.stream().mapToInt(BookHandler::getSum).sum();
        }
    }

    @Data
    public static class MaxDelta {
        final Reconciler reconciler;
        int maxDelta;

        @OnEvent
        public void calcMaxDelta() {
            maxDelta = Math.max(maxDelta, reconciler.getReconcileDifference());
        }
    }
}
