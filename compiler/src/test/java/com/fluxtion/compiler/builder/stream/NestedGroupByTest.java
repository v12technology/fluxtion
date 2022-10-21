package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.builder.stream.StreamBuildTest.Person;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.stream.groupby.GroupBy;
import com.fluxtion.runtime.stream.groupby.GroupByStreamed;
import com.fluxtion.runtime.stream.helpers.Mappers;
import org.junit.Test;

import java.util.Map;

import static com.fluxtion.compiler.builder.stream.EventFlow.subscribe;

public class NestedGroupByTest extends MultipleSepTargetInProcessTest {

    public NestedGroupByTest(boolean compiledSep) {
        super(compiledSep);
    }

    //TBD fix nested keys
    @Test
    public void nestedGroupBy() {
        sep(c -> {
            EventStreamBuilder<Map<String, GroupByStreamed<String, Person>>> filtered = subscribe(Person.class)
                    .console("->{}")
                    .groupBy(Person::getCountry, Mappers::valueIdentity)
                    .console("country key:{}")
                    .groupBy(NestedGroupByTest::getGender, Mappers::valueIdentity)
                    .map(GroupBy::map)
                    .console("secondary gender key:{}");
        });

//        onEvent(new Person("greg", "UK", "male"));
//        onEvent(new Person("josie", "UK", "female"));
//        onEvent(new Person("Freddie", "UK", "male"));
//        onEvent(new Person("Soren", "DK", "male"));
    }

    public static String getGender(GroupByStreamed<String, Person> stream) {
        return stream.value().getGender();
    }

    public static Person getPerson(GroupByStreamed<String, Person> stream) {
        return stream.value();
    }
}
