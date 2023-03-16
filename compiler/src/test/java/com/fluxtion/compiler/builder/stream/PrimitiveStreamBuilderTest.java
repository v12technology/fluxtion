package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.builder.stream.EventStreamBuildTest.NotifyAndPushTarget;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.event.Signal;
import com.fluxtion.runtime.node.NamedNode;
import com.fluxtion.runtime.stream.EventStream.DoubleEventSupplier;
import com.fluxtion.runtime.stream.EventStream.IntEventSupplier;
import com.fluxtion.runtime.stream.EventStream.LongEventSupplier;
import com.fluxtion.runtime.stream.SinkRegistration;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateDoubleSum;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateIntSum;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateLongSum;
import com.fluxtion.runtime.stream.helpers.Aggregates;
import com.fluxtion.runtime.stream.helpers.Mappers;
import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;
import org.junit.Test;

import static com.fluxtion.compiler.builder.stream.EventFlow.*;
import static com.fluxtion.runtime.stream.helpers.Aggregates.countFactory;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

public class PrimitiveStreamBuilderTest extends MultipleSepTargetInProcessTest {
    public PrimitiveStreamBuilderTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void intTest() {
//        addAuditor();
        EventStreamBuildTest.NotifyAndPushTarget notifyAndPushTarget = new EventStreamBuildTest.NotifyAndPushTarget();
        sep(c -> subscribe(String.class)
                .filter(NumberUtils::isCreatable)
                .mapToInt(EventStreamBuildTest::parseInt)
                .map(PrimitiveStreamBuilderTest::multiplyX10)
                .filter(PrimitiveStreamBuilderTest::gt10)
                .filter(PrimitiveStreamBuilderTest::gt10_withRefType)
                .notify(notifyAndPushTarget)
                .push(notifyAndPushTarget::setIntPushValue)
        );
//        auditToFile("intTest");
        EventStreamBuildTest.NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(0, is(notifyTarget.getOnEventCount()));
        onEvent("sdsdsd 230");
        onEvent("230");
        assertThat(notifyTarget.getOnEventCount(), is(1));
        assertThat(notifyTarget.getIntPushValue(), is(2300));
    }

    @Test
    public void streamMembersTest() {
        sep(c -> c.addNode(new StreamMembers(
                subscribe(Integer.class).mapToInt(Integer::intValue).getEventSupplier(),
                subscribe(Double.class).mapToDouble(Double::doubleValue).getEventSupplier(),
                subscribe(Long.class).mapToLong(Long::longValue).getEventSupplier()
        ), "root"));
        StreamMembers wrapper = getField("root");
        onEvent(10);
        assertThat(wrapper.getIntEventSupplier().getAsInt(), is(10));
        assertThat(wrapper.getDoubleEventSupplier().getAsDouble(), is(0.0));
        assertThat(wrapper.getLongEventSupplier().getAsLong(), is(0L));

        onEvent(10.9);
        assertThat(wrapper.getIntEventSupplier().getAsInt(), is(10));
        assertThat(wrapper.getDoubleEventSupplier().getAsDouble(), is(10.9));
        assertThat(wrapper.getLongEventSupplier().getAsLong(), is(0L));

        onEvent(10L);
        assertThat(wrapper.getIntEventSupplier().getAsInt(), is(10));
        assertThat(wrapper.getDoubleEventSupplier().getAsDouble(), is(10.9));
        assertThat(wrapper.getLongEventSupplier().getAsLong(), is(10L));
    }

    @Test
    public void dynamicIntFilterTest() {
        MutableInt target = new MutableInt();
        sep(c -> subscribe(String.class)
                .mapToInt(EventStreamBuildTest::parseInt)
                .filter(PrimitiveStreamBuilderTest::gt, subscribeToIntSignal("test"))
                .sink("sink"));

//        onEvent(SinkRegistration.intSink("sink", target::add));
        addIntSink("sink", target::add);
        assertThat(target.intValue(), is(0));
        onEvent("1");
        assertThat(target.intValue(), is(0));
//        onEvent(Signal.intSignal("test", 5));
        publishIntSignal("test", 5);
        assertThat(target.intValue(), is(0));
        onEvent("12");
        assertThat(target.intValue(), is(12));

//        onEvent(Signal.intSignal("test", 7));
        publishIntSignal("test", 7);
        assertThat(target.intValue(), is(24));

        onEvent("8");
        assertThat(target.intValue(), is(32));
    }

