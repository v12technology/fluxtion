package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.builder.stream.StreamBuildTest.NotifyAndPushTarget;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.Named;
import com.fluxtion.runtime.event.Signal;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateDoubleSum;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateIntSum;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateLongSum;
import com.fluxtion.runtime.stream.helpers.Mappers;
import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;
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
//        auditToFile("intTest");
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
    public void aggregateIntTest(){
        sep(c -> subscribe(String.class)
                .mapToInt(StreamBuildTest::parseInt)
                .aggregate(AggregateIntSum::new).id("sum")
                .resetTrigger(subscribe(Signal.class))
                .push(new NotifyAndPushTarget()::setIntPushValue)
        );

        NotifyAndPushTarget notifyTarget = getField(NotifyAndPushTarget.DEFAULT_NAME);
        assertThat(notifyTarget.getIntPushValue(), is(0));
        assertThat(getStreamed("sum"), is(0));

        onEvent("10");
        onEvent("10");
        onEvent("10");
        assertThat(notifyTarget.getIntPushValue(), is(30));
        assertThat(notifyTarget.getOnEventCount(), is(3));
        assertThat(getStreamed("sum"), is(30));

        onEvent(new Signal<>());
        assertThat(notifyTarget.getIntPushValue(), is(0));
        assertThat(notifyTarget.getOnEventCount(), is(4));
        assertThat(getStreamed("sum"), is(0));
    }

    @Test
    public void aggregateDoubleTest(){
        sep(c -> subscribe(String.class)
                .mapToDouble(StreamBuildTest::parseDouble)
                .aggregate(AggregateDoubleSum::new).id("sum")
                .resetTrigger(subscribe(Signal.class))
                .push(new NotifyAndPushTarget()::setDoublePushValue)
        );

        NotifyAndPushTarget notifyTarget = getField(NotifyAndPushTarget.DEFAULT_NAME);
        assertThat(notifyTarget.getDoublePushValue(), is(0d));
        assertThat(getStreamed("sum"), is(0d));

        onEvent("10.1");
        onEvent("10.1");
        onEvent("10.1");
        assertThat(notifyTarget.getDoublePushValue(), closeTo(30.3, 0.0001));
        assertThat(notifyTarget.getOnEventCount(), is(3));
        assertThat(getStreamed("sum"), closeTo(30.3, 0.0001));

        onEvent(new Signal<>());
        assertThat(notifyTarget.getDoublePushValue(), is(0d));
        assertThat(notifyTarget.getOnEventCount(), is(4));
        assertThat(getStreamed("sum"), is(0d));
    }

    @Test
    public void aggregateLongTest(){
        sep(c -> subscribe(String.class)
                .mapToLong(StreamBuildTest::parseLong)
                .aggregate(AggregateLongSum::new).id("sum")
                .resetTrigger(subscribe(Signal.class))
                .push(new NotifyAndPushTarget()::setLongPushValue)
        );

        NotifyAndPushTarget notifyTarget = getField(NotifyAndPushTarget.DEFAULT_NAME);
        assertThat(notifyTarget.getLongPushValue(), is(0L));
        assertThat(getStreamed("sum"), is(0L));

        onEvent("10");
        onEvent("10");
        onEvent("10");
        assertThat(notifyTarget.getLongPushValue(), is(30L));
        assertThat(notifyTarget.getOnEventCount(), is(3));
        assertThat(getStreamed("sum"), is(30L));

        onEvent(new Signal<>());
        assertThat(notifyTarget.getLongPushValue(), is(0L));
        assertThat(notifyTarget.getOnEventCount(), is(4));
        assertThat(getStreamed("sum"), is(0L));
    }

    @Test
    public void testMultipleIntConversions() {
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
    public void testDoubleConversions() {
//        addAuditor();
        sep(c -> {
            StreamBuildTest.NotifyAndPushTarget pushTarget = new StreamBuildTest.NotifyAndPushTarget();
            DoubleStreamBuilder doubleStreamBuilder = subscribe(Double.class).mapToDouble(Double::doubleValue);
            doubleStreamBuilder.mapToInt(PrimitiveStreamBuilderTest::castDoubleToInt).push(pushTarget::setIntPushValue);
            doubleStreamBuilder.mapToLong(PrimitiveStreamBuilderTest::castDoubleToLong).push(pushTarget::setLongPushValue);
        });
        StreamBuildTest.NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        onEvent((Double) 234.8);
        assertThat(notifyTarget.getIntPushValue(), is(234));
        assertThat(notifyTarget.getLongPushValue(), is(234L));
    }

    @Test
    public void testLongConversions() {
//        addAuditor();
        sep(c -> {
            StreamBuildTest.NotifyAndPushTarget pushTarget = new StreamBuildTest.NotifyAndPushTarget();
            LongStreamBuilder longStreamBuilder = subscribe(Long.class).mapToLong(Long::longValue);
            longStreamBuilder.mapToInt(PrimitiveStreamBuilderTest::castLongToInt).push(pushTarget::setIntPushValue);
            longStreamBuilder.mapToDouble(PrimitiveStreamBuilderTest::castLongToDouble).push(pushTarget::setDoublePushValue);
        });
        StreamBuildTest.NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        onEvent(234L);
        assertThat(notifyTarget.getIntPushValue(), is(234));
        assertThat(notifyTarget.getDoublePushValue(), closeTo(234.0, 0.0001));
    }


    @Test
    public void defaultIntValueTest() {
//        addAuditor();
        sep(c -> subscribe(String.class)
                .mapToInt(StreamBuildTest::parseInt)
                .defaultValue(100)
                .publishTrigger(subscribe(Signal.class))
                .push(new StreamBuildTest.NotifyAndPushTarget()::setIntPushValue)
        );
        StreamBuildTest.NotifyAndPushTarget notifyTarget = getField(StreamBuildTest.NotifyAndPushTarget.DEFAULT_NAME);

        onEvent(new Signal<>());
        assertThat(notifyTarget.getIntPushValue(), is(100));

        onEvent("2000");
        assertThat(notifyTarget.getIntPushValue(), is(2_000));

        onEvent("0");
        assertThat(notifyTarget.getIntPushValue(), is(0));
    }

    @Test
    public void defaultDoubleValueTest() {
//        addAuditor();
        sep(c -> subscribe(String.class)
                .mapToDouble(StreamBuildTest::parseDouble)
                .defaultValue(100)
                .publishTrigger(subscribe(Signal.class))
                .push(new StreamBuildTest.NotifyAndPushTarget()::setDoublePushValue)
        );
        StreamBuildTest.NotifyAndPushTarget notifyTarget = getField(StreamBuildTest.NotifyAndPushTarget.DEFAULT_NAME);

        onEvent(new Signal<>());
        assertThat(notifyTarget.getDoublePushValue(), is(100.0));

        onEvent("2000");
        assertThat(notifyTarget.getDoublePushValue(), is(2_000.0));

        onEvent("0");
        assertThat(notifyTarget.getDoublePushValue(), is(0.0));
    }

    @Test
    public void defaultLongValueTest() {
//        addAuditor();
        sep(c -> subscribe(String.class)
                .mapToLong(StreamBuildTest::parseLong)
                .defaultValue(100)
                .publishTrigger(subscribe(Signal.class))
                .push(new StreamBuildTest.NotifyAndPushTarget()::setLongPushValue)
        );
        StreamBuildTest.NotifyAndPushTarget notifyTarget = getField(StreamBuildTest.NotifyAndPushTarget.DEFAULT_NAME);

        onEvent(new Signal<>());
        assertThat(notifyTarget.getLongPushValue(), is(100L));

        onEvent("2000");
        assertThat(notifyTarget.getLongPushValue(), is(2_000L));

        onEvent("0");
        assertThat(notifyTarget.getLongPushValue(), is(0L));
    }

    @Test
    public void mapPrimitiveToRef() {
        sep(c -> {
            ResultsHolder results = new ResultsHolder();
            subscribe(MutableInt.class)
                    .mapToInt(MutableInt::intValue)
                    .mapToObj(PrimitiveStreamBuilderTest::toMutableDouble)
                    .push(results::setMutableDouble)
            ;

            subscribe(MutableInt.class)
//                    .mapToInt(MutableInt::intValue)
                    .mapToLong(MutableInt::longValue)
                    .mapToObj(PrimitiveStreamBuilderTest::toMutableLong)
                    .push(results::setMutableLong)
            ;

            subscribe(MutableInt.class)
                    .mapToInt(MutableInt::intValue)
                    .map(PrimitiveStreamBuilderTest::multiplyX10)
                    .mapToObj(PrimitiveStreamBuilderTest::toMutableInt)
                    .push(results::setMutableInt)
            ;

        });

        ResultsHolder results = getField(ResultsHolder.DEFAULT_NAME);
        onEvent(new MutableInt(100));
        assertThat(results.getMutableDouble(), is(new MutableDouble(100)));
        assertThat(results.getMutableLong(), is(new MutableLong(100)));
        assertThat(results.getMutableInt(), is(new MutableInt(1000)));

    }

    @Test
    public void boxPrimitiveTest(){
//        addAuditor();
        sep(c -> {
            ResultsHolder results = new ResultsHolder();
            subscribe(MutableInt.class)
                    .mapToInt(MutableInt::intValue)
                    .box()
                    .push(results::setBoxedInteger)
            ;

            subscribe(MutableDouble.class)
                    .mapToDouble(MutableDouble::doubleValue)
                    .box()
                    .push(results::setBoxedDouble)
            ;


            subscribe(MutableLong.class)
                    .mapToLong(MutableLong::longValue)
                    .box()
                    .push(results::setBoxedLong)
            ;
        });
        ResultsHolder results = getField(ResultsHolder.DEFAULT_NAME);
        onEvent(new MutableInt(100));
        onEvent(new MutableDouble(100.5));
        onEvent(new MutableLong(100));

        assertThat(results.getBoxedInteger(), is((Integer)100));
        assertThat(results.getBoxedDouble(), is((Double)100.5));
        assertThat(results.getBoxedLong(), is((Long)100L));
    }

    @Test
    public void multipleStatefulFunctionsOfSameTypeTest(){
        sep(c ->{
            subscribe(MutableInt.class)
                    .mapToInt(MutableInt::intValue)
                    .map(Mappers.cumSumInt()).id("sum")
                    .resetTrigger(subscribe(String.class).filter("reset"::equalsIgnoreCase));

            subscribe(MutableInt.class)
                    .mapToInt(MutableInt::intValue)
                    .map(Mappers.cumSumInt()).id("sum2");

            subscribe(MutableDouble.class)
                    .mapToInt(MutableDouble::intValue)
                    .map(Mappers.cumSumInt()).id("sum3");
        });
        onEvent(new MutableInt(10));
        onEvent(new MutableInt(10));
        onEvent(new MutableInt(10));
        onEvent(new MutableDouble(55.8));
        assertThat(getStreamed("sum"), is(30));

        onEvent("NO reset");
        assertThat(getStreamed("sum"), is(30));
        onEvent("reset");
        assertThat(getStreamed("sum"), is(0));
        assertThat(getStreamed("sum2"), is(30));
        assertThat(getStreamed("sum3"), is(55));
    }

    @Test
    public void testIntReset(){
//        addAuditor();
        sep(c ->{
            subscribe(MutableInt.class)
                    .mapToInt(MutableInt::intValue)
                    .map(Mappers.cumSumInt()).id("sum")
                    .resetTrigger(subscribe(String.class).filter("reset"::equalsIgnoreCase));
        });

        onEvent(new MutableInt(10));
        onEvent(new MutableInt(10));
        onEvent(new MutableInt(10));
        assertThat(getStreamed("sum"), is(30));

        onEvent("NO reset");
        assertThat(getStreamed("sum"), is(30));
        onEvent("reset");
        assertThat(getStreamed("sum"), is(0));
    }

    public static int multiplyDoubleBy100CastToInt(double input) {
        return (int) (100 * input);
    }

    public static long addMaxInteger(int val) {
        return Integer.MAX_VALUE + (long) val;
    }

    public static double divideLongBy1_000(long input) {
        return input / 1000.0;
    }

    public static int castDoubleToInt(double input) {
        return (int) input;
    }


    public static long castDoubleToLong(double input) {
        return (long) input;
    }

    public static double castLongToDouble(long input) {
        return (double) input;
    }

    public static int castLongToInt(long input) {
        return (int) input;
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


    public static MutableDouble toMutableDouble(int val){
        return new MutableDouble(val);
    }

    public static MutableLong toMutableLong(long val){
        return new MutableLong(val);
    }

    public static MutableInt toMutableInt(int val){
        return new MutableInt(val);
    }

    @Data
    public static class ResultsHolder implements Named {
        public static final String DEFAULT_NAME = "resultsHolder_Mutables";
        MutableInt mutableInt;
        MutableDouble mutableDouble;
        MutableLong mutableLong;

        Integer boxedInteger;
        Double boxedDouble;
        Long boxedLong;

        @Override
        public String getName() {
            return DEFAULT_NAME;
        }
    }

}
