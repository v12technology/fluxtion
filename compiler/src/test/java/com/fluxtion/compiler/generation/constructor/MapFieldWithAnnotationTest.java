package com.fluxtion.compiler.generation.constructor;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.audit.EventLogControlEvent;
import com.fluxtion.runtime.audit.EventLogControlEvent.LogLevel;
import com.fluxtion.runtime.callback.DirtyStateMonitor;
import com.fluxtion.runtime.callback.EventDispatcher;
import com.fluxtion.runtime.node.NamedNode;
import org.junit.Assert;
import org.junit.Test;

public class MapFieldWithAnnotationTest extends MultipleSepTargetInProcessTest {

    public MapFieldWithAnnotationTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void namedParamsOfSameType() {
        sep(c -> c.addNode(new MyHandler("NY", "greg", "USA", "Smith", "YES")));
        Assert.assertNotNull(getField("gregSmithNYUSAYES"));
    }

    @Test
    public void namedParamsOfSameTypeMixedFinalAndNonFInalFields() {
        sep(c -> c.addNode(new MyHandlerMixedFinalAndNonFinal("NY", "greg", "USA", "Smith", "YES")));
        Assert.assertNotNull(getField("gregSmithNYUSAYES"));
    }

    @Test(expected = RuntimeException.class)
    public void namedParamsInParentClass_FailNoAssignToMember() {
        sep(c -> c.addNode(new FailingChild("bill", "smith")));
    }

    @Test
    public void namedParamsInParentClass_ValidAssignToMember() {
        sep(c -> c.addNode(new ValidChild("bill", "smith")));
    }

    @Test
    public void partial_AssignToMember() {
        sep(c -> c.addNode(new PartialAssignField(null, null)));
    }

    @Test
    public void partialSubClass_AssignToMember() {
        sep(c -> c.addNode(new PartialAssignFieldSubClass(null, null)));
    }

    @Test
    public void partial_NoEventAssignToMember() {
        sep(c -> c.addNode(new PartialAgain("test", LogLevel.INFO, LogLevel.TRACE)));
    }

    public static class PartialAgain {
        private final String name;
        private final EventLogControlEvent.LogLevel audit_level_build;
        private final EventLogControlEvent.LogLevel audit_level_runtime;

        public PartialAgain(
                String name,
                @AssignToField("audit_level_build") LogLevel auditLevelBuild,
                @AssignToField("audit_level_runtime") LogLevel auditLevelRuntime) {
            this.name = name;
            this.audit_level_build = auditLevelBuild;
            this.audit_level_runtime = auditLevelRuntime;
        }
    }

    public static class PartialAssignField {

        @Inject
        private final DirtyStateMonitor dirtyStateMonitor;
        @Inject
        private final EventDispatcher eventDispatcher;

        public PartialAssignField(
                @AssignToField("eventDispatcher") EventDispatcher eventDispatcher,
                @AssignToField("dirtyStateMonitor") DirtyStateMonitor dirtyStateMonitor) {
            this.eventDispatcher = eventDispatcher;
            this.dirtyStateMonitor = dirtyStateMonitor;
        }

        @OnEventHandler
        public boolean stringUpdate(String in) {
            return true;
        }
    }

    public static class PartialAssignFieldSubClass extends PartialAssignField {

        public PartialAssignFieldSubClass(
                @AssignToField("eventDispatcher") EventDispatcher eventDispatcher,
                @AssignToField("dirtyStateMonitor") DirtyStateMonitor dirtyStateMonitor) {
            super(eventDispatcher, dirtyStateMonitor);
        }
    }

    public static class MyHandler implements NamedNode {
        private final String firstName;
        private final String city;
        private final String country;
        private final String name2;
        private final String person;

        public MyHandler(
                @AssignToField("city") String arg0,
                @AssignToField("firstName") String arg1,
                @AssignToField("country") String arg2,
                @AssignToField("name2") String arg3,
                @AssignToField("person") String arg4) {
            this.city = arg0;
            this.firstName = arg1;
            this.country = arg2;
            this.name2 = arg3;
            this.person = arg4;
        }

        @OnEventHandler
        public boolean StringUpdate(String in) {
            return true;
        }

        @Override
        public String getName() {
            return firstName + name2 + city + country + person;
        }
    }

    public static class MyHandlerMixedFinalAndNonFinal implements NamedNode {
        private final String city;
        private final String country;
        private final String name2;
        private final String person;
        private String firstName;

        public MyHandlerMixedFinalAndNonFinal(
                @AssignToField("city") String arg0,
                @AssignToField("firstName") String arg1,
                @AssignToField("country") String arg2,
                @AssignToField("name2") String arg3,
                @AssignToField("person") String arg4) {
            this.city = arg0;
            this.firstName = arg1;
            this.country = arg2;
            this.name2 = arg3;
            this.person = arg4;
        }

        @OnEventHandler
        public boolean StringUpdate(String in) {
            return true;
        }

        @Override
        public String getName() {
            return firstName + name2 + city + country + person;
        }
    }

    public static class Parent {
        private final String firstName;
        private final String lastName;

        public Parent(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    public static class FailingChild extends Parent {

        public FailingChild(
                String xxxName,
                String anotherName) {
            super(xxxName, anotherName);
        }

        @OnEventHandler
        public boolean stringUpdated(String text) {
            return true;
        }
    }

    public static class ValidChild extends Parent {

        public ValidChild(
                @AssignToField("firstName") String xxxName,
                @AssignToField("lastName") String anotherName) {
            super(xxxName, anotherName);
        }

        @OnEventHandler
        public boolean stringUpdated(String text) {
            return true;
        }
    }
}
