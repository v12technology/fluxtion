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

import com.fluxtion.ext.declarative.builder.log.LogBuilder;
import com.fluxtion.ext.declarative.api.log.MsgBuilder;
import com.fluxtion.api.node.SEPConfig;
import com.fluxtion.ext.declarative.api.EventWrapper;
import com.fluxtion.ext.declarative.builder.helpers.DataEvent;
import com.fluxtion.runtime.lifecycle.EventHandler;
import org.junit.Test;
import static com.fluxtion.ext.declarative.api.log.LogControlEvent.enableIdFiltering;
import static com.fluxtion.ext.declarative.api.log.LogControlEvent.enableLevelFiltering;
import static com.fluxtion.ext.declarative.api.log.LogControlEvent.recordMsgBuilderId;
import static com.fluxtion.ext.declarative.builder.event.EventSelect.select;
import com.fluxtion.generator.util.BaseSepTest;

/**
 *
 * @author greg
 */
public class ConsoleLogTest extends BaseSepTest{
    
    @Test
    public void testBuildLogger() throws Exception{
        System.out.println("testBuildLogger"); 
        EventHandler sep = buildAndInitSep(LogBuilder1.class);
        //fire some events
        DataEvent de1 = new DataEvent();
        de1.value = 2;
        sep.onEvent(de1);
        sep.onEvent(recordMsgBuilderId(true));
        de1.value = 200;
        sep.onEvent(de1);
        de1.value = 999; 
        sep.onEvent(enableLevelFiltering(1));
        de1.value = 4000000; 
        sep.onEvent(de1);
        sep.onEvent(de1);
        sep.onEvent(de1);
        de1.value = 333; 
        sep.onEvent(enableLevelFiltering(4));
        de1.value = 5; 
        sep.onEvent(de1);
        sep.onEvent(enableIdFiltering(new String[]{"fluxtion.prod"}));
        de1.value = 10; 
        sep.onEvent(de1);
        sep.onEvent(de1);
        sep.onEvent(enableIdFiltering(new String[]{"fluxtion"}));
        de1.value = 70; 
        sep.onEvent(de1);
        sep.onEvent(de1);
    }
    
    public static class LogBuilder1 extends SEPConfig{

        public LogBuilder1() {
            EventWrapper<DataEvent> dataEventSelect = select(DataEvent.class);
            MsgBuilder logger = LogBuilder.Log("DataEvent data:{} received {} ....{}"
                    , dataEventSelect, DataEvent::getValue, DataEvent::getValue, DataEvent::getValue);
            logger.name = "fluxtion.test";
            logger.logLevel = 3;
        }
        
    }
    
}