    @Test
    public void dynamicDoubleFilterTest() {
        MutableDouble target = new MutableDouble();
        sep(c -> subscribe(String.class)
                .mapToDouble(EventStreamBuildTest::parseDouble)
                .filter(PrimitiveStreamBuilderTest::gt, subscribeToDoubleSignal("test"))
                .sink("sink"));

        onEvent(SinkRegistration.doubleSink("sink", target::add));
        assertThat(target.doubleValue(), closeTo(0, 0.0001));
        onEvent("3");
        assertThat(target.doubleValue(), closeTo(0, 0.0001));
        onEvent(Signal.doubleSignal("test", 5));
        assertThat(target.doubleValue(), closeTo(0, 0.0001));

        onEvent("12");
        assertThat(target.doubleValue(), closeTo(12, 0.0001));

        onEvent(Signal.doubleSignal("test", 7));
        assertThat(target.doubleValue(), closeTo(24, 0.0001));

        onEvent("8.5");
        assertThat(target.doubleValue(), closeTo(32.5, 0.0001));
    }


    @Test
    public void dynamicLongFilterTest() {
        MutableLong target = new MutableLong();
        sep(c -> subscribe(String.class)
                .mapToLong(EventStreamBuildTest::parseLong)
                .filter(PrimitiveStreamBuilderTest::gt, subscribeToLongSignal("test"))
                .sink("sink"));

        onEvent(SinkRegistration.longSink("sink", target::add));
        assertThat(target.longValue(), is(0L));
        onEvent("1");
        assertThat(target.longValue(), is(0L));
        onEvent(Signal.longSignal("test", 5));
        assertThat(target.longValue(), is(0L));
        onEvent("12");
        assertThat(target.longValue(), is(12L));

        onEvent(Signal.longSignal("test", 7));
        assertThat(target.longValue(), is(24L));

        onEvent("8");
        assertThat(target.longValue(), is(32L));
    }

    public static boolean gt(int inputVariable, int limitToCompare) {
        return inputVariable > limitToCompare;
    }

    public static boolean gt(double inputVariable, double limitToCompare) {
        return inputVariable > limitToCompare;
    }

    @Test
    public void doubleTest() {
//        addAuditor();
        EventStreamBuildTest.NotifyAndPushTarget notifyAndPushTarget = new EventStreamBuildTest.NotifyAndPushTarget();
        sep(c -> subscribe(String.class)
                .filter(NumberUtils::isCreatable)
                .mapToDouble(EventStreamBuildTest::parseDouble)
                .map(PrimitiveStreamBuilderTest::multiplyX10)
                .filter(PrimitiveStreamBuilderTest::gt10)
                .filter(PrimitiveStreamBuilderTest::gt10_withRefType)
                .notify(notifyAndPushTarget)
                .push(notifyAndPushTarget::setDoublePushValue)
        );
        EventStreamBuildTest.NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(0, is(notifyTarget.getOnEventCount()));
        onEvent("sdsdsd 230");
        onEvent("230");
        assertThat(notifyTarget.getOnEventCount(), is(1));
        assertThat(notifyTarget.getDoublePushValue(), is(2300d));
    }

