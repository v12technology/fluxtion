package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.builder.stream.StreamOperatorService;
import com.fluxtion.generator.compiler.OutputRegistry;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

/**
 *
 * @author V12 Technology Ltd.
 */
public class WrapInstanceTest {

    
    @Test
    public void createStream(){
        GenerationContext.setupStaticContext("", "",
                new File(OutputRegistry.JAVA_TESTGEN_DIR),
                new File(OutputRegistry.RESOURCE_TEST_DIR));
        String t1 = "T1";
        String t2 = "T2";
        Wrapper<String> stream1 = StreamOperatorService.stream(t1);
        Wrapper<String> stream2 = StreamOperatorService.stream(t1);
        Wrapper<String> streamT2 = StreamOperatorService.stream(t2);
        assertThat(stream1, is(stream2));
        assertThat(stream1, not(streamT2));
        assertSame(stream1, stream2);
        assertNotSame(stream1, streamT2);
    }
    
}
