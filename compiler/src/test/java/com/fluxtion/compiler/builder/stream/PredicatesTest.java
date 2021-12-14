package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtim.stream.helpers.Mappers;
import com.fluxtion.runtim.stream.helpers.Predicates;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;

public class PredicatesTest  extends MultipleSepTargetInProcessTest {

    public PredicatesTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void hasChangedInt(){
        sep(c ->{
            EventFlow.subscribe(Integer.class)
                    .mapToInt(Integer::intValue)
                    .filter(Predicates.HAS_CHANGED_INT_FILTER)
                    .mapOnNotify(Mappers.newCount()).id("count")
                    .mapToInt(Mappers.Count::getCount)
            ;;
        });
        Mappers.Count count = getStreamed("count");

        onEvent((Integer)20);
        onEvent((Integer)20);
        onEvent((Integer)20);
        onEvent((Integer)20);
        assertThat(count.getCount(), CoreMatchers.is(1));

        onEvent((Integer)255);
        assertThat(count.getCount(), CoreMatchers.is(2));
    }


    @Test
    public void hasChangedDouble(){
        sep(c ->{
            EventFlow.subscribe(Integer.class)
                    .mapToDouble(Integer::doubleValue)
                    .filter(Predicates.HAS_CHANGED_DOUBLE_FILTER)
                    .mapOnNotify(Mappers.newCount()).id("count")
                    .mapToInt(Mappers.Count::getCount)
            ;
        });
        Mappers.Count count = getStreamed("count");

        onEvent((Integer)20);
        onEvent((Integer)20);
        onEvent((Integer)20);
        onEvent((Integer)20);
        assertThat(count.getCount(), CoreMatchers.is(1));

        onEvent((Integer)255);
        assertThat(count.getCount(), CoreMatchers.is(2));
    }


    @Test
    public void hasChangedLong(){
        sep(c ->{
            EventFlow.subscribe(Integer.class)
                    .mapToLong(Integer::longValue)
                    .filter(Predicates.HAS_CHANGED_LONG_FILTER)
                    .mapOnNotify(Mappers.newCount()).id("count")
                    .mapToInt(Mappers.Count::getCount)
            ;
        });
        Mappers.Count count = getStreamed("count");

        onEvent((Integer)20);
        onEvent((Integer)20);
        onEvent((Integer)20);
        onEvent((Integer)20);
        assertThat(count.getCount(), CoreMatchers.is(1));

        onEvent((Integer)255);
        assertThat(count.getCount(), CoreMatchers.is(2));
    }


    @Test
    public void hasChangedObject(){
        sep(c ->{
            EventFlow.subscribe(Integer.class)
                    .map(Objects::toString)
                    .filter(Predicates.hasChangedFilter())
                    .mapOnNotify(Mappers.newCount()).id("count")
                    .mapToInt(Mappers.Count::getCount)
            ;
        });
        Mappers.Count count = getStreamed("count");

        onEvent((Integer)20);
        onEvent((Integer)20);
        onEvent((Integer)20);
        onEvent((Integer)20);
        assertThat(count.getCount(), CoreMatchers.is(1));

        onEvent((Integer)255);
        assertThat(count.getCount(), CoreMatchers.is(2));
    }
}
