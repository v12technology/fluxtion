package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.builder.stream.StreamBuildTest.NotifyAndPushTarget;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
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
        sep(c -> {
            subscribe(Data_1.class)
                    .mapToInt(Data_1::getIntValue)
                    .map(BinaryMapTest::add,
                            subscribe(Data_2.class).mapToInt(Data_2::getIntValue)
                    )
                    .push( new NotifyAndPushTarget()::setIntPushValue);

        });
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
        sep(c -> {
            subscribe(MutableDouble.class)
                    .mapToDouble(MutableDouble::doubleValue)
                    .map(BinaryMapTest::multiply,
                            subscribe(MutableInt.class).mapToDouble(MutableInt::doubleValue)
                    )
                    .push( new NotifyAndPushTarget()::setDoublePushValue)
                    ;

        });
        NotifyAndPushTarget target = getField(NotifyAndPushTarget.DEFAULT_NAME);
        onEvent(new MutableDouble(10.1));
        assertThat(target.getDoublePushValue(), closeTo(0, 0.0001));
        onEvent(new MutableInt(20));
        assertThat(target.getDoublePushValue(), closeTo(202.0, 0.0001));
    }


    @Test
    public void testLongBinaryFunction() {
        sep(c -> {
            subscribe(MutableLong.class)
                    .mapToLong(MutableLong::longValue)
                    .map(BinaryMapTest::addLong,
                            subscribe(MutableInt.class).mapToLong(MutableInt::longValue)
                    )
                    .push( new NotifyAndPushTarget()::setLongPushValue);

        });
        NotifyAndPushTarget target = getField(NotifyAndPushTarget.DEFAULT_NAME);
        onEvent(new MutableLong(1_000));
        assertThat(target.getLongPushValue(), is(1_000L));
        onEvent(new MutableInt(20_000));
        assertThat(target.getLongPushValue(), is(21_000L));
    }

    @Test
    public void testIntBinaryFunctionWithDefaultValue() {
        sep(c -> {
            subscribe(Data_1.class)
                    .mapToInt(Data_1::getIntValue)
                    .map(BinaryMapTest::add,
                            subscribe(Data_2.class).mapToInt(Data_2::getIntValue).defaultValue(50)
                    )
                    .push( new NotifyAndPushTarget()::setIntPushValue);

        });
        NotifyAndPushTarget target = getField(NotifyAndPushTarget.DEFAULT_NAME);
        onEvent(new Data_1(10));
        assertThat(target.getIntPushValue(), is(60));
        onEvent(new Data_1(20));
        assertThat(target.getIntPushValue(), is(70));
        onEvent(new Data_2(80));
        assertThat(target.getIntPushValue(), is(100));
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

    public static double multiply(double arg1, double arg2){
        return arg1 * arg2;
    }

    public static long addLong(long arg1, long arg2){
        return arg1 + arg2;
    }

}
