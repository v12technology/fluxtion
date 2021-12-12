package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtim.stream.EventStream;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;

import static com.fluxtion.compiler.builder.stream.EventFlow.subscribe;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

public class PrimitiveStreamBuilderTest extends MultipleSepTargetInProcessTest {
    public PrimitiveStreamBuilderTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void intTest() {
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
    public void doubleTest() {
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
    public void longTest() {
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

    @Test
    public void testIntConversions() {
//        addAuditor();
        StreamBuildTest.NotifyAndPushTarget notifyAndPushTarget = new StreamBuildTest.NotifyAndPushTarget();
        sep(c -> subscribe(String.class)
                        .filter(NumberUtils::isNumber)
                        .mapToInt(StreamBuildTest::parseInt)
                        .mapToLong(PrimitiveStreamBuilderTest::addMaxInteger)
                        .push(notifyAndPushTarget::setLongPushValue)
                        .mapToDouble(PrimitiveStreamBuilderTest::divideLongBy1_000)
                        .push(notifyAndPushTarget::setDoublePushValue)
                        .mapToInt(PrimitiveStreamBuilderTest::castDoubleToInt)
                        .push(notifyAndPushTarget::setIntPushValue)
        );
        StreamBuildTest.NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        onEvent("1");
        assertThat(notifyTarget.getLongPushValue(), is(2147483648L));
        assertThat(notifyTarget.getDoublePushValue(), closeTo(2147483.648, 0.00001));
        assertThat(notifyTarget.getIntPushValue(), is(2147483));
    }


    @Test
    public void testDoubleConversions(){
//        addAuditor();
        sep(c ->{
            StreamBuildTest.NotifyAndPushTarget pushTarget = new StreamBuildTest.NotifyAndPushTarget();
            DoubleStreamBuilder<Double, EventStream<Double>> doubleStreamBuilder = subscribe(Double.class).mapToDouble(Double::doubleValue);
            doubleStreamBuilder.mapToInt(PrimitiveStreamBuilderTest::castDoubleToInt).push(pushTarget::setIntPushValue);
            doubleStreamBuilder.mapToLong(PrimitiveStreamBuilderTest::castDoubleToLong).push(pushTarget::setLongPushValue);
        });
        StreamBuildTest.NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        onEvent((Double)234.8);
        assertThat(notifyTarget.getIntPushValue(), is(234));
        assertThat(notifyTarget.getLongPushValue(), is(234L));
    }

    @Test
    public void testLongConversions(){
//        addAuditor();
        sep(c ->{
            StreamBuildTest.NotifyAndPushTarget pushTarget = new StreamBuildTest.NotifyAndPushTarget();
            LongStreamBuilder<Long, EventStream<Long>> doubleStreamBuilder = subscribe(Long.class).mapToLong(Long::longValue);
            doubleStreamBuilder.mapToInt(PrimitiveStreamBuilderTest::castLongToInt).push(pushTarget::setIntPushValue);
            doubleStreamBuilder.mapToDouble(PrimitiveStreamBuilderTest::castLongToDouble).push(pushTarget::setDoublePushValue);
        });
        StreamBuildTest.NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        onEvent(234L);
        assertThat(notifyTarget.getIntPushValue(), is(234));
        assertThat(notifyTarget.getDoublePushValue(), closeTo(234.0, 0.0001));
    }

    public static int multiplyDoubleBy100CastToInt(double input){
        return (int)(100 * input);
    }

    public static long addMaxInteger(int val) {
        return Integer.MAX_VALUE + (long) val;
    }

    public static double divideLongBy1_000(long input){
        return input/1000.0;
    }

    public static int castDoubleToInt(double input){
        return (int) input;
    }


    public static long castDoubleToLong(double input){
        return (long) input;
    }

    public static double castLongToDouble(long input){
        return (double)input;
    }

    public static int castLongToInt(long input){
        return (int)input;
    }


    //INT functions
    public static int multiplyX10(int input) {
        return input * 10;
    }

    public static int multiplyX10(Integer input) {
        return input * 10;
    }

    public static Boolean gt10(int i) {
        return i > 10;
    }

    //DOUBLE functions
    public static double multiplyX10(double input) {
        return input * 10;
    }

    public static boolean gt10(double i) {
        return i > 10;
    }

    //LONG functions
    public static long multiplyX10(long input) {
        return input * 10;
    }

    public static boolean gt10(long i) {
        return i > 10;
    }

    //NUMBER
    public static boolean gt10_withRefType(Number number) {
        return number.intValue() > 10;
    }

}
