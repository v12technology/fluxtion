package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.Named;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.event.DefaultEvent;
import com.fluxtion.runtime.event.Signal;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateDoubleSum;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateIntSum;
import com.fluxtion.runtime.stream.groupby.GroupBy;
import com.fluxtion.runtime.stream.groupby.GroupBy.KeyValue;
import com.fluxtion.runtime.stream.groupby.GroupByStreamed;
import com.fluxtion.runtime.stream.helpers.Aggregates;
import com.fluxtion.runtime.stream.helpers.Mappers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.apache.commons.lang3.math.NumberUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;

import static com.fluxtion.compiler.builder.stream.EventFlow.subscribe;
import static com.fluxtion.compiler.builder.stream.EventFlow.subscribeToNode;
import static com.fluxtion.compiler.builder.stream.EventFlow.subscribeToNodeProperty;
import static com.fluxtion.compiler.builder.stream.EventFlow.subscribeToSignal;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class StreamBuildTest extends MultipleSepTargetInProcessTest {

    public StreamBuildTest(boolean compiledSep) {
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
    public void nodePropertyStreamTest(){
        sep(c ->{
            MyStringHandler myStringHandler = new MyStringHandler();
            NotifyAndPushTarget target = new NotifyAndPushTarget();

            EventFlow
                    .subscribeToNodeProperty(myStringHandler::getInputString)
                    .push(target::setStringPushValue);
            EventFlow.subscribeToNodeProperty(myStringHandler::getParsedNumber)
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
                .map(StreamBuildTest::parseInt)
                .push(new NotifyAndPushTarget()::setIntPushValue)
        );
        NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(notifyTarget.getIntPushValue(), is(0));
        onEvent("86");
        assertThat(notifyTarget.getIntPushValue(), is(86));
    }

    @Test
    public void sinkTest(){
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
    public void signalTest(){
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
                .flatMap(StreamBuildTest::csvToIterable)
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
                .flatMap(StreamBuildTest::csvToIterable)
                .mapToInt(StreamBuildTest::parseInt)
                .map(Mappers.cumSumInt()).id("sum")
        );
        onEvent("15,33,55");
        assertThat(getStreamed("sum"), is(103));
    }

    @Test
    public void flatMapFromArrayThenMapEachElementTest() {
        sep(c -> subscribe(String.class)
                .flatMapFromArray(StreamBuildTest::csvToStringArray)
                .mapToInt(StreamBuildTest::parseInt)
                .map(Mappers.cumSumInt()).id("sum")
        );
        onEvent("15,33,55");
        assertThat(getStreamed("sum"), is(103));
    }

    @Test
    public void filterTest() {
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
    public void mapTestWithFilter() {
        sep(c -> subscribe(String.class)
                .filter(NumberUtils::isCreatable)
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
        onEvent((Integer) 1);
        assertThat(notifyTarget.getIntPushValue(), is(20000));
        assertThat(notifyTarget.getOnEventCount(), is(3));
    }

    @Test
    public void dynamicFilterTest(){
        sep(c -> subscribe(MyData.class)
                .filter(StreamBuildTest::myDataTooBig, subscribe(FilterConfig.class))
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
    public void dynamicFilterWithDefaultValueTest(){
        sep(c -> subscribe(MyData.class)
                .filter(StreamBuildTest::myDataTooBig,
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
    public void overridePublish() {
        sep(c -> subscribe(String.class)
                        .filter(NumberUtils::isCreatable)
                        .map(StreamBuildTest::parseInt)
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

        onEvent(new StreamBuildTest.FilteredInteger("ignored", 10));
        assertThat(notifyTarget.getIntPushValue(), is(0));

        onEvent(new StreamBuildTest.FilteredInteger("valid", 10));
        assertThat(notifyTarget.getIntPushValue(), is(10));
    }

    @Test
    public void lookupTest() {
        sep(c -> subscribe(PreMap.class)
                .lookup(StreamBuildTest::lookupFunction, PreMap::getName, StreamBuildTest::mapToPostMap)
                .map(PostMap::getLastName)
                .push(new NotifyAndPushTarget()::setStringPushValue));

        onEvent(new PreMap("test"));
        NotifyAndPushTarget notifyTarget = getField(NotifyAndPushTarget.DEFAULT_NAME);
        assertThat(notifyTarget.getStringPushValue(), is("TEST"));
    }

    @Test
    public void mergeTest(){
        LongAdder adder = new LongAdder();
        sep(c -> subscribe(Long.class)
                .merge(subscribe(String.class).map(StreamBuildTest::parseLong))
                .sink("integers"));
        addSink("integers", adder::add);
        onEvent(200L);
        onEvent("300");
        assertThat(adder.intValue(), is(500));
    }

    @Test
    public void slidingWindowTest() {
        sep(c -> subscribe(String.class)
                .map(StreamBuildTest::valueOfInt)
                .slidingAggregate(AggregateIntSum::new, 100, 4).id("sum"));
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

    @Value
    public static class CastFunction<T> {

        public static <S> LambdaReflection.SerializableFunction<?, S> cast(Class<S> in) {
            return new CastFunction<>(in)::castInstance;
        }

        Class<T> clazz;

        public T castInstance(Object o) {
            return clazz.cast(o);
        }
    }

    @Test
    public void aggregateTest() {
        sep(c -> subscribe(String.class)
                .map(StreamBuildTest::valueOfInt)
                .aggregate(AggregateIntSum::new).id("sum")
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
    public void tumblingMap() {
        sep(c -> subscribe(String.class)
                .map(StreamBuildTest::valueOfInt)
                .tumblingAggregate(AggregateIntSum::new, 300).id("sum")
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
    public void groupByTest(){
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();
        sep(c -> subscribe(KeyedData.class)
                .groupBy(KeyedData::getId, KeyedData::getAmount, AggregateIntSum::new)
                .map(GroupByStreamed::keyValue)
                .sink("keyValue"));

        addSink("keyValue", (KeyValue<String, Integer> kv) -> {
            results.clear();
            expected.clear();
            results.put(kv.getKey(), kv.getValue());
        });
        onEvent(new KeyedData("A", 22));
        expected.put("A", 22);
        assertThat(results, is(expected));

        onEvent(new KeyedData("B", 250));
        expected.put("B", 250);
        assertThat(results, is(expected));

        onEvent(new KeyedData("B", 140));
        expected.put("B", 390);
        assertThat(results, is(expected));

        onEvent(new KeyedData("A", 22));
        expected.put("A", 44);
        assertThat(results, is(expected));

        onEvent(new KeyedData("A", 22));
        expected.put("A", 66);
        assertThat(results, is(expected));
    }

    @Test
    public void groupByTumblingTest(){
//        addAuditor();
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> subscribe(KeyedData.class)
                .groupByTumbling(KeyedData::getId, KeyedData::getAmount, AggregateIntSum::new, 100)
                .map(GroupBy::map)
                .sink("map"));

        addSink("map", (Map<String, Integer> in) ->{
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        setTime(0);
        onEvent(new KeyedData("A", 40));

        tickDelta(25);
        onEvent(new KeyedData("A", 40));

        tickDelta(25);
        onEvent(new KeyedData("A", 40));
        onEvent(new KeyedData("B", 100));

        tickDelta(25);
        onEvent(new KeyedData("A", 40));
        onEvent(new KeyedData("B", 100));

        tickDelta(25);//100
        expected.put("A", 160);
        expected.put("B", 200);
        assertThat(results, is(expected));

        onEvent(new KeyedData("B", 400));
        onEvent(new KeyedData("C", 30));

        tickDelta(25);
        onEvent(new KeyedData("B", 400));
        onEvent(new KeyedData("C", 30));

        tickDelta(25);
        onEvent(new KeyedData("C", 30));

        tickDelta(25);
        onEvent(new KeyedData("C", 30));

        tickDelta(25);//100
        expected.put("B", 800);
        expected.put("C", 120);
        assertThat(results, is(expected));

        onEvent(new KeyedData("C", 80));

        tickDelta(25);
        onEvent(new KeyedData("C", 80));

        tickDelta(50);
        onEvent(new KeyedData("C", 80));

        tickDelta(25);//100
        expected.put("C", 240);
        assertThat(results, is(expected));

        tickDelta(200);
        assertThat(results, is(expected));
    }

    @Test
    public void groupBySlidingTest(){
//        addAuditor();
        Map<String, Integer> results = new HashMap<>();
        Map<String, Integer> expected = new HashMap<>();

        sep(c -> subscribe(KeyedData.class)
                .console("\t\tIN eventTime:%t -> {}")
                .groupBySliding(KeyedData::getId, KeyedData::getAmount, AggregateIntSum::new, 100, 10)
                .map(GroupBy::map)
                .console("OUT eventTime:%t {} ")
                .sink("map"));

        addSink("map", (Map<String, Integer> in) ->{
            results.clear();
            expected.clear();
            results.putAll(in);
        });

        setTime(0);
        onEvent(new KeyedData("A", 4000));

        tickDelta(100);
        onEvent(new KeyedData("A", 40));

        tickDelta(50, 4);
        onEvent(new KeyedData("A", 40));
        onEvent(new KeyedData("B", 100));

        tickDelta(100, 3);
        onEvent(new KeyedData("C", 40));

        tickDelta(10, 150);
        onEvent(new KeyedData("C", 40));
        onEvent(new KeyedData("B", 100));

        tickDelta(50, 2);
        onEvent(new KeyedData("C", 40));

        tickDelta(350);
        onEvent(new KeyedData("D", 100));

        tickDelta(10, 120);

    }

    public static KeyValue<String, Double> markToMarket(KeyValue<String, Double> assetPosition, Map<String, Double> assetPriceMap){
        if(assetPosition == null || assetPriceMap == null){
            return  null;
        }
        Double price = assetPriceMap.getOrDefault(assetPosition.getKey(), Double.NaN);
        return new KeyValue<>(assetPosition.getKey(), price * assetPosition.getValue());
    }

    @Test
    public void flatMapFollowedByGroupByTest(){
//        addAuditor();
        sep(c -> {
            EventStreamBuilder<GroupByStreamed<String, Double>> assetPosition = subscribe(Trade.class)
                    .flatMap(Trade::tradeLegs)
                    .groupBy(AssetAmountTraded::getId, AssetAmountTraded::getAmount, AggregateDoubleSum::new)
                    .resetTrigger(subscribe(String.class).filter("reset"::equalsIgnoreCase));

            EventStreamBuilder<GroupByStreamed<String, Double>> assetPriceMap = subscribe(PairPrice.class)
                    .flatMap(new ConvertToBasePrice("USD")::toCrossRate)
                    .groupBy(AssetPrice::getId, AssetPrice::getPrice, AggregateDoubleSum::new);

            EventStreamBuilder<KeyValue<String, Double>> posDrivenMtmStream = assetPosition.map(GroupByStreamed::keyValue)
                    .map(StreamBuildTest::markToMarket, assetPriceMap.map(GroupBy::map));

            EventStreamBuilder<KeyValue<String, Double>> priceDrivenMtMStream = assetPriceMap.map(GroupByStreamed::keyValue)
                    .map(StreamBuildTest::markToMarket, assetPosition.map(GroupBy::map)).updateTrigger(assetPriceMap);

            //Mark to market
            posDrivenMtmStream.merge(priceDrivenMtMStream)
                    .groupBy(KeyValue::getKey, KeyValue::getValueAsDouble, Aggregates.doubleIdentity())
                    .map(GroupBy::map)
                    .defaultValue(HashMap::new)
                    .updateTrigger(subscribe(String.class).filter("publish"::equalsIgnoreCase))
                    .console("MtM:{}");

            //Positions
            assetPosition.map(GroupBy::map)
                    .defaultValue(HashMap::new)
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

    @EqualsAndHashCode
    public static class ConvertToBasePrice{
        private final String baseCurrency;
        private transient boolean hasPublished = false;

        public ConvertToBasePrice(){
            this("USD");
        }

        public ConvertToBasePrice(String baseCurrency) {
            this.baseCurrency = baseCurrency;
        }

        public List<AssetPrice> toCrossRate(PairPrice pairPrice){
            List<AssetPrice> list = new ArrayList<>();
            if(!hasPublished){
                list.add(new AssetPrice(baseCurrency, 1.0));
            }
            if(pairPrice.id.startsWith(baseCurrency)){
                list.add(new AssetPrice(pairPrice.id.substring(3), 1.0/pairPrice.price));
            }else if(pairPrice.id.contains(baseCurrency)){
                list.add(new AssetPrice(pairPrice.id.substring(0,3), pairPrice.price));
            }
            hasPublished = true;
            return list;
        }
    }

    @Value
    public static class PairPrice{
        String id;
        double price;

    }
    @Value
    public static class AssetPrice{
        String id;
        double price;
    }

    @Value
    public static class AssetAmountTraded {
        String id;
        double amount;
    }
    @Value
    public static class Trade{
        public static Trade bought(String dealtId, double dealtAmount, String contraId, double contraAmount){
            return new Trade(new AssetAmountTraded(dealtId, dealtAmount), new AssetAmountTraded(contraId, -1.0 * contraAmount));
        }

        public static Trade sold(String dealtId, double dealtAmount, String contraId, double contraAmount){
            return new Trade(new AssetAmountTraded(dealtId, -1.0 * dealtAmount), new AssetAmountTraded(contraId, contraAmount));
        }
        AssetAmountTraded dealt;
        AssetAmountTraded contra;

        public List<AssetAmountTraded> tradeLegs(){
            return Arrays.asList(dealt, contra);
        }
    }
    @Data
    public static class NotifyAndPushTarget implements Named {
        public static final String DEFAULT_NAME = "notifyTarget";
        private transient int onEventCount;
        private transient int intPushValue;
        private transient double doublePushValue;
        private transient long longPushValue;
        private transient String stringPushValue;
        private transient List<String> strings = new ArrayList<>();
        private final String name;

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
        public void notified() {
            onEventCount++;
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

    public static String lookupFunction(String in) {
        return in.toUpperCase();
    }

    public static PostMap mapToPostMap(PreMap preMap, String lookupValue) {
        return new PostMap(preMap.getName(), lookupValue);
    }

    @Data
    public static class MyStringHandler {
        private String inputString;
        private int parsedNumber;

        @OnEventHandler
        public void newString(String in) {
            inputString = in;
            if (NumberUtils.isCreatable(in)) {
                parsedNumber = Integer.parseInt(in);
            }
        }
    }

    public static boolean isTrue(String in) {
        return Boolean.parseBoolean(in);
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

    @Data
    public static class Adder {
        int sum;

        public int add(int value) {
            return sum += value;
        }
    }

    @Data
    public static class FilteredInteger extends DefaultEvent {
        private final int value;

        public FilteredInteger(String filterId, int value) {
            super(filterId);
            this.value = value;
        }
    }

    @Value
    public static class FilterConfig{
        int limit;
    }

    @Value
    public static class MyData{
        int value;
    }

    @Value
    public static class KeyedData{
        String id;
        int amount;
    }

    public static boolean myDataTooBig(MyData myData, FilterConfig config){
        if(myData==null || config == null){
            return false;
        }
        return myData.getValue() > config.getLimit();
    }

}
