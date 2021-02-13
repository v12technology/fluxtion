/* 
 *  Copyright (C) [2016]-[2017] V12 Technology Limited
 *  
 *  This software is subject to the terms and conditions of its EULA, defined in the
 *  file "LICENCE.txt" and distributed with this software. All information contained
 *  herein is, and remains the property of V12 Technology Limited and its licensors, 
 *  if any. This source code may be protected by patents and patents pending and is 
 *  also protected by trade secret and copyright law. Dissemination or reproduction 
 *  of this material is strictly forbidden unless prior written permission is 
 *  obtained from V12 Technology Limited.  
 */
package com.fluxtion.ext.declarative.builder.event;

import com.fluxtion.ext.declarative.builder.helpers.DataEvent;
import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.builder.factory.EventSelect;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.factory.StreamFunctionsLibrary.cumSum;
import com.fluxtion.generator.targets.JavaTestGeneratorHelper;
import lombok.Data;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class EventSelectTest extends StreamInprocessTest {

    @Test
    public void testSimpleSelect() throws Exception {
        JavaTestGeneratorHelper.setupDefaultTestContext("com.fluxtion.ext.declarative.builder.event", "EventSelectTest");
        Wrapper<DataEvent> dataHandler = EventSelect.select(DataEvent.class);
        Wrapper<DataEvent> dataHandler_1 = EventSelect.select(DataEvent.class);
        assertEquals(dataHandler, dataHandler_1);

        Wrapper<DataEvent> dataHandler_int = EventSelect.select(DataEvent.class, 200);
        Wrapper<DataEvent> dataHandler_int2 = EventSelect.select(DataEvent.class, 200);
        Wrapper<DataEvent> dataHandler_int3 = EventSelect.select(DataEvent.class, 5454);
        assertEquals(dataHandler_int, dataHandler_int2);
        assertNotEquals(dataHandler_int, dataHandler_int3);
        assertNotEquals(dataHandler, dataHandler_int2);

        Wrapper<DataEvent> dataHandler_String = EventSelect.select(DataEvent.class, "Hello");
        Wrapper<DataEvent> dataHandler_String_1 = EventSelect.select(DataEvent.class, "Hello");
        Wrapper<DataEvent> dataHandler_String_bye = EventSelect.select(DataEvent.class, "bye");
        assertEquals(dataHandler_String, dataHandler_String_1);
        assertNotEquals(dataHandler_String, dataHandler_String_bye);
        assertNotEquals(dataHandler, dataHandler_String);
        assertNotEquals(dataHandler_int, dataHandler_String);

        assertEquals(dataHandler.eventClass(), DataEvent.class);
        assertEquals(dataHandler_int.eventClass(), DataEvent.class);
        assertEquals(dataHandler_String.eventClass(), DataEvent.class);
    }

    @Test
    public void testNonFluxtionEvent() {
        JavaTestGeneratorHelper.setupDefaultTestContext("com.fluxtion.ext.declarative.builder.event", "EventSelectTest");
        Wrapper<DataNonFluxtion> data_1 = EventSelect.select(DataNonFluxtion.class);
        Wrapper<DataNonFluxtion> data_1_copy = EventSelect.select(DataNonFluxtion.class);
        Wrapper<Data2NonFluxtion> data_2 = EventSelect.select(Data2NonFluxtion.class);
        Assert.assertEquals(data_1, data_1_copy);
        Assert.assertNotEquals(data_1, data_2);
        DataNonFluxtion data = new DataNonFluxtion(10);
    }

    @Test
    public void streamNonFluxtionEvent() {
        sep((cfg) -> {
            Wrapper<DataNonFluxtion> data_1 = select(DataNonFluxtion.class);
            Wrapper<DataNonFluxtion> data_1_copy = select(DataNonFluxtion.class);
            Wrapper<Data2NonFluxtion> data_2 = EventSelect.select(Data2NonFluxtion.class);
            data_1_copy.map(cumSum(), DataNonFluxtion::getValue).id("cumSum1");
            data_1.map(cumSum(), DataNonFluxtion::getValue).id("cumSum1_copy");
            data_2.map(cumSum(), Data2NonFluxtion::getValue).id("cumSum2");
        });//, "com.test.select1.SelectProcessor");

        Number cumSum1 = getWrappedField("cumSum1");
        Number cumSum1_copy = getWrappedField("cumSum1_copy");
        Number cumSum2 = getWrappedField("cumSum2");

        assertEquals(0, cumSum1.intValue());
        assertEquals(0, cumSum1_copy.intValue());
        assertEquals(0, cumSum2.intValue());

        onEvent(new Data2NonFluxtion(2));
        assertEquals(2, cumSum2.intValue());
        onEvent(new Data2NonFluxtion(12));
        assertEquals(14, cumSum2.intValue());
        onEvent(new Data2NonFluxtion(6));

        assertEquals(0, cumSum1.intValue());
        assertEquals(0, cumSum1_copy.intValue());
        assertEquals(20, cumSum2.intValue());

    }
    
    @Test
    public void testFilterProperty(){
        sep((cfg) ->{
            select(DataEvent::getValue).id("dataNoFilter");
            select(DataEvent::getValue, "XXX").id("dataXXXFilter");
        });
        Number dataNoFilter = getWrappedField("dataNoFilter");
        Number dataXXXFilter = getWrappedField("dataXXXFilter");
        assertEquals(0, dataNoFilter.intValue());
        assertEquals(0, dataXXXFilter.intValue());
        onEvent(new DataEvent(2));
        assertEquals(2, dataNoFilter.intValue());
        assertEquals(0, dataXXXFilter.intValue());
        dataEvent = new DataEvent(12);
        dataEvent.setDataKey("XXX");
        onEvent(dataEvent);
        assertEquals(12, dataNoFilter.intValue());
        assertEquals(12, dataXXXFilter.intValue());
        
        
    }
    private DataEvent dataEvent;

    @Data
    public static class DataNonFluxtion {

        private final int value;

    }

    @Data
    public static class Data2NonFluxtion {

        private final int value;
    }

}
