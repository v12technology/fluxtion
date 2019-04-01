package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.ext.streaming.builder.stream.StreamBuilder;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.generator.util.BaseSepTest;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class WrappInstanceTest extends BaseSepTest{

    
    @Test
    public void createStresm(){
        StreamBuilder sb = new StreamBuilder();
        String t1 = "T1";
        String t2 = "T2";
        Wrapper<String> stream1 = sb.stream(t1);
        Wrapper<String> stream2 = sb.stream(t1);
        Wrapper<String> streamT2 = sb.stream(t2);
        assertThat(stream1, is(stream2));
        assertThat(stream1, not(streamT2));
        Assert.assertTrue(stream1 == stream2);
        Assert.assertTrue(stream1 != streamT2);
    }
    
}
