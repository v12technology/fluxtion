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
package com.fluxtion.ext.futext.builder.util;

import com.fluxtion.api.generation.GenerationContext;
import com.fluxtion.api.node.SEPConfig;
import com.fluxtion.ext.futext.api.ascii.Csv2ByteBuffer;
import com.fluxtion.ext.futext.api.ascii.Csv2ByteBufferTemp;
import com.fluxtion.ext.futext.api.util.marshaller.CsvMultiTypeMarshaller;
import com.fluxtion.ext.futext.builder.ascii.AsciiHelper;

/**
 *
 * @author V12 Technology Limited
 */
public class MarshallerSepConfig extends SEPConfig {

    {

        CsvMultiTypeMarshaller multiType = addPublicNode(new CsvMultiTypeMarshaller(), "dispatcher");
//        multiType.type = AsciiHelper.readBytesCsv(0);
        multiType.type = addNode(new Csv2ByteBufferTemp(0, ",", 0));
        
        maxFiltersInline = 5;
    }
}
