package com.fluxtion.ext.declarative.builder.push;

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.ext.declarative.builder.helpers.DataEvent;
import com.fluxtion.ext.declarative.builder.helpers.DealEvent;
import com.fluxtion.ext.declarative.builder.helpers.TradeEvent;
import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.streaming.api.Wrapper;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.gt;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.count;
import com.fluxtion.ext.streaming.builder.factory.PushBuilder;
import com.fluxtion.ext.streaming.builder.stream.StreamOperatorService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class Push_2Test extends StreamInprocessTest {

    @Test
    public void pushNotification() {
        sep((c) -> {
            Wrapper<DealEvent> inSD = select(DealEvent.class);
            Wrapper<DataEvent> inDA = select(DataEvent.class);
            UpdateCount counter = c.addNode(new UpdateCount(), "counter");
            //push
            PushBuilder.pushNotification(inSD, counter);
            PushBuilder.pushNotification(inDA, counter);
        });

        sep.onEvent(new DealEvent());
        sep.onEvent(new DataEvent());
        sep.onEvent(new DealEvent());
        sep.onEvent(new TradeEvent());
        UpdateCount counter = getField("counter");
        assertThat(counter.count, is(3));
    }

    @Test
    public void anchorTest() {
        sep((c) -> {
            ArrayList list = c.addPublicNode(new ArrayList(), "recorder");
            Wrapper<DealEvent> inSD = select(DealEvent.class).id("sda");
            Tracker track_1 = c.addNode(new Tracker("track_1", inSD), "track_1");
            Tracker track_2 = c.addNode(new Tracker("track_2", inSD), "track_2");
            track_1.setSecondParent(select(DataEvent.class));
            track_2.setSecondParent(select(String.class));
            track_1.setRecorder(list);
            track_2.setRecorder(list);
            PushBuilder.anchor(track_1, track_2);
        });
        List<String> list = getField("recorder");
        assertThat(list, empty());
        //both
        onEvent(new DealEvent());
        assertThat(list, contains("track_1", "track_2"));
        //t1
        list.clear();
        assertThat(list, empty());
        onEvent(new DataEvent());
        assertThat(list, contains("track_1"));
        //t1
        list.clear();
        assertThat(list, empty());
        onEvent("hello world");
        assertThat(list, contains("track_2"));
    }

    @Test
    public void pushNotificationDataViaStream() {
        sep((c) -> {
            Wrapper<DealEvent> inSD = select(DealEvent.class);
            Wrapper<DataEvent> inDA = select(DataEvent.class);
            UpdateCount counter = c.addNode(new UpdateCount(), "counter");
            PushTarget pushTarget = c.addNode(new PushTarget(), "target");
            //push
            PushBuilder.pushNotification(inSD, counter);
            PushBuilder.pushNotification(inDA, counter);
            //push data
            PushBuilder.push(counter::getCount, pushTarget::setVal);

            StreamOperatorService.stream(pushTarget).id("streamedCumSum")
                .filter(PushTarget::getVal, gt(25))
                .log("[above 25]");
        });

        sep.onEvent(new DealEvent());
        sep.onEvent(new DataEvent());
        sep.onEvent(new DealEvent());
        sep.onEvent(new TradeEvent());
        UpdateCount counter = getField("counter");
        PushTarget target = getField("target");
        assertThat(counter.count, is(3));
        assertThat(target.count, is(3));
        assertThat(target.val, is(30));
    }

    @Test
    public void pushNotificationData() {
        sep((c) -> {
            Wrapper<DealEvent> inSD = select(DealEvent.class);
            Wrapper<DataEvent> inDA = select(DataEvent.class);
            UpdateCount counter = c.addNode(new UpdateCount(), "counter");
            PushTarget pushTarget = c.addNode(new PushTarget(), "target");
            //push
            PushBuilder.pushNotification(inSD, counter);
            PushBuilder.pushNotification(inDA, counter);
            //push data
            PushBuilder.push(counter::getCount, pushTarget::setVal);
            PushBuilder.pushSource(counter, pushTarget::pushUpdateCount);
            PushBuilder.pushSource(counter, pushTarget::setLongCount);

        });

        sep.onEvent(new DealEvent());
        sep.onEvent(new DataEvent());
        sep.onEvent(new DealEvent());
        sep.onEvent(new TradeEvent());
        UpdateCount counter = getField("counter");
        PushTarget target = getField("target");
        assertThat(counter.count, is(3));
        assertThat(target.count, is(3));
        assertThat(target.val, is(30));
        assertThat(target.updatePushVal, is(300));
        assertThat(target.longVal, is(3000l));
    }

    @Test
    public void pushToComplexObject() {
        sep((c) -> {
            select(LongNumber.class)
                .push(LongNumber::getVal, c.addPublicNode(new Date(), "date")::setTime)
                .map(count())
                .push(c.addPublicNode(new Date(), "date_2")::setTime);
        });

        Date date = getField("date");
        Date date_2 = getField("date_2");
        sep.onEvent(new LongNumber((8000)));
        sep.onEvent(new LongNumber((2000)));
        sep.onEvent(new LongNumber((1000)));
        assertThat(date.getTime(), is(1000L));
        assertThat(date_2.getTime(), is(3L));
    }

    public static class LongNumber {

        long val;

        public LongNumber(long val) {
            this.val = val;
        }

        public long getVal() {
            return val;
        }

        public void setVal(long val) {
            this.val = val;
        }

    }

    public static class UpdateCount extends Number {

        public int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        @Override
        public double doubleValue() {
            return getCount();
        }

        @Override
        public float floatValue() {
            return getCount();
        }

        @Override
        public int intValue() {
            return getCount();
        }

        @Override
        public long longValue() {
            return getCount();
        }

        @OnEvent
        public void update() {
            count++;
        }

    }

    public static class PushTarget {

        public int count;
        public int val;
        public int updatePushVal;
        public long longVal;

        @OnEvent
        public void update() {
            count++;
        }

        public void pushUpdateCount(UpdateCount update) {
            this.updatePushVal = update.getCount() * 100;
        }

        public void setVal(int val) {
            this.val = val * 10;
        }

        public void setLongCount(long longVal) {
            this.longVal = longVal * 1000;
        }

        public int getCount() {
            return count;
        }

        public int getVal() {
            return val;
        }

    }

    @Data
    public static class Tracker {

        final String name;
        final Object parent;
        List<String> recorder;
        Object secondParent;

        @OnEvent
        public boolean handleEvent() {
            recorder.add(name);
            return true;
        }
    }

    @Data
    public static class Recorder {

    }
}
