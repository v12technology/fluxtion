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
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.generator.it;

import com.fluxtion.generator.graphbuilder.NodeFactoryLocator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Greg Higgins
 */
public class NodeFactoryExampleTestIT {

    private final Logger LOG = LoggerFactory.getLogger(NodeFactoryExampleTestIT.class);

    @Test
    public void testJavaGeneration() throws Exception {
        LOG.info("registered factories:{}", NodeFactoryLocator.findCallbackByPackage("com"));
    }
}
