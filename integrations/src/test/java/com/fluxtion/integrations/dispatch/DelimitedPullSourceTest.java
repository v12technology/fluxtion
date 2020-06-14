/*
 * Copyright (c) 2020, V12 Technology Ltd.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.integrations.dispatch;

import com.fluxtion.integration.eventflow.sources.DelimitedPullSource;
import com.fluxtion.integrations.dispatch.FileDispatchTest.DataEvent;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.concurrent.atomic.LongAdder;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class DelimitedPullSourceTest {

    @Test
    public void testPipedReadWrite() throws IOException {
        PipedReader reader = new PipedReader();
        PipedWriter writer = new PipedWriter(reader);
        char[] resultBuffer = new char[4096];
        assertFalse(reader.ready());

        writer.write("test");
        assertTrue(reader.ready());
        int readSize = reader.read(resultBuffer);
        assertThat(readSize, is(4));
    }

    @Test
    public void testDelimitedPullSource() throws IOException {

        String part1 = "id,name\n"
                + "1,greg\n"
                + "2,jo";

        String part2 = "sie\n";
        
        PipedReader reader = new PipedReader();
        PipedWriter writer = new PipedWriter(reader);
        assertFalse(reader.ready());
        
        LongAdder count = new LongAdder();
        DataEvent[] store = new DataEvent[10];

        DelimitedPullSource pullSource = new DelimitedPullSource(reader, new DataEventCsvDecoder0(), "push1");
        pullSource.init();
        pullSource.start((o) -> {
            store[count.intValue()] = (DataEvent) o;
            count.increment();
        });
        
        pullSource.poll();
        assertThat(count.intValue(), is(0));
        writer.write(part1);
        assertThat(count.intValue(), is(0));
        pullSource.poll();
        assertThat(count.intValue(), is(1));
        assertThat(store[0].getName(), is("greg"));
        
        writer.write(part2);
        assertThat(count.intValue(), is(1));
        pullSource.poll();
        assertThat(count.intValue(), is(2));
        assertThat(store[1].getName(), is("josie"));

    }

}
