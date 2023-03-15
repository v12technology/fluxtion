package com.fluxtion.compiler.generation.filter;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.event.Event;
import lombok.Data;
import lombok.Value;
import org.junit.Assert;
import org.junit.Test;

public class FilterDescriptionTest extends MultipleSepTargetInProcessTest {

    public FilterDescriptionTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testFilterDynamic() {
        sep(c -> {
            FilteredByInt hiltonById = c.addNode(
                    new FilteredByInt("HILTON"), "hiltonNode");
            c.getFilterMap().put(hiltonById, 20);

        });
        onEvent(new MyFilterEvent("not hilton", 10));
        Assert.assertNull(getField("hiltonNode", FilteredByInt.class).getUpdatedValue());
        onEvent(new MyFilterEvent("success", 20));
        Assert.assertEquals("success", getField("hiltonNode", FilteredByInt.class).getUpdatedValue());
    }

    @Value
    public static class MyFilterEvent implements Event {
        String data;
        int filterId;

        public int filterId() {
            return filterId;
        }
    }

    @Data
    public static class FilteredByInt {

        private final String filterName;
        private String updatedValue;

        @OnEventHandler
        public boolean filterMe(MyFilterEvent event) {
            updatedValue = event.getData();
            return true;
        }
    }
}
