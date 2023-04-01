package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.builder.dataflow.EventStreamBuildTest.Person;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.dataflow.groupby.GroupBy;
import com.fluxtion.runtime.dataflow.helpers.Collectors;
import com.fluxtion.runtime.dataflow.helpers.Mappers;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fluxtion.compiler.builder.dataflow.DataFlow.subscribe;
import static org.hamcrest.MatcherAssert.assertThat;

public class NestedGroupByTest extends MultipleSepTargetInProcessTest {

    private final Map<String, Map<String, Person>> results = new HashMap<>();
    private final Map<String, Map<String, List<Person>>> resultsList = new HashMap<>();
    private final Map<String, Map<String, Person>> expected = new HashMap<>();
    private final Map<String, Map<String, List<Person>>> expectedList = new HashMap<>();
    private final Map<String, Person> ukMap = new HashMap<>();
    private final Map<String, Person> dkMap = new HashMap<>();
    private final Map<String, List<Person>> ukMapList = new HashMap<>();
    private final Map<String, List<Person>> dkMapList = new HashMap<>();
    private final List<Person> ukMaleList = new ArrayList<>();
    private final List<Person> ukFemaleList = new ArrayList<>();
    private final List<Person> dkMaleList = new ArrayList<>();

    public NestedGroupByTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Before
    public void beforeEach() {
        results.clear();
        expected.clear();
        ukMap.clear();
        dkMap.clear();
        //list
        resultsList.clear();
        expectedList.clear();
        ukMapList.clear();
        dkMapList.clear();
        ukMaleList.clear();
        ukFemaleList.clear();
    }

    @Test
    public void nestedGroupByWithHelper() {
        sep(c -> {
            subscribe(Person.class)
                    .groupBy(
                            Person::getCountry,
                            Collectors.groupingBy(Person::getGender))
                    .sink("groupBy");
        });

        this.addSink("groupBy", this::convertToMap);
        onEvent(new Person("greg", "UK", "male"));
        expected.put("UK", ukMap);
        ukMap.put("male", new Person("greg", "UK", "male"));
        assertThat(results, CoreMatchers.is(expected));

        onEvent(new Person("josie", "UK", "female"));
        ukMap.put("female", new Person("josie", "UK", "female"));
        assertThat(results, CoreMatchers.is(expected));

        onEvent(new Person("Freddie", "UK", "male"));
        ukMap.put("male", new Person("Freddie", "UK", "male"));
        assertThat(results, CoreMatchers.is(expected));

        onEvent(new Person("Soren", "DK", "male"));
        expected.put("DK", dkMap);
        dkMap.put("male", new Person("Soren", "DK", "male"));
        assertThat(results, CoreMatchers.is(expected));
    }

    @Test
    public void nestedGroupByToList_WithHelper() {
        sep(c -> {
            subscribe(Person.class)
                    .groupBy(
                            Person::getCountry, Mappers::identity,
                            Collectors.groupingByCollectToList(Person::getGender))
                    .sink("groupBy");
        });
        this.addSink("groupBy", this::convertToMapList);
        onEvent(new Person("greg", "UK", "male"));
        expectedList.put("UK", ukMapList);
        ukMapList.put("male", ukMaleList);
        ukMaleList.add(new Person("greg", "UK", "male"));
        assertThat(resultsList, CoreMatchers.is(expectedList));

        onEvent(new Person("josie", "UK", "female"));
        ukMapList.put("female", ukFemaleList);
        ukFemaleList.add(new Person("josie", "UK", "female"));
        assertThat(resultsList, CoreMatchers.is(expectedList));

        onEvent(new Person("Freddie", "UK", "male"));
        ukMaleList.add(new Person("Freddie", "UK", "male"));
        assertThat(resultsList, CoreMatchers.is(expectedList));

        onEvent(new Person("Soren", "DK", "male"));
        expectedList.put("DK", dkMapList);
        dkMapList.put("male", dkMaleList);
        dkMaleList.add(new Person("Soren", "DK", "male"));
        assertThat(resultsList, CoreMatchers.is(expectedList));
    }

    @Test
    public void nestedDataFlowGroupBy_toCollector() {
        sep(c -> {
            DataFlow.groupBy(Person::getCountry, Collectors.groupingBy(Person::getGender, Collectors.toList()))
                    .sink("groupBy");
        });
        this.addSink("groupBy", this::convertToMapList);
        onEvent(new Person("greg", "UK", "male"));
        expectedList.put("UK", ukMapList);
        ukMapList.put("male", ukMaleList);
        ukMaleList.add(new Person("greg", "UK", "male"));
        assertThat(resultsList, CoreMatchers.is(expectedList));

        onEvent(new Person("josie", "UK", "female"));
        ukMapList.put("female", ukFemaleList);
        ukFemaleList.add(new Person("josie", "UK", "female"));
        assertThat(resultsList, CoreMatchers.is(expectedList));

        onEvent(new Person("Freddie", "UK", "male"));
        ukMaleList.add(new Person("Freddie", "UK", "male"));
        assertThat(resultsList, CoreMatchers.is(expectedList));

        onEvent(new Person("Soren", "DK", "male"));
        expectedList.put("DK", dkMapList);
        dkMapList.put("male", dkMaleList);
        dkMaleList.add(new Person("Soren", "DK", "male"));
        assertThat(resultsList, CoreMatchers.is(expectedList));
    }