    @Test
    public void longTest() {
//        addAuditor();
        EventStreamBuildTest.NotifyAndPushTarget notifyAndPushTarget = new EventStreamBuildTest.NotifyAndPushTarget();
        sep(c -> subscribe(String.class)
                .filter(NumberUtils::isCreatable)
                .mapToLong(EventStreamBuildTest::parseLong)
                .map(PrimitiveStreamBuilderTest::multiplyX10)
                .filter(PrimitiveStreamBuilderTest::gt10)
                .filter(PrimitiveStreamBuilderTest::gt10_withRefType)
                .notify(notifyAndPushTarget)
                .push(notifyAndPushTarget::setLongPushValue)
        );
        EventStreamBuildTest.NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(0, is(notifyTarget.getOnEventCount()));
        onEvent("sdsdsd 230");
        onEvent("230");
        assertThat(notifyTarget.getOnEventCount(), is(1));
        assertThat(notifyTarget.getLongPushValue(), is(2300L));
    }

    @Test
    public void aggregateCountTest() {
        sep(c -> subscribe(String.class)
                .aggregate(countFactory())
                .push(new NotifyAndPushTarget()::setIntPushValue));
        NotifyAndPushTarget notifyTarget = getField(NotifyAndPushTarget.DEFAULT_NAME);
        assertThat(notifyTarget.getIntPushValue(), is(0));

        onEvent("ttt");
        assertThat(notifyTarget.getIntPushValue(), is(1));
        onEvent("ttt");
        onEvent(23);
        assertThat(notifyTarget.getIntPushValue(), is(2));
        onEvent(23);
        onEvent("ttt");
        onEvent(23);

        assertThat(notifyTarget.getIntPushValue(), is(3));
    }

