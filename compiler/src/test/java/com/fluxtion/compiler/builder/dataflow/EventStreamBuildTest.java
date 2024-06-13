package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.dataflow.FlowSupplier;
import com.fluxtion.runtime.dataflow.Tuple;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.DoubleSumFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.primitive.IntSumFlowFunction;
import com.fluxtion.runtime.dataflow.groupby.GroupBy;
import com.fluxtion.runtime.dataflow.groupby.GroupBy.KeyValue;
import com.fluxtion.runtime.dataflow.helpers.Aggregates;
import com.fluxtion.runtime.dataflow.helpers.Collectors;
import com.fluxtion.runtime.dataflow.helpers.Mappers;
import com.fluxtion.runtime.event.DefaultEvent;
import com.fluxtion.runtime.event.Signal;
import com.fluxtion.runtime.node.NamedNode;
import com.fluxtion.runtime.partition.LambdaReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.apache.commons.lang3.math.NumberUtils;
import org.hamcrest.CoreMatchers;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.LongAdder;

import static com.fluxtion.compiler.builder.dataflow.DataFlow.*;
import static junit.framework.TestCase.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

public class EventStreamBuildTest extends MultipleSepTargetInProcessTest {

    public EventStreamBuildTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void wrapNodeAsStreamTest() {
        sep(c -> subscribeToNode(new MyStringHandler())
                .notify(new NotifyAndPushTarget()));
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(0, is(notifyTarget.getOnEventCount()));
        onEvent("test");
        assertThat(1, is(notifyTarget.getOnEventCount()));
    }

    @Data
    public static class MyPushTarget {
        String data;
    }

    @Test
    public void pushToNodeAddedWithId() {
        sep(c -> {
            MyPushTarget target = c.addNode(new MyPushTarget(), "target");
            DataFlow.subscribe(String.class)
                    .push(target::setData);
        });

        MyPushTarget target = getField("target");
        assertNotNull(target);
    }


    @Test
    public void wrapNodeAndPushStreamPropertyStreamTest() {
        sep(c -> subscribeToNodeProperty(MyStringHandler::getInputString)
                .push(new NotifyAndPushTarget()::setStringPushValue));
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(0, is(notifyTarget.getOnEventCount()));
        onEvent("test");
        assertThat(notifyTarget.getStringPushValue(), is("test"));
        assertThat(notifyTarget.getOnEventCount(), is(1));
    }

    @Test
    public void streamAsMemberTest() {
        sep(c -> c.addNode(new StreamAsMemberClass(subscribe(String.class).flowSupplier(), "target")));
        StreamAsMemberClass target = getField("target");
        assertFalse(target.isHasChanged());
        assertFalse(target.isTriggered());
        assertNull(target.stringValue());

        onEvent("test");
        assertTrue(target.isHasChanged());
        assertTrue(target.isTriggered());
        assertThat(target.stringValue(), is("test"));

        target.setTriggered(false);
        publishSignal("*");
        assertFalse(target.isHasChanged());
        assertTrue(target.isTriggered());
        assertThat(target.stringValue(), is("test"));
    }

