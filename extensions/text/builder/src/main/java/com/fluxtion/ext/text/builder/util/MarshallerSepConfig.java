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
package com.fluxtion.ext.text.builder.util;

import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.ext.text.api.ascii.Csv2ByteBufferTemp;
import com.fluxtion.ext.text.api.util.marshaller.CsvMultiTypeMarshaller;

/**
 *
 * @author V12 Technology Limited
 */
public class MarshallerSepConfig extends SEPConfig {

    {
        CsvMultiTypeMarshaller multiType = addPublicNode(new CsvMultiTypeMarshaller(), "dispatcher");
        multiType.type = addNode(new Csv2ByteBufferTemp(0, ",", 0));
        maxFiltersInline = 5;
    }
}
