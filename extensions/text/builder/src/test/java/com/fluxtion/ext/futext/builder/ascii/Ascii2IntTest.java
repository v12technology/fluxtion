/* 
 * Copyright (C) 2018 V12 Technology Ltd.
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
package com.fluxtion.ext.futext.builder.ascii;

import com.fluxtion.ext.text.api.ascii.Ascii2IntFixedLength;
import com.fluxtion.ext.text.api.event.CharEvent;
import com.fluxtion.ext.text.builder.util.StringDriver;
import com.fluxtion.api.event.Event;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Greg Higgins
 */
public class Ascii2IntTest {

    Object notifier;//new Object();
    String filter = "";

    @Test
    public void applyFilter() {
        System.out.println("applyFilter");
        Ascii2IntFixedLength instance = new Ascii2IntFixedLength(notifier, (byte) 4, filter);
        instance.applyFilter(null);
        StringDriver.streamChars("test number=200", (event) -> {
            if (event instanceof CharEvent) {
                instance.on_2((CharEvent) event);
                instance.afterEvent();
            }
        });
        assertEquals(0, instance.intValue());
    }

    @Test
    public void matched() {
        System.out.println("matched");
        Ascii2IntFixedLength instance = new Ascii2IntFixedLength(notifier, (byte) 4, filter);
        instance.filterMatched(null);
        StringDriver.streamChars("test number=200", (event) -> {
            if (event instanceof CharEvent) {
                instance.on_2((CharEvent) event);
                instance.afterEvent();
            }
        });
        assertEquals(0, instance.intValue());
    }

    @Test
    public void goodIntParse() {
        System.out.println("goodIntParse");
        Ascii2IntFixedLength instance = new Ascii2IntFixedLength(notifier, (byte) 4, filter);

        StreamString(instance, "2123");
        assertEquals(2123, instance.intValue());
    }

    @Test
    public void goodIntParseIgnoreExtra() {
        System.out.println("goodIntParseIgnoreExtra");
        Ascii2IntFixedLength instance = new Ascii2IntFixedLength(notifier, (byte) 4, filter);

        StreamString(instance, "212324747");
        assertEquals(2123, instance.intValue());
    }

    @Test
    public void multipleGoodIntParseIgnoreExtra() {
        System.out.println("multipleGoodIntParseIgnoreExtra");
        Ascii2IntFixedLength instance = new Ascii2IntFixedLength(notifier, (byte) 4, filter);

        StreamString(instance, "212324747");
        assertEquals(2123, instance.intValue());

        StreamString(instance, "6987");
        assertEquals(6987, instance.intValue());
    }

    private void StreamString(Ascii2IntFixedLength instance, String input) {
        instance.init();
        instance.applyFilter(null);
        instance.filterMatched(null);
        instance.afterEvent();
        StringDriver.streamChars(input, (event) -> {
            if (event instanceof CharEvent) {
                CharEvent charEvent = (CharEvent) event;
                switch (charEvent.getCharacter()) {
                    case ('0'):
                        instance.on_0(charEvent);
                        break;
                    case ('1'):
                        instance.on_1(charEvent);
                        break;
                    case ('2'):
                        instance.on_2(charEvent);
                        break;
                    case ('3'):
                        instance.on_3(charEvent);
                        break;
                    case ('4'):
                        instance.on_4(charEvent);
                        break;
                    case ('5'):
                        instance.on_5(charEvent);
                        break;
                    case ('6'):
                        instance.on_6(charEvent);
                        break;
                    case ('7'):
                        instance.on_7(charEvent);
                        break;
                    case ('8'):
                        instance.on_8(charEvent);
                        break;
                    case ('9'):
                        instance.on_9(charEvent);
                        break;
                    case ('-'):
                        instance.onSign(charEvent);
                        break;
                }
            }

//            instance.onChar((CharEvent) event);
            instance.afterEvent();
        });
    }

}
