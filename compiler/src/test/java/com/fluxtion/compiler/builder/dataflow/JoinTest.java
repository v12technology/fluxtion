package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.dataflow.groupby.GroupBy;
import com.fluxtion.runtime.dataflow.groupby.MutableTuple;
import lombok.Data;
import lombok.val;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class JoinTest extends MultipleSepTargetInProcessTest {
    public JoinTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Data
    public final static class Pupil {
        private final int year;
        private final String school;
        private final String name;
    }

    @Data
    public final static class School {
        private final String name;
    }

    @Test
    public void innerJoinTest() {
        sep(c -> {
                    GroupByFlowBuilder<String, School> schools = DataFlow.subscribe(School.class)
                            .groupBy(School::getName);
                    val pupils = DataFlow.subscribe(Pupil.class)
                            .groupByToList(Pupil::getSchool)
                            .defaultValue(GroupBy.emptyCollection());

                    JoinFlowBuilder.innerJoin(schools, pupils)
                            .map(GroupBy::toMap).id("join");
                }
        );

        //register some schools
        onEvent(new School("RGS"));
        onEvent(new School("Belles"));
        onEvent(new School("Highland"));

        //register some pupils
        Pupil bob = new Pupil(2015, "RGS", "Bob");
        Pupil ashkay = new Pupil(2013, "RGS", "Ashkay");
        Pupil channing = new Pupil(2013, "Belles", "Channing");
        Pupil chelsea = new Pupil(2013, "RGS", "Chelsea");
        Pupil tamsin = new Pupil(2013, "Dagger high", "Tamsin");

        onEvent(bob);
        onEvent(ashkay);
        onEvent(channing);
        onEvent(chelsea);
        onEvent(tamsin);

        Map<String, MutableTuple<String, List<Pupil>>> actual = getStreamed("join");

        Assert.assertEquals(2, actual.size());
        assertThat(actual.get("RGS").getSecond(), IsIterableContainingInAnyOrder.containsInAnyOrder(bob, ashkay, chelsea));
        assertThat(actual.get("Belles").getSecond(), IsIterableContainingInAnyOrder.containsInAnyOrder(channing));

        //no right side
        Assert.assertNull(actual.get("Highland"));

        //no left only side
        Assert.assertNull(actual.get("Dagger high"));
    }

    @Test
    public void leftJoinTest() {
        sep(c -> {
                    val schools = DataFlow.subscribe(School.class)
                            .groupBy(School::getName);

                    val pupils = DataFlow.subscribe(Pupil.class)
                            .groupByToList(Pupil::getSchool)
                            .defaultValue(GroupBy.emptyCollection());

                    JoinFlowBuilder.leftJoin(schools, pupils)
                            .map(GroupBy::toMap).id("join");
                }
        );

        //register some schools
        onEvent(new School("RGS"));
        onEvent(new School("Belles"));
        onEvent(new School("Highland"));

        //register some pupils
        Pupil bob = new Pupil(2015, "RGS", "Bob");
        Pupil ashkay = new Pupil(2013, "RGS", "Ashkay");
        Pupil channing = new Pupil(2013, "Belles", "Channing");
        Pupil chelsea = new Pupil(2013, "RGS", "Chelsea");
        Pupil tamsin = new Pupil(2013, "Dagger high", "Tamsin");

        onEvent(bob);
        onEvent(ashkay);
        onEvent(channing);
        onEvent(chelsea);
        onEvent(tamsin);

        Map<String, MutableTuple<String, List<Pupil>>> actual = getStreamed("join");

        Assert.assertEquals(3, actual.size());
        assertThat(actual.get("RGS").getSecond(), IsIterableContainingInAnyOrder.containsInAnyOrder(bob, ashkay, chelsea));
        assertThat(actual.get("Belles").getSecond(), IsIterableContainingInAnyOrder.containsInAnyOrder(channing));

        //add left only side
        Assert.assertNull(actual.get("Highland").getSecond());

        //ignore right only side
        Assert.assertNull(actual.get("Dagger high"));
    }

    @Test
    public void rightJoinTest() {
        sep(c -> {
                    val schools = DataFlow.subscribe(School.class)
                            .groupBy(School::getName);
                    val pupils = DataFlow.subscribe(Pupil.class)
                            .groupByToList(Pupil::getSchool)
                            .defaultValue(GroupBy.emptyCollection());

                    JoinFlowBuilder.rightJoin(schools, pupils)
                            .map(GroupBy::toMap).id("join");
                }
        );

        //register some schools
        onEvent(new School("RGS"));
        onEvent(new School("Belles"));
        onEvent(new School("Highland"));

        //register some pupils
        Pupil bob = new Pupil(2015, "RGS", "Bob");
        Pupil ashkay = new Pupil(2013, "RGS", "Ashkay");
        Pupil channing = new Pupil(2013, "Belles", "Channing");
        Pupil chelsea = new Pupil(2013, "RGS", "Chelsea");
        Pupil tamsin = new Pupil(2013, "Dagger high", "Tamsin");

        onEvent(bob);
        onEvent(ashkay);
        onEvent(channing);
        onEvent(chelsea);
        onEvent(tamsin);

        Map<String, MutableTuple<String, List<Pupil>>> actual = getStreamed("join");

        Assert.assertEquals(3, actual.size());
        assertThat(actual.get("RGS").getSecond(), IsIterableContainingInAnyOrder.containsInAnyOrder(bob, ashkay, chelsea));
        assertThat(actual.get("Belles").getSecond(), IsIterableContainingInAnyOrder.containsInAnyOrder(channing));

        //right side only
        assertThat(actual.get("Dagger high").getSecond(), IsIterableContainingInAnyOrder.containsInAnyOrder(tamsin));

        //ignore left only side
        Assert.assertNull(actual.get("Highland"));
    }

    @Test
    public void outerJoinTest() {
        sep(c -> {
                    val schools = DataFlow.subscribe(School.class)
                            .groupBy(School::getName);
                    val pupils = DataFlow.subscribe(Pupil.class)
                            .groupByToList(Pupil::getSchool)
                            .defaultValue(GroupBy.emptyCollection());

                    JoinFlowBuilder.outerJoin(schools, pupils)
                            .map(GroupBy::toMap).id("join");
                }
        );

        //register some schools
        onEvent(new School("RGS"));
        onEvent(new School("Belles"));
        onEvent(new School("Highland"));

        //register some pupils
        Pupil bob = new Pupil(2015, "RGS", "Bob");
        Pupil ashkay = new Pupil(2013, "RGS", "Ashkay");
        Pupil channing = new Pupil(2013, "Belles", "Channing");
        Pupil chelsea = new Pupil(2013, "RGS", "Chelsea");
        Pupil tamsin = new Pupil(2013, "Dagger high", "Tamsin");

        onEvent(bob);
        onEvent(ashkay);
        onEvent(channing);
        onEvent(chelsea);
        onEvent(tamsin);

        Map<String, MutableTuple<String, List<Pupil>>> actual = getStreamed("join");

        Assert.assertEquals(4, actual.size());
        assertThat(actual.get("RGS").getSecond(), IsIterableContainingInAnyOrder.containsInAnyOrder(bob, ashkay, chelsea));
        assertThat(actual.get("Belles").getSecond(), IsIterableContainingInAnyOrder.containsInAnyOrder(channing));

        //left side only
        Assert.assertNull(actual.get("Highland").getSecond());

        //right side only
        assertThat(actual.get("Dagger high").getSecond(), IsIterableContainingInAnyOrder.containsInAnyOrder(tamsin));
    }

}
