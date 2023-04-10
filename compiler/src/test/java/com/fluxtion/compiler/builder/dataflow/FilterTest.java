package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.builder.dataflow.EventStreamBuildTest.FilterConfig;
import com.fluxtion.compiler.builder.dataflow.EventStreamBuildTest.MyData;
import com.fluxtion.compiler.builder.dataflow.EventStreamBuildTest.NotifyAndPushTarget;
import com.fluxtion.compiler.generation.time.MutableNumber;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.dataflow.helpers.Mappers;
import com.fluxtion.runtime.dataflow.helpers.Predicates;
import org.junit.Assert;
import org.junit.Test;

import static com.fluxtion.compiler.builder.dataflow.DataFlow.subscribe;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FilterTest extends MultipleSepTargetInProcessTest {

    public FilterTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void filterTest() {
        sep(c -> subscribe(String.class)
                .filter(EventStreamBuildTest::isTrue)
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
    public void filterByPropertyTest() {
        sep(c -> subscribe(String.class)
                .filterByProperty(String::length, EventStreamBuildTest::gt5)
                .notify(new NotifyAndPushTarget())
        );
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(notifyTarget.getOnEventCount(), is(0));
        onEvent("short");
        assertThat(notifyTarget.getOnEventCount(), is(0));
        onEvent("loooong");
        assertThat(notifyTarget.getOnEventCount(), is(1));
    }

    @Test
    public void dynamicFilterTest() {
        sep(c -> subscribe(MyData.class)
                .filter(EventStreamBuildTest::myDataTooBig, subscribe(FilterConfig.class))
                .map(MyData::getValue)
                .push(new NotifyAndPushTarget()::setIntPushValue));
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        onEvent(new FilterConfig(10));
        onEvent(new MyData(5));
        assertThat(notifyTarget.getIntPushValue(), is(0));
        assertThat(notifyTarget.getOnEventCount(), is(0));

        onEvent(new MyData(50));
        assertThat(notifyTarget.getIntPushValue(), is(50));
        assertThat(notifyTarget.getOnEventCount(), is(1));
    }

    @Test
    public void dynamicFilterByPropertyTest() {
        sep(c -> subscribe(MyData.class)
                .filterByProperty(EventStreamBuildTest::myDataIntTooBig, MyData::getValue, subscribe(FilterConfig.class))
                .map(MyData::getValue)
                .push(new NotifyAndPushTarget()::setIntPushValue));
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        onEvent(new FilterConfig(10));
        onEvent(new MyData(5));
        assertThat(notifyTarget.getIntPushValue(), is(0));
        assertThat(notifyTarget.getOnEventCount(), is(0));

        onEvent(new MyData(50));
        assertThat(notifyTarget.getIntPushValue(), is(50));
        assertThat(notifyTarget.getOnEventCount(), is(1));
    }

    @Test
    public void dynamicFilterWithDefaultValueTest() {
        sep(c -> subscribe(MyData.class)
                .filter(EventStreamBuildTest::myDataTooBig,
                        subscribe(FilterConfig.class).defaultValue(new FilterConfig(4)))
                .map(MyData::getValue)
                .push(new NotifyAndPushTarget()::setIntPushValue));
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        onEvent(new MyData(5));
        assertThat(notifyTarget.getIntPushValue(), is(5));
        assertThat(notifyTarget.getOnEventCount(), is(1));

        onEvent(new FilterConfig(10));
        onEvent(new MyData(5));
        assertThat(notifyTarget.getIntPushValue(), is(5));
        assertThat(notifyTarget.getOnEventCount(), is(1));

        onEvent(new MyData(50));
        assertThat(notifyTarget.getIntPushValue(), is(50));
        assertThat(notifyTarget.getOnEventCount(), is(2));
    }

    @Test
    public void filterDynamicWithPrimitiveIntPropertyTest() {
        sep(c -> {
            FlowBuilder<MutableNumber> numberStream = DataFlow.subscribe(MutableNumber.class);
            numberStream.filterByProperty(
                            Predicates::greaterThanInt, MutableNumber::intValue, DataFlow.subscribeToIntSignal("number"))
                    .mapToInt(Mappers.count())
                    .id("count");

        });

        publishIntSignal("number", 100);
        onEvent(MutableNumber.fromLong(50));
        onEvent(MutableNumber.fromLong(150));
        onEvent(MutableNumber.fromLong(500));
        onEvent(MutableNumber.fromLong(50));

        Assert.assertEquals(2, (int) getStreamed("count"));
    }

    @Test
    public void filterDynamicWithPrimitiveDoublePropertyTest() {
        sep(c -> {
            FlowBuilder<MutableNumber> numberStream = DataFlow.subscribe(MutableNumber.class);
            numberStream.filterByProperty(
                            Predicates::greaterThanDouble, MutableNumber::doubleValue, DataFlow.subscribeToDoubleSignal("number"))
                    .mapToInt(Mappers.count())
                    .id("count");

        });

        publishDoubleSignal("number", 100);
        onEvent(MutableNumber.fromLong(50));
        onEvent(MutableNumber.fromLong(150));
        onEvent(MutableNumber.fromLong(500));
        onEvent(MutableNumber.fromLong(50));

        Assert.assertEquals(2, (int) getStreamed("count"));
    }

    @Test
    public void filterDynamicWithPrimitiveLongPropertyTest() {
        sep(c -> {
            FlowBuilder<MutableNumber> numberStream = DataFlow.subscribe(MutableNumber.class);
            numberStream.filterByProperty(
                            Predicates::greaterThanLong, MutableNumber::longValue, DataFlow.subscribeToLongSignal("number"))
                    .mapToInt(Mappers.count())
                    .id("count");

        });

        publishLongSignal("number", 100);
        onEvent(MutableNumber.fromLong(50));
        onEvent(MutableNumber.fromLong(150));
        onEvent(MutableNumber.fromLong(500));
        onEvent(MutableNumber.fromLong(50));

        Assert.assertEquals(2, (int) getStreamed("count"));
    }

    @Test
    public void filterFunctionWithPrimitiveArgumentTest() {
        sep(c -> {
            FlowBuilder<MutableNumber> numberStream = DataFlow.subscribe(MutableNumber.class);
            numberStream.filter(
                            FilterTest::filterMutableNumber, DataFlow.subscribeToIntSignal("number"))
                    .mapToInt(Mappers.count())
                    .id("count");

        });

        publishIntSignal("number", 100);
        onEvent(MutableNumber.fromLong(50));
        onEvent(MutableNumber.fromLong(150));
        onEvent(MutableNumber.fromLong(500));
        onEvent(MutableNumber.fromLong(50));

        Assert.assertEquals(2, (int) getStreamed("count"));
    }

    public static boolean filterMutableNumber(MutableNumber number, int check) {
        return number.intValue() > check;
    }
}