    @Test
    public void nodePropertyStreamTest() {
        sep(c -> {
            MyStringHandler myStringHandler = new MyStringHandler();
            NotifyAndPushTarget target = new NotifyAndPushTarget();

            DataFlow
                    .subscribeToNodeProperty(myStringHandler::getInputString)
                    .push(target::setStringPushValue);
            DataFlow.subscribeToNodeProperty(myStringHandler::getParsedNumber)
                    .push(target::setIntPushValue);
        });

        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(0, is(notifyTarget.getOnEventCount()));
        onEvent("test");
        assertThat(notifyTarget.getStringPushValue(), is("test"));
        assertThat(notifyTarget.getIntPushValue(), is(0));
        assertThat(notifyTarget.getOnEventCount(), is(1));

        onEvent("42");
        assertThat(notifyTarget.getStringPushValue(), is("42"));
        assertThat(notifyTarget.getIntPushValue(), is(42));
        assertThat(notifyTarget.getOnEventCount(), is(2));
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
    public void pushTest() {
        sep(c -> subscribe(Integer.class)
                .push(new NotifyAndPushTarget()::setIntPushValue)
        );
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(0, is(notifyTarget.getIntPushValue()));
        onEvent((Integer) 200);
        assertThat(200, is(notifyTarget.getIntPushValue()));
    }

    @Test
    public void mapTest() {
        sep(c -> subscribe(String.class)
                .map(EventStreamBuildTest::parseInt)
                .push(new NotifyAndPushTarget()::setIntPushValue)
        );
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(notifyTarget.getIntPushValue(), is(0));
        onEvent("86");
        assertThat(notifyTarget.getIntPushValue(), is(86));
    }

    @Test
    public void sinkTest() {
        List<Object> myList = new ArrayList<>();
        sep(c -> subscribe(String.class)
                .sink("mySink")
        );

//        onEvent(SinkRegistration.sink("mySink", myList::add));
        addSink("mySink", myList::add);
        onEvent("aa");
        onEvent("2222");
        onEvent("three");

        assertThat(myList, is(Arrays.asList("aa", "2222", "three")));
    }

    @Test
    public void signalTest() {
        List<String> myList = new ArrayList<>();
        sep(c -> subscribeToSignal("myfilter", String.class).sink("strings"));
        addSink("strings", (String s) -> myList.add(s));
        publishSignal("myfilter", "aa");
        publishSignal("xx", "BBB");

        assertThat(myList, is(Collections.singletonList("aa")));
    }

    @Test
    public void flatMapTest() {
        sep(c -> subscribe(String.class)
                .flatMap(EventStreamBuildTest::csvToIterable)
                .push(new NotifyAndPushTarget()::addStringElement)
        );
        onEvent("one,2,THREE");
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(notifyTarget.getOnEventCount(), is(3));
        assertThat(notifyTarget.getStrings(), CoreMatchers.hasItems("one", "2", "THREE"));
    }

    @Test
    public void flatMapThenMapEachElementTest() {
        sep(c -> subscribe(String.class)
                .flatMap(EventStreamBuildTest::csvToIterable)
                .mapToInt(EventStreamBuildTest::parseInt)
                .map(Mappers.cumSumInt()).id("sum")
        );
        onEvent("15,33,55");
        assertThat(getStreamed("sum"), is(103));
    }

    @Test
    public void flatMapFromArrayThenMapEachElementTest() {
        sep(c -> subscribe(String.class)
                .flatMapFromArray(EventStreamBuildTest::csvToStringArray)
                .mapToInt(EventStreamBuildTest::parseInt)
                .map(Mappers.cumSumInt()).id("sum")
        );
        onEvent("15,33,55");
        assertThat(getStreamed("sum"), is(103));
    }

    @Test
    public void mapWithInstanceFunctionTest() {
        sep(c -> subscribe(Integer.class).map(new Adder()::add).id("cumsum"));
        onEvent(10);
        onEvent(10);
        onEvent(10);
        int cumsum = getStreamed("cumsum");
        assertThat(30, is(cumsum));
    }

    @Test
    public void mapTestWithFilter() {
        sep(c -> subscribe(String.class)
                .filter(NumberUtils::isCreatable)
                .map(EventStreamBuildTest::parseInt)
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
    public void multipleNotifiers() {
        sep(c -> {
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
                .filter(NumberUtils::isCreatable)
                .map(EventStreamBuildTest::parseInt)
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
        onEvent((Integer) 1);
        assertThat(notifyTarget.getIntPushValue(), is(20000));
        assertThat(notifyTarget.getOnEventCount(), is(3));
    }

    @Test
    public void overridePublish() {
        sep(c -> subscribe(String.class)
                        .filter(NumberUtils::isCreatable)
                        .map(EventStreamBuildTest::parseInt)
                        .map(new Adder()::add)
                        .publishTriggerOverride(subscribe(Integer.class))
//                .peek(Peekers.console("sum:{}"))
                        .push(new NotifyAndPushTarget()::setIntPushValue)
        );
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        onEvent("100");
        assertThat(notifyTarget.getIntPushValue(), is(0));
        assertThat(notifyTarget.getOnEventCount(), is(0));
//        System.out.println(notifyTarget);

        onEvent("1000");
        assertThat(notifyTarget.getIntPushValue(), is(0));
        assertThat(notifyTarget.getOnEventCount(), is(0));
//        System.out.println(notifyTarget);

        onEvent("10000");
        assertThat(notifyTarget.getIntPushValue(), is(0));
        assertThat(notifyTarget.getOnEventCount(), is(0));
//        System.out.println(notifyTarget);

        onEvent((Integer) 2);
        assertThat(notifyTarget.getIntPushValue(), is(11100));
        assertThat(notifyTarget.getOnEventCount(), is(1));
//        System.out.println(notifyTarget);
    }

    @Test
    public void defaultValueTest() {
        sep(c -> subscribe(String.class)
                .defaultValue("not null")
                .publishTrigger(subscribe(Signal.class))
                .push(new NotifyAndPushTarget()::setStringPushValue)
        );
        NotifyAndPushTarget notifyTarget = getField(NotifyAndPushTarget.DEFAULT_NAME);
        assertThat(notifyTarget.getStringPushValue(), nullValue());

        onEvent(new Signal<>());
        assertThat(notifyTarget.getStringPushValue(), is("not null"));
    }

    @Test
    public void defaultPrimitiveWrapperValueTest() {
        sep(c -> subscribe(Integer.class)
                .defaultValue(1).id("defaultValue"));
        Integer defaultValue = getStreamed("defaultValue");
        assertThat(defaultValue, is(1));
    }

    @Test
    public void defaultValueTestWithReset() {
        sep(c -> subscribe(String.class)
                .defaultValue("not null").resetTrigger(subscribe(Integer.class))
                .publishTrigger(subscribe(Signal.class))
                .push(new NotifyAndPushTarget()::setStringPushValue)
        );
        NotifyAndPushTarget notifyTarget = getField(NotifyAndPushTarget.DEFAULT_NAME);
        assertThat(notifyTarget.getStringPushValue(), nullValue());

        onEvent(new Signal<>());
        assertThat(notifyTarget.getStringPushValue(), is("not null"));

        onEvent("hello");
        assertThat(notifyTarget.getStringPushValue(), is("hello"));

        onEvent(Integer.valueOf(1));
        assertThat(notifyTarget.getStringPushValue(), is("not null"));
    }

    @Test
    public void filteredSubscriptionTest() {
        sep(c -> subscribe(FilteredInteger.class, "valid")
                .map(FilteredInteger::getValue)
                .push(new NotifyAndPushTarget()::setIntPushValue));
        NotifyAndPushTarget notifyTarget = getField(NotifyAndPushTarget.DEFAULT_NAME);
        assertThat(notifyTarget.getIntPushValue(), is(0));

        onEvent(new EventStreamBuildTest.FilteredInteger("ignored", 10));
        assertThat(notifyTarget.getIntPushValue(), is(0));

        onEvent(new EventStreamBuildTest.FilteredInteger("valid", 10));
        assertThat(notifyTarget.getIntPushValue(), is(10));
    }

    @Test
    public void lookupTest() {
        sep(c -> subscribe(PreMap.class)
                .lookup(PreMap::getName, EventStreamBuildTest::lookupFunction, EventStreamBuildTest::mapToPostMap)
                .map(PostMap::getLastName)
                .push(new NotifyAndPushTarget()::setStringPushValue));

        onEvent(new PreMap("test"));
        NotifyAndPushTarget notifyTarget = getField(NotifyAndPushTarget.DEFAULT_NAME);
        assertThat(notifyTarget.getStringPushValue(), is("TEST"));
    }

    @Test
    public void mergeTest() {
        LongAdder adder = new LongAdder();
        sep(c -> subscribe(Long.class)
                .merge(subscribe(String.class).map(EventStreamBuildTest::parseLong))
                .sink("integers"));
        addSink("integers", adder::add);
        onEvent(200L);
        onEvent("300");
        assertThat(adder.intValue(), is(500));
    }

    @Test
    public void slidingWindowTest() {
        sep(c -> subscribe(String.class)
                .map(EventStreamBuildTest::valueOfInt)
                .slidingAggregate(IntSumFlowFunction::new, 100, 4).id("sum"));
        addClock();
        onEvent("10");
        onEvent("10");
        onEvent("10");
        tickDelta(100);

        assertThat(getStreamed("sum"), is(nullValue()));

        onEvent("10");
        tickDelta(100);
        assertThat(getStreamed("sum"), is(nullValue()));

        tickDelta(100);
        assertThat(getStreamed("sum"), is(nullValue()));

        tickDelta(100);
        assertThat(getStreamed("sum"), is(40));

        tickDelta(100);
        assertThat(getStreamed("sum"), is(10));

        tickDelta(100);
        assertThat(getStreamed("sum"), is(0));
    }


    @Test
    public void aggregateTest() {
        sep(c -> subscribe(String.class)
                .map(EventStreamBuildTest::valueOfInt)
                .aggregate(IntSumFlowFunction::new).id("sum")
                .resetTrigger(subscribe(Signal.class))
                .push(new NotifyAndPushTarget()::setIntPushValue));
        NotifyAndPushTarget notifyTarget = getField(NotifyAndPushTarget.DEFAULT_NAME);
        assertThat(notifyTarget.getIntPushValue(), is(0));
        assertThat(getStreamed("sum"), is(nullValue()));

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
    public void aggregateToLIstTest() {
        sep(c -> subscribe(String.class)
                .aggregate(Collectors.listFactory(4))
                .id("myList"));

        onEvent("A");
        onEvent("F");
        onEvent("B");
        assertThat(getStreamed("myList"), contains("A", "F", "B"));
        onEvent("A1");
        onEvent("A2");
        onEvent("A3");
        onEvent("A4");
        onEvent("N");
        assertThat(getStreamed("myList"), contains("A2", "A3", "A4", "N"));
    }

    @Test
    public void tumblingMap() {
        sep(c -> subscribe(String.class)
                .map(EventStreamBuildTest::valueOfInt)
                .tumblingAggregate(IntSumFlowFunction::new, 300).id("sum")
                .push(new NotifyAndPushTarget()::setIntPushValue));
        NotifyAndPushTarget notifyTarget = getField(NotifyAndPushTarget.DEFAULT_NAME);

        onEvent("10");
        onEvent("10");
        onEvent("10");
        tickDelta(100);
        assertThat(notifyTarget.getIntPushValue(), is(0));
        assertThat(getStreamed("sum"), is(nullValue()));

        onEvent("10");
        tickDelta(100);
        assertThat(notifyTarget.getIntPushValue(), is(0));
        assertThat(getStreamed("sum"), is(nullValue()));

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
    public void testMapToSet() {
        sep(c -> DataFlow.subscribe(String.class).mapToSet().id("set"));
        HashSet<String> set = new HashSet<>();
        set.add("test");
        onEvent("test");
        onEvent("test");
        assertThat(getStreamed("set"), is(set));
        onEvent("test2");
        set.add("test2");
        assertThat(getStreamed("set"), is(set));
        onEvent("test");
        assertThat(getStreamed("set"), is(set));
    }

    @Test
    public void testMapToSetFromProperty() {
        sep(c -> DataFlow.subscribe(GroupByTest.Data.class).mapToSet(GroupByTest.Data::getName).id("set"));
        HashSet<String> set = new HashSet<>();
        set.add("test");
        onEvent(new GroupByTest.Data("test", 22));
        onEvent(new GroupByTest.Data("test", 31));
        assertThat(getStreamed("set"), is(set));
        onEvent(new GroupByTest.Data("test2", 2334));
        set.add("test2");
        assertThat(getStreamed("set"), is(set));
        onEvent(new GroupByTest.Data("test", 31));
        assertThat(getStreamed("set"), is(set));
    }

    @Test
    public void testMapToList() {
        sep(c -> DataFlow.subscribe(String.class).mapToList().id("list"));
        List<String> list = new ArrayList<>();
        list.add("test");
        list.add("test");
        onEvent("test");
        onEvent("test");
        assertThat(getStreamed("list"), is(list));
        onEvent("test2");
        list.add("test2");
        assertThat(getStreamed("list"), is(list));
        onEvent("test");
        list.add("test");
        assertThat(getStreamed("list"), is(list));
    }

    @Test
    public void testMapToList_MaxElements() {
        sep(c -> DataFlow.subscribe(String.class).mapToList(2).id("list"));
        List<String> list = new ArrayList<>();
        list.add("test");
        list.add("test");
        onEvent("test");
        onEvent("test");
        assertThat(getStreamed("list"), is(list));
        //deleting
        onEvent("test2");
        list.add("test2");
        list.remove(0);
        assertThat(getStreamed("list"), is(list));
        //deleting
        onEvent("test");
        list.add("test");
        list.remove(0);
        assertThat(getStreamed("list"), is(list));
    }

    @Test
    public void testMapToListFromProperty() {
        sep(c -> DataFlow.subscribe(GroupByTest.Data.class).mapToList(GroupByTest.Data::getName).id("list"));
        List<String> list = new ArrayList<>();
        list.add("test");
        list.add("test");
        onEvent(new GroupByTest.Data("test", 22));
        onEvent(new GroupByTest.Data("test", 31));
        assertThat(getStreamed("list"), is(list));
        onEvent(new GroupByTest.Data("test2", 2334));
        list.add("test2");
        assertThat(getStreamed("list"), is(list));
        onEvent(new GroupByTest.Data("test3", 3451));
        list.add("test3");
        assertThat(getStreamed("list"), is(list));
    }

    @Test
    public void testMapToListFromProperty_MaxElements() {
        sep(c -> DataFlow.subscribe(GroupByTest.Data.class).mapToList(GroupByTest.Data::getName, 2).id("list"));
        List<String> list = new ArrayList<>();
        list.add("test1");
        list.add("test2");
        onEvent(new GroupByTest.Data("test1", 22));
        onEvent(new GroupByTest.Data("test2", 31));
        assertThat(getStreamed("list"), is(list));
        //deleting
        onEvent(new GroupByTest.Data("test3", 2334));
        list.add("test3");
        list.remove(0);
        assertThat(getStreamed("list"), is(list));
        //deleting
        onEvent(new GroupByTest.Data("tes4", 3451));
        list.add("tes4");
        list.remove(0);
        assertThat(getStreamed("list"), is(list));
    }

    @Value
    public static class Person {
        String name;
        String country;
        String gender;
    }

    public static int doubleInt(int value) {
        return value * 2;
    }

    @Value
    public static class MergedType {
        int value;
        String name;
    }

    public static MergedType mergedTypeFromTuple(Tuple<Integer, String> t) {
        return new MergedType(t.getFirst(), t.getSecond());
    }

    public static boolean gt500Integer(Integer val) {
        return val > 500;
    }

    public static class MyIntFilter {
        private final int limit;

        public MyIntFilter(int limit) {
            this.limit = limit;
        }

        public boolean gt(Integer testValue) {
            return testValue > limit;
        }
    }

    public static class MyDynamicIntFilter {
        private final int limit;
        private int dynamicLimit = Integer.MAX_VALUE;

        @OnEventHandler
        public boolean setDynamicLimit(Integer dynamicLimit) {
            this.dynamicLimit = dynamicLimit;
            return true;
        }

        public MyDynamicIntFilter(int limit) {
            this.limit = limit;
        }

        public boolean gt(Integer testValue) {
            return testValue > limit || testValue > dynamicLimit;
        }
    }

    public static String toUpperCase(String s) {
        return s.toUpperCase();
    }

    public static String prefixInt(Integer input) {
        return "altered-" + input;
    }


    //    @Test
    public void flatMapFollowedByGroupByTest() {
//        addAuditor();
        sep(c -> {
            FlowBuilder<GroupBy<String, Double>> assetPosition = subscribe(Trade.class)
                    .flatMap(Trade::tradeLegs)
                    .groupBy(AssetAmountTraded::getId, AssetAmountTraded::getAmount, DoubleSumFlowFunction::new)
                    .resetTrigger(subscribe(String.class).filter("reset"::equalsIgnoreCase));

            FlowBuilder<GroupBy<String, Double>> assetPriceMap = subscribe(PairPrice.class)
                    .flatMap(new ConvertToBasePrice("USD")::toCrossRate)
                    .groupBy(AssetPrice::getId, AssetPrice::getPrice, DoubleSumFlowFunction::new);

            FlowBuilder<KeyValue<String, Double>> posDrivenMtmStream = assetPosition.map(GroupBy::lastKeyValue)
                    .mapBiFunction(EventStreamBuildTest::markToMarket, assetPriceMap.map(GroupBy::toMap));

            FlowBuilder<KeyValue<String, Double>> priceDrivenMtMStream = assetPriceMap.map(GroupBy::lastKeyValue)
                    .mapBiFunction(EventStreamBuildTest::markToMarket, assetPosition.map(GroupBy::toMap)).updateTrigger(assetPriceMap);

            //Mark to market
            posDrivenMtmStream.merge(priceDrivenMtMStream)
                    .groupBy(KeyValue::getKey, KeyValue::getValueAsDouble, Aggregates.doubleIdentityFactory())
                    .map(GroupBy::toMap)
                    .defaultValue(Collections::emptyMap)
                    .updateTrigger(subscribe(String.class).filter("publish"::equalsIgnoreCase))
                    .console("MtM:{}");

            //Positions
            assetPosition.map(GroupBy::toMap)
                    .defaultValue(Collections::emptyMap)
                    .updateTrigger(subscribe(String.class).filter("publish"::equalsIgnoreCase))
                    .filter(Objects::nonNull)
                    .console("positionMap:{}");
        });


        onEvent(new PairPrice("EURUSD", 1.5));
        onEvent(new PairPrice("GBPUSD", 2.0));
        onEvent("publish");
        System.out.println();

        onEvent(Trade.bought("EUR", 200, "GBP", 170));
        onEvent("publish");
        System.out.println();

        onEvent(new PairPrice("GBPUSD", 3.0));
        onEvent("publish");
        System.out.println();

        onEvent(Trade.sold("EUR", 10, "CHF", 15));
        onEvent("publish");
        System.out.println();

        onEvent(Trade.sold("EUR", 500, "USD", 650));
        onEvent("publish");
        System.out.println();

        onEvent(new PairPrice("USDCHF", 0.5));
        onEvent("publish");
    }

    @Value
    public static class CastFunction<T> {

        Class<T> clazz;

        public static <S> LambdaReflection.SerializableFunction<?, S> cast(Class<S> in) {
            return new CastFunction<>(in)::castInstance;
        }

        public T castInstance(Object o) {
            return clazz.cast(o);
        }
    }

    @EqualsAndHashCode
    public static class ConvertToBasePrice {
        private final String baseCurrency;
        private transient boolean hasPublished = false;

        public ConvertToBasePrice() {
            this("USD");
        }

        public ConvertToBasePrice(String baseCurrency) {
            this.baseCurrency = baseCurrency;
        }

        public List<AssetPrice> toCrossRate(PairPrice pairPrice) {
            List<AssetPrice> list = new ArrayList<>();
            if (!hasPublished) {
                list.add(new AssetPrice(baseCurrency, 1.0));
            }
            if (pairPrice.id.startsWith(baseCurrency)) {
                list.add(new AssetPrice(pairPrice.id.substring(3), 1.0 / pairPrice.price));
            } else if (pairPrice.id.contains(baseCurrency)) {
                list.add(new AssetPrice(pairPrice.id.substring(0, 3), pairPrice.price));
            }
            hasPublished = true;
            return list;
        }
    }

    @Value
    public static class PairPrice {
        String id;
        double price;

    }

    @Value
    public static class AssetPrice {
        String id;
        double price;
    }

    @Value
    public static class AssetAmountTraded {
        String id;
        double amount;
    }

    @Value
    public static class Trade {
        AssetAmountTraded dealt;
        AssetAmountTraded contra;

        public static Trade bought(String dealtId, double dealtAmount, String contraId, double contraAmount) {
            return new Trade(new AssetAmountTraded(dealtId, dealtAmount), new AssetAmountTraded(contraId, -1.0 * contraAmount));
        }

        public static Trade sold(String dealtId, double dealtAmount, String contraId, double contraAmount) {
            return new Trade(new AssetAmountTraded(dealtId, -1.0 * dealtAmount), new AssetAmountTraded(contraId, contraAmount));
        }

        public List<AssetAmountTraded> tradeLegs() {
            return Arrays.asList(dealt, contra);
        }
    }

    @Data
    public static class NotifyAndPushTarget implements NamedNode {
        public static final String DEFAULT_NAME = "notifyTarget";
        private final String name;
        private transient int onEventCount;
        private transient int intPushValue;
        private transient double doublePushValue;
        private transient long longPushValue;
        private transient String stringPushValue;
        private transient List<String> strings = new ArrayList<>();

        public NotifyAndPushTarget(String name) {
            this.name = name;
        }

        public NotifyAndPushTarget() {
            this(DEFAULT_NAME);
        }

        public void addStringElement(String element) {
            strings.add(element);
        }

        @OnTrigger
        public boolean notified() {
            onEventCount++;
            return true;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Data
    @AllArgsConstructor
    public static class PreMap {
        String name;
    }

    @Data
    @AllArgsConstructor
    public static class PostMap {
        String name;
        String lastName;
    }

    @Data
    public static class MyStringHandler {
        private String inputString;
        private int parsedNumber;

        @OnEventHandler
        public boolean newString(String in) {
            inputString = in;
            if (NumberUtils.isCreatable(in)) {
                parsedNumber = Integer.parseInt(in);
            }
            return true;
        }
    }

    @Data
    public static class Adder {
        int sum;

        public int add(int value) {
            return sum += value;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class FilteredInteger extends DefaultEvent {
        private final int value;

        public FilteredInteger(String filterId, int value) {
            super(filterId);
            this.value = value;
        }
    }

    @Value
    public static class FilterConfig {
        int limit;
    }

    @Value
    public static class MyData {
        int value;
    }

    @Value
    public static class KeyedData implements Comparable<KeyedData> {
        String id;
        int amount;

        @Override
        public int compareTo(@NotNull KeyedData other) {
            return amount - other.amount;
        }
    }


    public static KeyValue<String, Double> markToMarket(KeyValue<String, Double> assetPosition, Map<String, Double> assetPriceMap) {
        if (assetPosition == null || assetPriceMap == null) {
            return null;
        }
        Double price = assetPriceMap.getOrDefault(assetPosition.getKey(), Double.NaN);
        return new KeyValue<>(assetPosition.getKey(), price * assetPosition.getValue());
    }

    public static String lookupFunction(String in) {
        return in.toUpperCase();
    }

    public static PostMap mapToPostMap(PreMap preMap, String lookupValue) {
        return new PostMap(preMap.getName(), lookupValue);
    }

    public static int parseInt(String in) {
        return Integer.parseInt(in);
    }

    public static Integer valueOfInt(String in) {
        return parseInt(in);
    }

    public static double parseDouble(String in) {
        return Double.parseDouble(in);
    }

    public static long parseLong(String in) {
        return Long.parseLong(in);
    }

    public static Iterable<String> csvToIterable(String input) {
        return Arrays.asList(input.split(","));
    }

    public static String[] csvToStringArray(String input) {
        return input.split(",");
    }

    public static boolean myDataTooBig(MyData myData, FilterConfig config) {
        if (myData == null || config == null) {
            return false;
        }
        return myData.getValue() > config.getLimit();
    }

    public static boolean myDataIntTooBig(int myData, FilterConfig config) {
        if (config == null) {
            return false;
        }
        return myData > config.getLimit();
    }

    @Data
    public static class StreamAsMemberClass implements NamedNode {
        private final FlowSupplier<String> stringStream;
        private final String name;
        private transient boolean triggered;
        private transient boolean hasChanged;

        @Override
        public String getName() {
            return name;
        }

        @OnEventHandler
        public boolean signal(Signal signal) {
            triggered = true;
            hasChanged = stringStream.hasChanged();
            return true;
        }

        @OnTrigger
        public boolean trigger() {
            triggered = true;
            hasChanged = stringStream.hasChanged();
            return true;
        }

        public String stringValue() {
            return stringStream.get();
        }
    }

}