    @Test
    public void aggregateIntTest() {
        sep(c -> subscribe(String.class)
                .mapToInt(EventStreamBuildTest::parseInt)
                .aggregate(Aggregates.intSumFactory()).id("sum")
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
    public void aggregateDoubleTest() {
        sep(c -> subscribe(String.class)
                        .mapToDouble(EventStreamBuildTest::parseDouble)
//                .aggregate(AggregateDoubleSum::new).id("sum")
                        .aggregate(Aggregates.doubleSumFactory()).id("sum")
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
    public void aggregateLongTest() {
        sep(c -> subscribe(String.class)
                .mapToLong(EventStreamBuildTest::parseLong)
                .aggregate(Aggregates.longSumFactory()).id("sum")
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
    public void tumblingIntMap() {
        sep(c -> subscribe(String.class)
                .mapToInt(EventStreamBuildTest::parseInt)
                .tumblingAggregate(AggregateIntSum::new, 300).id("sum")
                .push(new NotifyAndPushTarget()::setIntPushValue));
        NotifyAndPushTarget notifyTarget = getField(NotifyAndPushTarget.DEFAULT_NAME);

        onEvent("10");
        onEvent("10");
        onEvent("10");
        tickDelta(100);
        assertThat(notifyTarget.getIntPushValue(), is(0));
        assertThat(getStreamed("sum"), is(0));

        onEvent("10");
        tickDelta(100);
        assertThat(notifyTarget.getIntPushValue(), is(0));
        assertThat(getStreamed("sum"), is(0));

        tickDelta(100);
        assertThat(notifyTarget.getIntPushValue(), is(40));
        assertThat(getStreamed("sum"), is(40));

        tickDelta(100);
        assertThat(notifyTarget.getIntPushValue(), is(40));
        assertThat(getStreamed("sum"), is(40));

        tickDelta(100);
        assertThat(notifyTarget.getIntPushValue(), is(40));
        assertThat(getStreamed("sum"), is(40));

        tickDelta(100);
        assertThat(notifyTarget.getIntPushValue(), is(0));
        assertThat(getStreamed("sum"), is(0));
    }

    @Test
    public void tumblingDoubleMap() {
        sep(c -> subscribe(String.class)
                .mapToDouble(EventStreamBuildTest::parseDouble)
                .tumblingAggregate(AggregateDoubleSum::new, 300).id("sum")
                .push(new NotifyAndPushTarget()::setDoublePushValue));
        NotifyAndPushTarget notifyTarget = getField(NotifyAndPushTarget.DEFAULT_NAME);

        onEvent("10.1");
        onEvent("10.1");
        onEvent("10.4");
        tickDelta(100);
        assertThat(notifyTarget.getDoublePushValue(), is(0d));
        assertThat(getStreamed("sum"), is(0d));

        onEvent("10");
        tickDelta(100);
        assertThat(notifyTarget.getDoublePushValue(), is(0d));
        assertThat(getStreamed("sum"), is(0d));

        tickDelta(100);
        assertThat(notifyTarget.getDoublePushValue(), closeTo(40.6, 000.1));
        assertThat(getStreamed("sum"), closeTo(40.6, 000.1));

        tickDelta(100);
        assertThat(notifyTarget.getDoublePushValue(), closeTo(40.6, 000.1));
        assertThat(getStreamed("sum"), closeTo(40.6, 000.1));

        tickDelta(100);
        assertThat(notifyTarget.getDoublePushValue(), closeTo(40.6, 000.1));
        assertThat(getStreamed("sum"), closeTo(40.6, 000.1));

        tickDelta(100);
        assertThat(notifyTarget.getDoublePushValue(), is(0d));
        assertThat(getStreamed("sum"), is(0d));
    }

    @Test
    public void tumblingLongMap() {
        sep(c -> subscribe(String.class)
                .mapToLong(EventStreamBuildTest::parseLong)
                .tumblingAggregate(AggregateLongSum::new, 300).id("sum")
                .push(new NotifyAndPushTarget()::setLongPushValue));
        NotifyAndPushTarget notifyTarget = getField(NotifyAndPushTarget.DEFAULT_NAME);

        onEvent("10");
        onEvent("10");
        onEvent("10");
        tickDelta(100);
        assertThat(notifyTarget.getLongPushValue(), is(0L));
        assertThat(getStreamed("sum"), is(0L));

        onEvent("10");
        tickDelta(100);
        assertThat(notifyTarget.getLongPushValue(), is(0L));
        assertThat(getStreamed("sum"), is(0L));

        tickDelta(100);
        assertThat(notifyTarget.getLongPushValue(), is(40L));
        assertThat(getStreamed("sum"), is(40L));

        tickDelta(100);
        assertThat(notifyTarget.getLongPushValue(), is(40L));
        assertThat(getStreamed("sum"), is(40L));

        tickDelta(100);
        assertThat(notifyTarget.getLongPushValue(), is(40L));
        assertThat(getStreamed("sum"), is(40L));

        tickDelta(100);
        assertThat(notifyTarget.getLongPushValue(), is(0L));
        assertThat(getStreamed("sum"), is(0L));
    }

    @Test
    public void slidingIntWindowTest() {
        sep(c -> subscribe(String.class)
                .mapToInt(EventStreamBuildTest::parseInt)
                .slidingAggregate(AggregateIntSum::new, 100, 4).id("sum")
                .push(new NotifyAndPushTarget()::setIntPushValue));
        setTime(0);
        onEvent("10");
        onEvent("10");
        onEvent("10");
        tickDelta(100);

        assertThat(getStreamed("sum"), is(0));

        onEvent("10");
        tickDelta(100);
        assertThat(getStreamed("sum"), is(0));

        tickDelta(100);
        assertThat(getStreamed("sum"), is(0));

        tickDelta(100);
        assertThat(getStreamed("sum"), is(40));

        tickDelta(100);
        assertThat(getStreamed("sum"), is(10));

        tickDelta(100);
        assertThat(getStreamed("sum"), is(0));
    }

    @Test
    public void slidingDoubleWindowTest() {
        sep(c -> subscribe(String.class)
                .mapToDouble(EventStreamBuildTest::parseDouble)
                .slidingAggregate(AggregateDoubleSum::new, 100, 4).id("sum")
                .push(new NotifyAndPushTarget()::setDoublePushValue));
        setTime(0);
        onEvent("10.5");
        onEvent("10.5");
        onEvent("10.3");
        tickDelta(100);

        assertThat(getStreamed("sum"), is(0d));

        onEvent("10.2");
        tickDelta(100);
        assertThat(getStreamed("sum"), is(0d));

        tickDelta(100);
        assertThat(getStreamed("sum"), is(0d));

        tickDelta(100);
        assertThat(getStreamed("sum"), closeTo(41.5, 0.0001));

        tickDelta(100);
        assertThat(getStreamed("sum"), closeTo(10.2, 0.0001));

        tickDelta(100);
        assertThat(getStreamed("sum"), is(0d));
    }

    @Test
    public void slidingLongWindowTest() {
        sep(c -> subscribe(String.class)
                .mapToLong(EventStreamBuildTest::parseLong)
                .slidingAggregate(AggregateLongSum::new, 100, 4).id("sum")
                .push(new NotifyAndPushTarget()::setLongPushValue));
        setTime(0);
        onEvent("10");
        onEvent("10");
        onEvent("10");
        tickDelta(100);

        assertThat(getStreamed("sum"), is(0L));

        onEvent("10");
        tickDelta(100);
        assertThat(getStreamed("sum"), is(0L));

        tickDelta(100);
        assertThat(getStreamed("sum"), is(0L));

        tickDelta(100);
        assertThat(getStreamed("sum"), is(40L));

        tickDelta(100);
        assertThat(getStreamed("sum"), is(10L));

        tickDelta(100);
        assertThat(getStreamed("sum"), is(0L));
    }

    @Test
    public void testMultipleIntConversions() {
//        addAuditor();
        EventStreamBuildTest.NotifyAndPushTarget notifyAndPushTarget = new EventStreamBuildTest.NotifyAndPushTarget();
        sep(c -> subscribe(String.class)
                .filter(NumberUtils::isCreatable)
                .mapToInt(EventStreamBuildTest::parseInt)
                .mapToLong(PrimitiveStreamBuilderTest::addMaxInteger)
                .push(notifyAndPushTarget::setLongPushValue)
                .mapToDouble(PrimitiveStreamBuilderTest::divideLongBy1_000)
                .push(notifyAndPushTarget::setDoublePushValue)
                .mapToInt(PrimitiveStreamBuilderTest::castDoubleToInt)
                .push(notifyAndPushTarget::setIntPushValue)
        );
        EventStreamBuildTest.NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        onEvent("1");
        assertThat(notifyTarget.getLongPushValue(), is(2147483648L));
        assertThat(notifyTarget.getDoublePushValue(), closeTo(2147483.648, 0.00001));
        assertThat(notifyTarget.getIntPushValue(), is(2147483));
    }

    @Test
    public void testDoubleConversions() {
//        addAuditor();
        sep(c -> {
            EventStreamBuildTest.NotifyAndPushTarget pushTarget = new EventStreamBuildTest.NotifyAndPushTarget();
            DoubleStreamBuilder doubleStreamBuilder = subscribe(Double.class).mapToDouble(Double::doubleValue);
            doubleStreamBuilder.mapToInt(PrimitiveStreamBuilderTest::castDoubleToInt).push(pushTarget::setIntPushValue);
            doubleStreamBuilder.mapToLong(PrimitiveStreamBuilderTest::castDoubleToLong).push(pushTarget::setLongPushValue);
        });
        EventStreamBuildTest.NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        onEvent(234.8);
        assertThat(notifyTarget.getIntPushValue(), is(234));
        assertThat(notifyTarget.getLongPushValue(), is(234L));
    }

    @Test
    public void testLongConversions() {
//        addAuditor();
        sep(c -> {
            EventStreamBuildTest.NotifyAndPushTarget pushTarget = new EventStreamBuildTest.NotifyAndPushTarget();
            LongStreamBuilder longStreamBuilder = subscribe(Long.class).mapToLong(Long::longValue);
            longStreamBuilder.mapToInt(PrimitiveStreamBuilderTest::castLongToInt).push(pushTarget::setIntPushValue);
            longStreamBuilder.mapToDouble(PrimitiveStreamBuilderTest::castLongToDouble).push(pushTarget::setDoublePushValue);
        });
        EventStreamBuildTest.NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        onEvent(234L);
        assertThat(notifyTarget.getIntPushValue(), is(234));
        assertThat(notifyTarget.getDoublePushValue(), closeTo(234.0, 0.0001));
    }

    @Test
    public void testSink() {
        MutableDouble d = new MutableDouble();
        MutableInt i = new MutableInt();
        MutableLong l = new MutableLong();

        sep(c -> subscribe(String.class)
                .mapToDouble(Double::parseDouble)
                .sink("doubleSink")
                .mapToInt(PrimitiveStreamBuilderTest::castDoubleToInt)
                .sink("intSink")
                .mapToLong(PrimitiveStreamBuilderTest::castIntToLong)
                .sink("longSink"));
        //register sinks
//        onEvent(SinkRegistration.doubleSink("doubleSink", d::add));
        addDoubleSink("doubleSink", d::add);
//        onEvent(SinkRegistration.intSink("intSink", i::add));
        addIntSink("intSink", i::add);
//        onEvent(SinkRegistration.longSink("longSink", l::add));
        addLongSink("longSink", l::add);
        //test
        onEvent("12.3");
        assertThat(d.doubleValue(), closeTo(12.3, 0.0001));
        assertThat(i.intValue(), is(12));
        assertThat(l.longValue(), is(12L));
        //de-register int
//        onEvent(SinkDeregister.sink("intSink"));
        removeSink("intSink");
        onEvent("58.4");
        assertThat(d.doubleValue(), closeTo(70.7, 0.0001));
        assertThat(i.intValue(), is(12));
        assertThat(l.longValue(), is(70L));
    }

    @Test
    public void defaultIntValueTest() {
//        addAuditor();
        sep(c -> subscribe(String.class)
                .mapToInt(EventStreamBuildTest::parseInt)
                .defaultValue(100)
                .publishTrigger(subscribe(Signal.class))
                .push(new EventStreamBuildTest.NotifyAndPushTarget()::setIntPushValue)
        );
        EventStreamBuildTest.NotifyAndPushTarget notifyTarget = getField(EventStreamBuildTest.NotifyAndPushTarget.DEFAULT_NAME);

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
                .mapToDouble(EventStreamBuildTest::parseDouble)
                .defaultValue(100)
                .publishTrigger(subscribe(Signal.class))
                .push(new EventStreamBuildTest.NotifyAndPushTarget()::setDoublePushValue)
        );
        EventStreamBuildTest.NotifyAndPushTarget notifyTarget = getField(EventStreamBuildTest.NotifyAndPushTarget.DEFAULT_NAME);

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
                .mapToLong(EventStreamBuildTest::parseLong)
                .defaultValue(100)
                .publishTrigger(subscribe(Signal.class))
                .push(new EventStreamBuildTest.NotifyAndPushTarget()::setLongPushValue)
        );
        EventStreamBuildTest.NotifyAndPushTarget notifyTarget = getField(EventStreamBuildTest.NotifyAndPushTarget.DEFAULT_NAME);

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
    public void boxPrimitiveTest() {
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

        assertThat(results.getBoxedInteger(), is(100));
        assertThat(results.getBoxedDouble(), is(100.5));
        assertThat(results.getBoxedLong(), is(100L));
    }

    @Test
    public void multipleStatefulFunctionsOfSameTypeTest() {
        sep(c -> {
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
    public void testIntReset() {
//        addAuditor();
        sep(c -> subscribe(MutableInt.class)
                .mapToInt(MutableInt::intValue)
                .map(Mappers.cumSumInt()).id("sum")
                .resetTrigger(subscribe(String.class).filter("reset"::equalsIgnoreCase)));

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

    public static long castIntToLong(int input) {
        return input;
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


    public static MutableDouble toMutableDouble(int val) {
        return new MutableDouble(val);
    }

    public static MutableLong toMutableLong(long val) {
        return new MutableLong(val);
    }

    public static MutableInt toMutableInt(int val) {
        return new MutableInt(val);
    }

    @Data
    public static class ResultsHolder implements NamedNode {
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

    @Data
    public static class StreamMembers {
        private final IntEventSupplier intEventSupplier;
        private final DoubleEventSupplier doubleEventSupplier;
        private final LongEventSupplier longEventSupplier;
    }

}
