package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtim.Named;
import com.fluxtion.runtim.annotations.EventHandler;
import com.fluxtion.runtim.annotations.OnEvent;
import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;

import static com.fluxtion.compiler.builder.stream.EventFlow.streamFromNode;
import static com.fluxtion.compiler.builder.stream.EventFlow.subscribe;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class StreamBuildTest extends MultipleSepTargetInProcessTest {

    public StreamBuildTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void wrapNodeAsStreamTest(){
        sep(c -> streamFromNode(new MyStringHandler())
                .notify(new NotifyAndPushTarget()));
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(0, is(notifyTarget.getOnEventCount()));
        onEvent("test");
        assertThat(1, is(notifyTarget.getOnEventCount()));
    }

    @Test
    public void notifyTest() {
        sep(c -> subscribe(String.class)
                .notify(new NotifyAndPushTarget())
        );
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(0, is(notifyTarget.getOnEventCount()));
        onEvent("test");
        assertThat(1, is(notifyTarget.getOnEventCount()));
    }

    @Test
    public void pushTest(){
        sep(c -> subscribe(Integer.class)
                .push(new NotifyAndPushTarget()::setIntPushValue)
        );
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(0, is(notifyTarget.getIntPushValue()));
        onEvent((Integer)200);
        assertThat(200, is(notifyTarget.getIntPushValue()));
    }

    @Test
    public void mapTest(){
        sep(c -> subscribe(String.class)
                .map(StreamBuildTest::parseInt)
                .push(new NotifyAndPushTarget()::setIntPushValue)
        );
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(notifyTarget.getIntPushValue(), is(0));
        onEvent("86");
        assertThat(notifyTarget.getIntPushValue(), is(86));
    }

    @Test
    public void filterTest(){
        sep(c -> subscribe(String.class)
                .filter(StreamBuildTest::isTrue)
                .notify(new NotifyAndPushTarget())
        );
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(notifyTarget.getOnEventCount(), is(0));
        onEvent("86");
        assertThat(notifyTarget.getOnEventCount(), is(0));
        onEvent("true");
        assertThat(notifyTarget.getOnEventCount(), is(1));
    }

    @Test
    public void mapTestWithFilter(){
        sep(c -> subscribe(String.class)
                .filter(NumberUtils::isNumber)
                .map(StreamBuildTest::parseInt)
                .map(new Adder()::add)
                .push(new NotifyAndPushTarget()::setIntPushValue)
        );
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(notifyTarget.getIntPushValue(), is(0));
        assertThat(notifyTarget.getOnEventCount(), is(0));

        onEvent("86");
        assertThat(notifyTarget.getIntPushValue(), is(86));
        assertThat(notifyTarget.getOnEventCount(), is(1));

        onEvent("ignore me");
        assertThat(notifyTarget.getIntPushValue(), is(86));
        assertThat(notifyTarget.getOnEventCount(), is(1));

        onEvent("14");
        assertThat(notifyTarget.getIntPushValue(), is(100));
        assertThat(notifyTarget.getOnEventCount(), is(2));
    }

    @Test
    public void multipleNotifiers(){
        sep(c ->{
            subscribe(String.class).notify(new NotifyAndPushTarget());
            subscribe(Double.class).notify(new NotifyAndPushTarget("doubleNotifier"));
        });
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        NotifyAndPushTarget doubleNotifier = getField("doubleNotifier");

        assertThat(notifyTarget.getOnEventCount(), is(0));
        assertThat(doubleNotifier.getOnEventCount(), is(0));

        onEvent("hellp");
        assertThat(notifyTarget.getOnEventCount(), is(1));
        assertThat(doubleNotifier.getOnEventCount(), is(0));

        onEvent(23323d);
        assertThat(notifyTarget.getOnEventCount(), is(1));
        assertThat(doubleNotifier.getOnEventCount(), is(1));
    }

    @Test
    public void mapTestWithFilterAndUpdateAndPublishTriggers() {
        sep(c -> subscribe(String.class)
                .filter(NumberUtils::isNumber)
                .map(StreamBuildTest::parseInt)
                .map(new Adder()::add)
                    .updateTrigger(subscribe(Double.class))
                    .publishTrigger(subscribe(Integer.class))
                .push(new NotifyAndPushTarget()::setIntPushValue)
        );
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(notifyTarget.getIntPushValue(), is(0));
        assertThat(notifyTarget.getOnEventCount(), is(0));

        onEvent("10");
        assertThat(notifyTarget.getIntPushValue(), is(0));
        assertThat(notifyTarget.getOnEventCount(), is(0));

        onEvent("ignore me");
        assertThat(notifyTarget.getIntPushValue(), is(0));
        assertThat(notifyTarget.getOnEventCount(), is(0));

        onEvent("100");
        onEvent("1000");
        onEvent("10000");
        onEvent(1.01);
        assertThat(notifyTarget.getIntPushValue(), is(10000));
        assertThat(notifyTarget.getOnEventCount(), is(1));

        onEvent(1.01);
        assertThat(notifyTarget.getIntPushValue(), is(20000));
        assertThat(notifyTarget.getOnEventCount(), is(2));

        onEvent("343540");
        onEvent((Integer)1);
        assertThat(notifyTarget.getIntPushValue(), is(20000));
        assertThat(notifyTarget.getOnEventCount(), is(3));
    }

    @Data
    public static class NotifyAndPushTarget implements Named {
        private transient int onEventCount;
        private transient int intPushValue;
        private transient double doublePushValue;
        private transient long longPushValue;
        private final String name;

        public NotifyAndPushTarget(String name) {
            this.name = name;
        }

        public NotifyAndPushTarget() {
            this("notifyTarget");
        }

        @OnEvent
        public void notified() {
            onEventCount++;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Data
    public static class MyStringHandler{
        private String inputString;

        @EventHandler
        public void newString(String in){
            inputString = in;
        }
    }

    public static boolean isTrue(String in) {
        return Boolean.parseBoolean(in);
    }

    public static int parseInt(String in){
        return Integer.parseInt(in);
    }

    public static double parseDouble(String in){
        return Double.parseDouble(in);
    }

    public static long parseLong(String in){
        return Long.parseLong(in);
    }

    @Data
    public static class Adder{
        int sum;

        public int add(int value){
            return sum += value;
        }
    }

}
