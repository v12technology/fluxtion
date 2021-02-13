package com.fluxtion.ext.declarative.builder.notifier;

import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import org.junit.Test;
import static com.fluxtion.ext.streaming.builder.factory.FilterBuilder.filter;
import static com.fluxtion.ext.streaming.builder.factory.StreamFunctionsLibrary.count;
import static com.fluxtion.ext.streaming.builder.factory.NotificationBuilder.notifierOverride;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author gregp
 */
public class NotifierBuilderTest extends StreamInprocessTest {

    @Test
    public void filterEvent() {
        sep(c -> {
            notifierOverride(select(Double.class), filter("tick"::equalsIgnoreCase)).id("value")
                .map(count()).id("count");
        });
        Number count = getWrappedField("count");
        onEvent("tick");
        assertThat(count.intValue(), is(0));
        onEvent(1.0);
        assertThat(count.intValue(), is(0));
        Number value = getWrappedField("value");
        assertThat(value.intValue(), is(1));
        onEvent(2.0);
        value = getWrappedField("value");
        assertThat(count.intValue(), is(0));
        assertThat(value.intValue(), is(2));
        onEvent(3.0);
        value = getWrappedField("value");
        assertThat(count.intValue(), is(0));
        assertThat(value.intValue(), is(3));
        onEvent("tick");
        assertThat(count.intValue(), is(1));
        value = getWrappedField("value");
        assertThat(value.intValue(), is(3));
        onEvent(4.0);
        assertThat(count.intValue(), is(1));
        value = getWrappedField("value");
        assertThat(value.intValue(), is(4));
    }

}
