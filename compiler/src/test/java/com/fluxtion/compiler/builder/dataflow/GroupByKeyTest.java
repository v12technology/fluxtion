package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.builder.dataflow.EventStreamBuildTest.Person;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.dataflow.groupby.GroupBy;
import com.fluxtion.runtime.dataflow.groupby.GroupByKey;
import com.fluxtion.runtime.dataflow.helpers.Collectors;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class GroupByKeyTest extends MultipleSepTargetInProcessTest {

    public GroupByKeyTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }


    @Test
    public void compoundGroupByTest() {
        sep(c -> {
            DataFlow.subscribe(Person.class)
                    .groupByFields(Person::getCountry, Person::getGender)
                    .mapKeys(GroupByKey::getKey)
                    .map(GroupBy::toMap).id("groupBy");
        });

        Person greg = new Person("greg", "UK", "male");
        Person josie = new Person("josie", "UK", "female");
        Person freddie = new Person("Freddie", "UK", "male");
        Person soren = new Person("Soren", "DK", "male");

        onEvent(greg);
        onEvent(josie);
        onEvent(freddie);
        onEvent(soren);

        Map<String, Person> map = getStreamed("groupBy");

        //get the sub lists
        SerializableFunction<Person, GroupByKey<Person>> keyFunction = GroupByKey.build(Person::getCountry, Person::getGender);

        Assert.assertEquals(freddie, map.get(keyFunction.apply(freddie).getKey()));
        Assert.assertEquals(josie, map.get(keyFunction.apply(josie).getKey()));
        Assert.assertEquals(soren, map.get(keyFunction.apply(soren).getKey()));
    }

    @Test
    public void groupByToList_compoundKey() {
        sep(c -> {
            DataFlow.subscribe(Person.class)
                    .groupByFieldsAggregate(Collectors.listFactory(), Person::getCountry, Person::getGender)
                    .mapKeys(GroupByKey::getKey)
                    .map(GroupBy::toMap).id("groupBy");
        });

        Person greg = new Person("greg", "UK", "male");
        Person josie = new Person("josie", "UK", "female");
        Person freddie = new Person("Freddie", "UK", "male");
        Person soren = new Person("Soren", "DK", "male");

        onEvent(greg);
        onEvent(josie);
        onEvent(freddie);
        onEvent(soren);

        Map<String, List<Person>> map = getStreamed("groupBy");

        //get the sub lists
        SerializableFunction<Person, GroupByKey<Person>> keyFunction = GroupByKey.build(Person::getCountry, Person::getGender);
        List<Person> ukMen = map.get(keyFunction.apply(greg).getKey());
        List<Person> ukWomen = map.get(keyFunction.apply(josie).getKey());
        List<Person> dkMen = map.get(keyFunction.apply(soren).getKey());

        //assert
        assertThat(ukMen, IsIterableContainingInAnyOrder.containsInAnyOrder(greg, freddie));
        assertThat(ukWomen, IsIterableContainingInAnyOrder.containsInAnyOrder(josie));
        assertThat(dkMen, IsIterableContainingInAnyOrder.containsInAnyOrder(soren));
    }
}
