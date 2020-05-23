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
package com.fluxtion.ext.declarative.builder.log;

import com.fluxtion.ext.declarative.builder.helpers.DataEvent;
import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.streaming.api.WrappedCollection;
import static com.fluxtion.ext.streaming.api.log.LogControlEvent.enableIdFiltering;
import static com.fluxtion.ext.streaming.api.log.LogControlEvent.enableLevelFiltering;
import static com.fluxtion.ext.streaming.api.log.LogControlEvent.recordMsgBuilderId;
import static com.fluxtion.ext.streaming.api.stream.Argument.arg;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.log.LogBuilder.log;
import com.fluxtion.junit.SystemOutResource;
import java.util.Collection;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 * @author greg
 */
public class ConsoleLogTest extends StreamInprocessTest {

    @Rule
    public SystemOutResource sysOut = new SystemOutResource();

    @Test
    public void streamLog() {
        sep(c -> {
            select(DataEvent.class).console("intValue:{} eventTime:{}", DataEvent::getValue, DataEvent::getEventTime);
            select(MyEvent.class).console("");
        });
        DataEvent de1 = new DataEvent();
        de1.value = 2;
        de1.setEventTime(300);
        sep.onEvent(de1);
        assertThat(sysOut.asString().trim(), is("intValue:2 eventTime:300"));
        
        MyEvent evt = new MyEvent();
        sysOut.clear();
        sep.onEvent(evt);
        assertThat(sysOut.asString().trim(), is("MyEvent"));
    }
    
    @Test
    public void streamToCollection(){
        sep(c ->{
            select(MyEvent.class).collect().console("size:{} collection:{}", Collection::size, Collection::toString);
        });
        MyEvent evt = new MyEvent();
        sep.onEvent(evt);
        sep.onEvent(evt);
        sep.onEvent(evt);
        sysOut.clear();
        sep.onEvent(evt);
        assertThat(sysOut.asString().trim(), is("size:4 collection:[MyEvent, MyEvent, MyEvent, MyEvent]"));
    }
    
    

    @Test
    public void testBuildLogger() throws Exception {
//        System.out.println("testBuildLogger");
        sep(c -> {
            log("DataEvent data:{} received {} ....{}",
                    arg(DataEvent::getValue),
                    arg(DataEvent::getValue),
                    arg(DataEvent::getValue)
            ).name("fluxtion.test").level(3);
        });

        //fire some events
        DataEvent de1 = new DataEvent();
        de1.value = 2;
        sep.onEvent(de1);
        assertThat(sysOut.asString(), containsString("DataEvent data:2 received 2 ....2"));
        sysOut.clear();

        sep.onEvent(recordMsgBuilderId(true));
        de1.value = 200;
        sep.onEvent(de1);
        assertThat(sysOut.asString(), containsString("DataEvent data:200 received 200 ....200"));
        sysOut.clear();

        sep.onEvent(enableLevelFiltering(1));
        de1.value = 4000000;
        sep.onEvent(de1);
        sep.onEvent(de1);
        sep.onEvent(de1);
        assertTrue(sysOut.asString().isEmpty());

        sep.onEvent(enableLevelFiltering(4));
        de1.value = 5;
        sep.onEvent(de1);
        assertThat(sysOut.asString(), containsString("DataEvent data:5 received 5 ....5"));
        sysOut.clear();

        sep.onEvent(enableIdFiltering(new String[]{"fluxtion.prod"}));
        de1.value = 10;
        sep.onEvent(de1);
        sep.onEvent(de1);
        assertTrue(sysOut.asString().isEmpty());

        sep.onEvent(enableIdFiltering(new String[]{"fluxtion"}));
        de1.value = 70;
        sep.onEvent(de1);
        assertThat(sysOut.asString(), containsString("DataEvent data:70 received 70 ....70"));
    }
    
    public static class MyEvent{

        @Override
        public String toString() {
            return "MyEvent";
        }
        
    }

}