    @Test
    public void nestedDataFlowGroupByWithHelper() {
        sep(c -> {
            DataFlow.groupBy(Person::getCountry, Collectors.groupingBy(Person::getGender))
                    .sink("groupBy");
        });

        this.addSink("groupBy", this::convertToMap);
        onEvent(new Person("greg", "UK", "male"));
        expected.put("UK", ukMap);
        ukMap.put("male", new Person("greg", "UK", "male"));
        assertThat(results, CoreMatchers.is(expected));

        onEvent(new Person("josie", "UK", "female"));
        ukMap.put("female", new Person("josie", "UK", "female"));
        assertThat(results, CoreMatchers.is(expected));

        onEvent(new Person("Freddie", "UK", "male"));
        ukMap.put("male", new Person("Freddie", "UK", "male"));
        assertThat(results, CoreMatchers.is(expected));

        onEvent(new Person("Soren", "DK", "male"));
        expected.put("DK", dkMap);
        dkMap.put("male", new Person("Soren", "DK", "male"));
        assertThat(results, CoreMatchers.is(expected));
    }

    @Test
    public void nestedGroupByToCollector_List_WithHelper() {
        writeSourceFile = true;
        sep(c -> {
            subscribe(Person.class)
                    .groupBy(
                            Person::getCountry,
                            Collectors.groupingBy(Person::getGender, Collectors.toList()))
                    .sink("groupBy");
        });
        this.addSink("groupBy", this::convertToMapList);
        onEvent(new Person("greg", "UK", "male"));
        expectedList.put("UK", ukMapList);
        ukMapList.put("male", ukMaleList);
        ukMaleList.add(new Person("greg", "UK", "male"));
        assertThat(resultsList, CoreMatchers.is(expectedList));

        onEvent(new Person("josie", "UK", "female"));
        ukMapList.put("female", ukFemaleList);
        ukFemaleList.add(new Person("josie", "UK", "female"));
        assertThat(resultsList, CoreMatchers.is(expectedList));

        onEvent(new Person("Freddie", "UK", "male"));
        ukMaleList.add(new Person("Freddie", "UK", "male"));
        assertThat(resultsList, CoreMatchers.is(expectedList));

        onEvent(new Person("Soren", "DK", "male"));
        expectedList.put("DK", dkMapList);
        dkMapList.put("male", dkMaleList);
        dkMaleList.add(new Person("Soren", "DK", "male"));
        assertThat(resultsList, CoreMatchers.is(expectedList));
    }

    @Test
    public void nestedGroupByToList_WithHelperInstanceGroupBy() {
        sep(c -> {
            subscribe(Person.class)
                    .groupBy(
                            Person::getCountry, Mappers::identity,
                            Collectors.groupingByCollectToList(new MapToGender()::asGender))
                    .sink("groupBy");
        });
        this.addSink("groupBy", this::convertToMapList);
        onEvent(new Person("greg", "UK", "male"));
        expectedList.put("UK", ukMapList);
        ukMapList.put("male", ukMaleList);
        ukMaleList.add(new Person("greg", "UK", "male"));
        assertThat(resultsList, CoreMatchers.is(expectedList));

        onEvent(new Person("josie", "UK", "female"));
        ukMapList.put("female", ukFemaleList);
        ukFemaleList.add(new Person("josie", "UK", "female"));
        assertThat(resultsList, CoreMatchers.is(expectedList));

        onEvent(new Person("Freddie", "UK", "male"));
        ukMaleList.add(new Person("Freddie", "UK", "male"));
        assertThat(resultsList, CoreMatchers.is(expectedList));

        onEvent(new Person("Soren", "DK", "male"));
        expectedList.put("DK", dkMapList);
        dkMapList.put("male", dkMaleList);
        dkMaleList.add(new Person("Soren", "DK", "male"));
        assertThat(resultsList, CoreMatchers.is(expectedList));
    }

    private void convertToMap(GroupBy<String, GroupBy<String, Person>> input) {
        Map<String, GroupBy<String, Person>> e = input.toMap();
        results.clear();
        e.forEach((s, g) -> {
            results.put(s, g.toMap());
        });
    }

    private void convertToMapList(GroupBy<String, GroupBy<String, List<Person>>> input) {
        Map<String, GroupBy<String, List<Person>>> e = input.toMap();
        resultsList.clear();
        e.forEach((s, g) -> {
            resultsList.put(s, g.toMap());
        });
    }

    public static class MapToGender {

        public String asGender(Person p) {
            return p.getGender();
        }
    }
}
