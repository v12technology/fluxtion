package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.builder.stream.StreamOperatorService;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class WrappInstanceTest {

    
    @Test
    public void createStresm(){
        StreamOperatorService sb = new StreamOperatorService();
        String t1 = "T1";
        String t2 = "T2";
        Wrapper<String> stream1 = sb.stream(t1);
        Wrapper<String> stream2 = sb.stream(t1);
        Wrapper<String> streamT2 = sb.stream(t2);
        assertThat(stream1, is(stream2));
        assertThat(stream1, not(streamT2));
        assertTrue(stream1 == stream2);
        assertTrue(stream1 != streamT2);
    }
    
}
