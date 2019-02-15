package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.ext.declarative.api.Wrapper;
import static com.fluxtion.ext.declarative.builder.event.EventSelect.select;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class StreamPushTest extends BaseSepInprocessTest {

    @Test
    public void mapRef2Ref() {
//        fixedPkg = true;
        sep((c) -> {
            Wrapper<StreamData> in = select(StreamData.class);
            PushTarget target = c.addNode(new PushTarget(), "target");
            in.filter(StreamData::getIntValue, new FilterFunctions()::positive).id("data")
                    .push(StreamData::getIntValue, target::setVal);
        });
        PushTarget target = getField("target");
        onEvent(new StreamData(89));
        assertThat(target.val, is(890));
        assertThat(target.count, is(1));
    }

}
