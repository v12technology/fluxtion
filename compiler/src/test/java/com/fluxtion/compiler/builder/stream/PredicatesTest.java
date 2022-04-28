package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.stream.helpers.Mappers;
import com.fluxtion.runtime.stream.helpers.Predicates;
import com.fluxtion.runtime.stream.helpers.Predicates.AllUpdatedPredicate;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.Objects;

import static com.fluxtion.compiler.builder.stream.EventFlow.subscribe;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PredicatesTest extends MultipleSepTargetInProcessTest {

    public PredicatesTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void hasChangedInt() {
        sep(c -> {
            EventFlow.subscribe(Integer.class)
                    .mapToInt(Integer::intValue)
                    .filter(Predicates.HAS_CHANGED_INT_FILTER)
                    .mapOnNotify(Mappers.newCount()).id("count")
//                    .mapToInt(Mappers.Count::getCount)
            ;
        });
        Mappers.Count count = getStreamed("count");

        onEvent((Integer) 20);
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        assertThat(count.getCount(), CoreMatchers.is(1));

        onEvent((Integer) 255);
        assertThat(count.getCount(), CoreMatchers.is(2));
    }

    @Test
    public void hasChangedIntWithMapCount() {
        sep(c -> {
            EventFlow.subscribe(Integer.class)
                    .mapToInt(Integer::intValue)
                    .filter(Predicates.HAS_CHANGED_INT_FILTER)
                    .map(Mappers.countInt()).id("count")
            ;

            EventFlow.subscribe(String.class)
                    .mapToInt(Mappers.count()).id("count_strings");
        });
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        assertThat(getStreamed("count"), CoreMatchers.is(1));

        onEvent((Integer) 255);
        assertThat(getStreamed("count"), CoreMatchers.is(2));

        assertThat(getStreamed("count_strings"), CoreMatchers.is(0));
        onEvent("test");
        onEvent("test");
        onEvent("test");
        assertThat(getStreamed("count_strings"), CoreMatchers.is(3));
    }

    @Test
    public void hasChangedDouble() {
        sep(c -> {
            EventFlow.subscribe(Integer.class)
                    .mapToDouble(Integer::doubleValue)
                    .filter(Predicates.HAS_CHANGED_DOUBLE_FILTER)
                    .mapOnNotify(Mappers.newCount()).id("count")
                    .mapToInt(Mappers.Count::getCount)
            ;
        });
        Mappers.Count count = getStreamed("count");

        onEvent((Integer) 20);
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        assertThat(count.getCount(), CoreMatchers.is(1));

        onEvent((Integer) 255);
        assertThat(count.getCount(), CoreMatchers.is(2));
    }


    @Test
    public void hasChangedLong() {
        sep(c -> {
            EventFlow.subscribe(Integer.class)
                    .mapToLong(Integer::longValue)
                    .filter(Predicates.HAS_CHANGED_LONG_FILTER)
                    .mapOnNotify(Mappers.newCount()).id("count")
                    .mapToInt(Mappers.Count::getCount)
            ;
        });
        Mappers.Count count = getStreamed("count");

        onEvent((Integer) 20);
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        assertThat(count.getCount(), CoreMatchers.is(1));

        onEvent((Integer) 255);
        assertThat(count.getCount(), CoreMatchers.is(2));
    }


    @Test
    public void hasChangedObject() {
        sep(c -> {
            EventFlow.subscribe(Integer.class)
                    .map(Objects::toString)
                    .filter(Predicates.hasChangedFilter())
                    .mapOnNotify(Mappers.newCount()).id("count")
                    .mapToInt(Mappers.Count::getCount)
            ;
        });
        Mappers.Count count = getStreamed("count");

        onEvent((Integer) 20);
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        onEvent((Integer) 20);
        assertThat(count.getCount(), CoreMatchers.is(1));

        onEvent((Integer) 255);
        assertThat(count.getCount(), CoreMatchers.is(2));
    }

    @Test
    public void allUpdated() {
//        addAuditor();
        sep(c -> {
            LongStreamBuilder int1 = subscribe(BinaryMapTest.Data_1.class).mapToInt(BinaryMapTest.Data_1::getIntValue).box().mapToLong(Integer::longValue);
            LongStreamBuilder int2 = subscribe(BinaryMapTest.Data_2.class).mapToInt(BinaryMapTest.Data_2::getIntValue).box().mapToLong(Integer::longValue);
            int1.map(Mappers.DIVIDE_LONGS, int2).id("divide")
                    .updateTrigger(new AllUpdatedPredicate(StreamHelper.getSourcesAsList(int1, int2)));
        });
        onEvent(new BinaryMapTest.Data_1(100));
        assertThat(getStreamed("divide"), is(0L));
        onEvent(new BinaryMapTest.Data_2(25));
        assertThat(getStreamed("divide"), is(4L));
    }

    @Test
    public void allUpdatedWithReset() {
//        addAuditor();
        sep(c -> {
            //inputs
            IntStreamBuilder int1 = subscribe(BinaryMapTest.Data_1.class).mapToInt(BinaryMapTest.Data_1::getIntValue);
            IntStreamBuilder int2 = subscribe(BinaryMapTest.Data_2.class).mapToInt(BinaryMapTest.Data_2::getIntValue);
            //filter - trigger if all inputs have updated
            AllUpdatedPredicate allUpdatedPredicate = new AllUpdatedPredicate(
                    StreamHelper.getSourcesAsList(int1, int2)
            );
            int1.map(Mappers.DIVIDE_INTS, int2).id("divide")
                    .updateTrigger(allUpdatedPredicate)
                    .resetTrigger(subscribe(String.class));
        });
        onEvent(new BinaryMapTest.Data_1(100));
        assertThat(getStreamed("divide"), is(0));
        onEvent(new BinaryMapTest.Data_2(25));
        assertThat(getStreamed("divide"), is(4));
    }

}
