package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.builder.stream.EventStreamBuildTest.Person;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.stream.helpers.Collectors;
import com.fluxtion.runtime.stream.helpers.Mappers;
import org.junit.Test;

import static com.fluxtion.compiler.builder.stream.EventFlow.subscribe;

public class NestedGroupByTest extends MultipleSepTargetInProcessTest {

    public NestedGroupByTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }


    @Test
    public void nestedGroupByWithHelper() {
        writeSourceFile = true;
        sep(c -> {
            subscribe(Person.class)
                    .groupBy(Person::getCountry, Mappers::identity, Collectors.groupBy(Person::getGender))
                    .console("[country/gender] :{}\n\n");
        });
        onEvent(new Person("greg", "UK", "male"));
        onEvent(new Person("josie", "UK", "female"));
        onEvent(new Person("Freddie", "UK", "male"));
        onEvent(new Person("Soren", "DK", "male"));
    }

    @Test
    public void nestedGroupByToList_WithHelper() {
        writeSourceFile = true;
        sep(c -> {
            subscribe(Person.class)
                    .groupBy(Person::getCountry, Mappers::identity, Collectors.groupByAsList(Person::getGender))
                    .console("[country/gender] :{}\n\n");
        });
        onEvent(new Person("greg", "UK", "male"));
        onEvent(new Person("josie", "UK", "female"));
        onEvent(new Person("Freddie", "UK", "male"));
        onEvent(new Person("Soren", "DK", "male"));
    }

    @Test
    public void nestedGroupByToList_WithHelperInstanceGroupBy() {
        writeSourceFile = true;
        sep(c -> {
            subscribe(Person.class)
                    .groupBy(Person::getCountry, Mappers::identity, Collectors.groupByAsList(new MapToGender()::asGender))
                    .console("[country/gender] :{}\n\n");
        });
        onEvent(new Person("greg", "UK", "male"));
        onEvent(new Person("josie", "UK", "female"));
        onEvent(new Person("Freddie", "UK", "male"));
        onEvent(new Person("Soren", "DK", "male"));
    }

    public static class MapToGender {

        public String asGender(Person p) {
            return p.getGender();
        }
    }
}
