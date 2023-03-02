package com.fluxtion.compiler.generation.constructor;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.node.NamedNode;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import org.junit.Assert;
import org.junit.Test;

public class MapFieldWithAnnotationTest extends MultipleSepTargetInProcessTest {

    public MapFieldWithAnnotationTest(boolean compiledSep) {
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
        private String firstName;
        private final String city;
        private final String country;
        private final String name2;
        private final String person;

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
}
