package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtim.Named;
import com.fluxtion.runtim.annotations.EventHandler;
import com.fluxtion.runtim.annotations.OnEvent;
import lombok.Data;
import org.junit.Test;

import static com.fluxtion.compiler.builder.stream.EventStreamBuilder.nodeAsEventStream;
import static com.fluxtion.compiler.builder.stream.EventStreamBuilder.subscribe;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class StreamBuildTest extends MultipleSepTargetInProcessTest {

    public StreamBuildTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void wrapNodeAsStreamTest(){
        sep(c -> nodeAsEventStream(new MyStringHandler())
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
                .push(new NotifyAndPushTarget()::setPushValue)
        );
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(0, is(notifyTarget.getPushValue()));
        onEvent((Integer)200);
        assertThat(200, is(notifyTarget.getPushValue()));
    }

    @Test
    public void mapTest(){
        sep(c -> subscribe(String.class)
                .map(StreamBuildTest::parseInt)
                .push(new NotifyAndPushTarget()::setPushValue)
        );
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(notifyTarget.getPushValue(), is(0));
        onEvent("86");
        assertThat(notifyTarget.getPushValue(), is(86));
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

    @Data
    public static class NotifyAndPushTarget implements Named {
        private transient int onEventCount;
        private transient int pushValue;

        @OnEvent
        public void notified() {
            onEventCount++;
        }

        @Override
        public String getName() {
            return "notifyTarget";
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

}
