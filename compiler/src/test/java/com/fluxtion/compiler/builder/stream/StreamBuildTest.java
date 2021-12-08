package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.generation.util.BaseSepInProcessTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.audit.EventLogControlEvent;
import org.junit.Test;

import static com.fluxtion.compiler.builder.stream.EventStreamBuilder.subscribe;

//public class StreamBuildTest extends MultipleSepTargetInProcessTest {
public class StreamBuildTest extends BaseSepInProcessTest {

//    public StreamBuildTest(boolean compiledSep) {
//        super(compiledSep);
//    }

    @Test
    public void subscribeToStream() {
        sep(c -> {
            subscribe(String.class)
//                    .peek(Console::println)
//                    .map(StreamBuildTest::toCaps)
//                    .peek(Console::println)
//                    .map(StreamBuildTest::triggerString).updateTrigger(subscribe(Double.class))
//                    .peek(Console::println)
                    .push(new StringReceiver()::newData)
            ;
//            c.addEventAudit(EventLogControlEvent.LogLevel.INFO);
        });
        onEvent("hello world");
        onEvent("test");
        onEvent((Double)12.3);
    }

    public static String toCaps(String in) {
        return in.toUpperCase();
    }

    public static String triggerString(String in){
        return "TRANSFORM:" + in;
    }

    public static class StringReceiver{
        public void newData(String data){
            System.out.println("received:" + data);
        }

        @OnEvent
        public void onEvent(){

        }
    }

}
