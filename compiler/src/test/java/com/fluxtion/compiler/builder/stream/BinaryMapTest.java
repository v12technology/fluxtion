package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.builder.stream.StreamBuildTest.NotifyAndPushTarget;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtim.stream.helpers.Mappers;
import lombok.Value;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;
import org.junit.Test;

import static com.fluxtion.compiler.builder.stream.EventFlow.subscribe;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

public class BinaryMapTest extends MultipleSepTargetInProcessTest {
    public BinaryMapTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testIntBinaryFunctionWith() {
        sep(c ->
                subscribe(Data_1.class)
                        .mapToInt(Data_1::getIntValue)
                        .map(BinaryMapTest::add,
                                subscribe(Data_2.class).mapToInt(Data_2::getIntValue)
                        )
                        .push(new NotifyAndPushTarget()::setIntPushValue)
        );
        NotifyAndPushTarget target = getField(NotifyAndPushTarget.DEFAULT_NAME);
        onEvent(new Data_1(10));
        assertThat(target.getIntPushValue(), is(10));
        onEvent(new Data_1(20));
        assertThat(target.getIntPushValue(), is(20));
        onEvent(new Data_2(80));
        assertThat(target.getIntPushValue(), is(100));
    }

    @Test
    public void testDoubleBinaryFunction() {
        sep(c ->
                subscribe(MutableDouble.class)
                        .mapToDouble(MutableDouble::doubleValue)
                        .map(BinaryMapTest::multiply,
                                subscribe(MutableInt.class).mapToDouble(MutableInt::doubleValue)
                        )
                        .push(new NotifyAndPushTarget()::setDoublePushValue)
        );
        NotifyAndPushTarget target = getField(NotifyAndPushTarget.DEFAULT_NAME);
        onEvent(new MutableDouble(10.1));
        assertThat(target.getDoublePushValue(), closeTo(0, 0.0001));
        onEvent(new MutableInt(20));
        assertThat(target.getDoublePushValue(), closeTo(202.0, 0.0001));
    }


    @Test
    public void testLongBinaryFunction() {
        sep(c ->
                subscribe(MutableLong.class)
                        .mapToLong(MutableLong::longValue)
                        .map(BinaryMapTest::addLong,
                                subscribe(MutableInt.class).mapToLong(MutableInt::longValue)
                        )
                        .push(new NotifyAndPushTarget()::setLongPushValue)
        );
        NotifyAndPushTarget target = getField(NotifyAndPushTarget.DEFAULT_NAME);
        onEvent(new MutableLong(1_000));
        assertThat(target.getLongPushValue(), is(1_000L));
        onEvent(new MutableInt(20_000));
        assertThat(target.getLongPushValue(), is(21_000L));
    }

    @Test
    public void testIntBinaryFunctionWithDefaultValue() {
        sep(c ->
                subscribe(Data_1.class)
                        .mapToInt(Data_1::getIntValue)
                        .map(BinaryMapTest::add,
                                subscribe(Data_2.class).mapToInt(Data_2::getIntValue).defaultValue(50)
                        )
                        .push(new NotifyAndPushTarget()::setIntPushValue)
        );
        NotifyAndPushTarget target = getField(NotifyAndPushTarget.DEFAULT_NAME);
        onEvent(new Data_1(10));
        assertThat(target.getIntPushValue(), is(60));
        onEvent(new Data_1(20));
        assertThat(target.getIntPushValue(), is(70));
        onEvent(new Data_2(80));
        assertThat(target.getIntPushValue(), is(100));
    }

    @Test
    public void testInTAddStandardFunction(){
        sep(c ->{
            IntStreamBuilder int1 = subscribe(Data_1.class).mapToInt(Data_1::getIntValue);
            IntStreamBuilder int2 = subscribe(Data_2.class).mapToInt(Data_2::getIntValue);

            int1.map(Mappers.ADD_INTS, int2).id("add");
            int1.map(Mappers.SUBTRACT_INTS, int2).id("subtract");
            int1.map(Mappers.MULTIPLY_INTS, int2).id("multiply");
        });

        onEvent(new Data_1(10));
        assertThat(getStreamed("add"), is(10));
        assertThat(getStreamed("subtract"), is(10));
        assertThat(getStreamed("multiply"), is(0));
        onEvent(new Data_2(130));
        assertThat(getStreamed("add"), is(140));
        assertThat(getStreamed("subtract"), is(-120));
        assertThat(getStreamed("multiply"), is(1300));
    }

    @Test
    public void testDoubleAddStandardFunction(){
        sep(c ->{
            DoubleStreamBuilder int1 = subscribe(Data_1.class).mapToInt(Data_1::getIntValue).box().mapToDouble(Integer::doubleValue);
            DoubleStreamBuilder int2 = subscribe(Data_2.class).mapToInt(Data_2::getIntValue).box().mapToDouble(Integer::doubleValue);

            int1.map(Mappers.ADD_DOUBLES, int2).id("add");
            int1.map(Mappers.SUBTRACT_DOUBLES, int2).id("subtract");
            int1.map(Mappers.MULTIPLY_DOUBLES, int2).id("multiply");
        });

        onEvent(new Data_1(10));
        assertThat(getStreamed("add"), is(10d));
        assertThat(getStreamed("subtract"), is(10d));
        assertThat(getStreamed("multiply"), is(0d));
        onEvent(new Data_2(130));
        assertThat(getStreamed("add"), is(140d));
        assertThat(getStreamed("subtract"), is(-120d));
        assertThat(getStreamed("multiply"), is(1300d));
    }


    @Test
    public void testLongAddStandardFunction(){
        sep(c ->{
            LongStreamBuilder int1 = subscribe(Data_1.class).mapToInt(Data_1::getIntValue).box().mapToLong(Integer::longValue);
            LongStreamBuilder int2 = subscribe(Data_2.class).mapToInt(Data_2::getIntValue).box().mapToLong(Integer::longValue);

            int1.map(Mappers.ADD_LONGS, int2).id("add");
            int1.map(Mappers.SUBTRACT_LONGS, int2).id("subtract");
            int1.map(Mappers.MULTIPLY_LONGS, int2).id("multiply");
        });

        onEvent(new Data_1(10));
        assertThat(getStreamed("add"), is(10L));
        assertThat(getStreamed("subtract"), is(10L));
        assertThat(getStreamed("multiply"), is(0L));
        onEvent(new Data_2(130));
        assertThat(getStreamed("add"), is(140L));
        assertThat(getStreamed("subtract"), is(-120L));
        assertThat(getStreamed("multiply"), is(1300L));
    }

    @Value
    public static class Data_1 {
        int intValue;
    }

    @Value
    public static class Data_2 {
        int intValue;
    }

    public static int add(int arg1, int arg2) {
        return arg1 + arg2;
    }

    public static double multiply(double arg1, double arg2) {
        return arg1 * arg2;
    }

    public static long addLong(long arg1, long arg2) {
        return arg1 + arg2;
    }

}
