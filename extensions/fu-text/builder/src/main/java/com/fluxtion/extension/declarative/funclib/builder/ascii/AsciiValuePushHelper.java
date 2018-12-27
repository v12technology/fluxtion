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
package com.fluxtion.extension.declarative.funclib.builder.ascii;

import com.fluxtion.extension.declarative.funclib.api.ascii.Ascii2DoubleTerminator;
import com.fluxtion.extension.declarative.funclib.api.ascii.Ascii2IntTerminator;
import java.util.function.BiConsumer;
import com.fluxtion.extension.declarative.builder.factory.NumericValuePushFactory;

/**
 *
 * @author Greg Higgins
 */
public class AsciiValuePushHelper {
    
    public static <T> void setNumeric(String searchFilter, T targetInstance, BiConsumer<T, ? super Byte> targetSetMethod) throws Exception{
        Ascii2IntTerminator asciiIntValue = AsciiHelper.readInt(searchFilter);
        NumericValuePushFactory.setNumeric(asciiIntValue, targetInstance, targetSetMethod);
    }
    
    public static <T> void setByte(String searchFilter, T targetInstance, BiConsumer<T, ? super Byte> targetSetMethod) throws Exception{
        setNumeric(searchFilter, targetInstance, targetSetMethod);
    }
    
    public static <T> void setChar(String searchFilter, T targetInstance, BiConsumer<T, ? super Character> targetSetMethod) throws Exception{
        Ascii2IntTerminator asciiIntValue = AsciiHelper.readInt(searchFilter);
        NumericValuePushFactory.setChar(asciiIntValue, targetInstance, targetSetMethod);
    }
    
    public static <T> void setShort(String searchFilter, T targetInstance, BiConsumer<T, ? super Byte> targetSetMethod) throws Exception{
        setNumeric(searchFilter, targetInstance, targetSetMethod);
    }
    
    public static <T> void setInt(String searchFilter, T targetInstance, BiConsumer<T, ? super Byte> targetSetMethod) throws Exception{
        setNumeric(searchFilter, targetInstance, targetSetMethod);
    }
    
    public static <T> void setLong(String searchFilter, T targetInstance, BiConsumer<T, ? super Byte> targetSetMethod) throws Exception{
        setNumeric(searchFilter, targetInstance, targetSetMethod);
    }
    
    public static <T> void setFloat(String searchFilter, T targetInstance, BiConsumer<T, ? super Byte> targetSetMethod) throws Exception{
        Ascii2DoubleTerminator asciiIntValue = AsciiHelper.readDouble(searchFilter);
        NumericValuePushFactory.setNumeric(asciiIntValue, targetInstance, targetSetMethod);
    }
        
    public static <T> void setDouble(String searchFilter, T targetInstance, BiConsumer<T, ? super Byte> targetSetMethod) throws Exception{
        Ascii2DoubleTerminator asciiIntValue = AsciiHelper.readDouble(searchFilter);
        NumericValuePushFactory.setNumeric(asciiIntValue, targetInstance, targetSetMethod);
    }

}
