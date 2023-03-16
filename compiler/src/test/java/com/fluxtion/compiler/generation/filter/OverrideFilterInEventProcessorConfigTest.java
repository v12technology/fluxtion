package com.fluxtion.compiler.generation.filter;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.event.Event;
import lombok.Data;
import lombok.Value;
import org.junit.Assert;
import org.junit.Test;

public class OverrideFilterInEventProcessorConfigTest extends MultipleSepTargetInProcessTest {

    public OverrideFilterInEventProcessorConfigTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void filterOverrideDynamic() {
        sep(c -> {
            FilteredByInt hiltonById = c.addNode(
                    new FilteredByInt("HILTON"), "hiltonNode");
            c.overrideOnEventHandlerFilterId(hiltonById, 20);
        });

        onEvent(new MyFilterEvent("not hilton", 10));
        Assert.assertNull(getField("hiltonNode", FilteredByInt.class).getUpdatedValue());

        onEvent(new MyFilterEvent("success", 20));
        Assert.assertEquals("success", getField("hiltonNode", FilteredByInt.class).getUpdatedValue());
    }

    @Test
    public void filterOverrideSingleMethod() {
        sep(c -> {
            FilteredByInt hiltonById = c.addNode(
                    new FilteredByInt("HILTON"), "hiltonNode");
            c.overrideOnEventHandlerFilterId(hiltonById, MyFilterEvent.class, 20);
        });

        onEvent(new MyFilterEvent("not hilton", 10));
        FilteredByInt hiltonNode = getField("hiltonNode", FilteredByInt.class);
        Assert.assertNull(hiltonNode.getUpdatedValue());

        onEvent(new MyFilterEvent("success", 20));
        Assert.assertEquals("success", hiltonNode.getUpdatedValue());
    }

    @Test
    public void filterOverrideSingleMethod_multipleHandlersInClass() {
        sep(c -> {
            MultipleHandlers country = c.addNode(
                    new MultipleHandlers("united kingdom"), "uk");
            c.overrideOnEventHandlerFilterId(country, MyFilterEvent.class, 20);
        });

        MultipleHandlers multipleHandlers = getField("uk");
        onEvent(new MyFilterEvent("not hilton", 10));
        Assert.assertNull(multipleHandlers.getUpdatedValue());

        onEvent(new MyFilterEvent("success", 20));
        Assert.assertEquals("success", multipleHandlers.getUpdatedValue());

        onEvent(new CountryEvent("country-success", 360));
        Assert.assertEquals("country-success", multipleHandlers.getUpdatedValue());
    }

    @Test
    public void filterOverrideMultipleMethod_multipleHandlersInClass() {
        sep(c -> {
            MultipleHandlers country = c.addNode(
                    new MultipleHandlers("united kingdom"), "uk");
            c.overrideOnEventHandlerFilterId(country, MyFilterEvent.class, 20);
            c.overrideOnEventHandlerFilterId(country, CountryEvent.class, 360);
        });

        MultipleHandlers multipleHandlers = getField("uk");
        onEvent(new MyFilterEvent("not hilton", 10));
        Assert.assertNull(multipleHandlers.getUpdatedValue());

        onEvent(new MyFilterEvent("success", 20));
        Assert.assertEquals("success", multipleHandlers.getUpdatedValue());

        multipleHandlers.setUpdatedValue("none");

        onEvent(new CountryEvent("country-success", 55));
        Assert.assertEquals("none", multipleHandlers.getUpdatedValue());

        onEvent(new CountryEvent("country-success", 360));
        Assert.assertEquals("country-success", multipleHandlers.getUpdatedValue());
    }

    @Test
    public void filterOverrideMultipleMethod_multipleHandlersInClass_InterfaceEvent() {
        sep(c -> {
            MultipleHandlersInterfaceEvent country = c.addNode(
                    new MultipleHandlersInterfaceEvent("united kingdom"), "uk");
            c.overrideOnEventHandlerFilterId(country, DataEvent.class, 20);
            c.overrideOnEventHandlerFilterId(country, CountryEvent.class, 360);
        });

        MultipleHandlersInterfaceEvent multipleHandlers = getField("uk");
        onEvent(new MyFilterEvent("not hilton", 10));
        Assert.assertNull(multipleHandlers.getUpdatedValue());

        onEvent(new MyFilterEvent("success", 20));
        Assert.assertEquals("success", multipleHandlers.getUpdatedValue());

        multipleHandlers.setUpdatedValue("none");

        onEvent(new CountryEvent("country-success", 55));
        Assert.assertEquals("none", multipleHandlers.getUpdatedValue());

        onEvent(new CountryEvent("country-success", 360));
        Assert.assertEquals("country-success", multipleHandlers.getUpdatedValue());
    }


    @Value
    public static class MyFilterEvent implements Event, DataEvent {
        String data;
        int filterId;

        public int filterId() {
            return filterId;
        }
    }

    @Value
    public static class CountryEvent implements Event, DataEvent {
        String data;
        int filterId;

        public int filterId() {
            return filterId;
        }
    }

    @Data
    @SuppressWarnings("unused")
    public static class FilteredByInt {

        private final String humanReadableName;
        private String updatedValue;

        @OnEventHandler
        public boolean filterMe(MyFilterEvent event) {
            updatedValue = event.getData();
            return true;
        }
    }

    @Data
    @SuppressWarnings("unused")
    public static class MultipleHandlers {

        private final String humanReadableName;
        private String updatedValue;

        @OnEventHandler
        public boolean filterMe(MyFilterEvent event) {
            updatedValue = event.getData();
            return true;
        }

        @OnEventHandler
        public boolean country(CountryEvent event) {
            updatedValue = event.getData();
            return true;
        }

    }

    @Data
    @SuppressWarnings("unused")
    public static class MultipleHandlersInterfaceEvent {

        private final String humanReadableName;
        private String updatedValue;

        @OnEventHandler
        public boolean country(CountryEvent event) {
            updatedValue = event.getData();
            return true;
        }

        @OnEventHandler(ofType = MyFilterEvent.class)
        public boolean dataEvent(DataEvent event) {
            updatedValue = event.getData();
            return true;
        }
    }
}
