package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;

import static com.fluxtion.compiler.builder.stream.EventFlow.subscribe;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PrimitiveStreamBuilderTest extends MultipleSepTargetInProcessTest {
    public PrimitiveStreamBuilderTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void notifyTest() {
//        addAuditor();
        StreamBuildTest.NotifyAndPushTarget notifyAndPushTarget = new StreamBuildTest.NotifyAndPushTarget();
        sep(c -> subscribe(String.class)
                .filter(NumberUtils::isNumber)
                .mapToInt(StreamBuildTest::parseInt)
                .map(PrimitiveStreamBuilderTest::multiplyX10)
                .filter(PrimitiveStreamBuilderTest::gt10)
                .filter(PrimitiveStreamBuilderTest::gt10_withRefType)
                .notify(notifyAndPushTarget)
                .push(notifyAndPushTarget::setIntPushValue)
        );
        StreamBuildTest.NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(0, is(notifyTarget.getOnEventCount()));
        onEvent("sdsdsd 230");
        onEvent("230");
        assertThat(notifyTarget.getOnEventCount(), is(1));
        assertThat(notifyTarget.getIntPushValue(), is(2300));
    }

    @Test
    public void doubleTest(){
//        addAuditor();
        StreamBuildTest.NotifyAndPushTarget notifyAndPushTarget = new StreamBuildTest.NotifyAndPushTarget();
        sep(c -> subscribe(String.class)
                .filter(NumberUtils::isNumber)
                .mapToDouble(StreamBuildTest::parseDouble)
                .map(PrimitiveStreamBuilderTest::multiplyX10)
                .filter(PrimitiveStreamBuilderTest::gt10)
                .filter(PrimitiveStreamBuilderTest::gt10_withRefType)
                .notify(notifyAndPushTarget)
                .push(notifyAndPushTarget::setDoublePushValue)
        );
        StreamBuildTest.NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(0, is(notifyTarget.getOnEventCount()));
        onEvent("sdsdsd 230");
        onEvent("230");
        assertThat(notifyTarget.getOnEventCount(), is(1));
        assertThat(notifyTarget.getDoublePushValue(), is(2300d));
    }

    @Test
    public void longTest(){
//        addAuditor();
        StreamBuildTest.NotifyAndPushTarget notifyAndPushTarget = new StreamBuildTest.NotifyAndPushTarget();
        sep(c -> subscribe(String.class)
                .filter(NumberUtils::isNumber)
                .mapToLong(StreamBuildTest::parseLong)
                .map(PrimitiveStreamBuilderTest::multiplyX10)
                .filter(PrimitiveStreamBuilderTest::gt10)
                .filter(PrimitiveStreamBuilderTest::gt10_withRefType)
                .notify(notifyAndPushTarget)
                .push(notifyAndPushTarget::setLongPushValue)
        );
        StreamBuildTest.NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(0, is(notifyTarget.getOnEventCount()));
        onEvent("sdsdsd 230");
        onEvent("230");
        assertThat(notifyTarget.getOnEventCount(), is(1));
        assertThat(notifyTarget.getLongPushValue(), is(2300L));
    }

    //INT functions
    public static int multiplyX10(int input){
        return input * 10;
    }

    public static Boolean gt10(int i) {
        return i > 10;
    }

    //DOUBLE functions
    public static double multiplyX10(double input){
        return input * 10;
    }

    public static boolean gt10(double i) {
        return i > 10;
    }

    //DOUBLE functions
    public static long multiplyX10(long input){
        return input * 10;
    }

    public static boolean gt10(long i) {
        return i > 10;
    }

    //NUMBER
    public static boolean gt10_withRefType(Number number){
        return number.intValue() > 10;
    }

}
