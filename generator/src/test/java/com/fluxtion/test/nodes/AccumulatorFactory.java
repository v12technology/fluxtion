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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.test.nodes;

import com.fluxtion.api.node.NodeFactory;
import com.fluxtion.api.node.NodeRegistry;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Greg Higgins
 */
public class AccumulatorFactory implements NodeFactory<Accumulator> {

    public static final String KEY_BASE = AccumulatorFactory.class.getName() + ".KEY_BASE";
    public static final String VAL_BASE_10 = AccumulatorFactory.class.getName() + ".VAL_BASE_10";
    public static final String VAL_HEX = AccumulatorFactory.class.getName() + ".VAL_HEX";
    public static final String VAL_BINARY = AccumulatorFactory.class.getName() + ".VAL_BINARY";

    private Accumulator base10;
    private static final Logger LOG = LoggerFactory.getLogger(Accumulator.class);
    private boolean createdKeys = false;
    private char[] operations = new char[]{'+','-','*','/'};
    @Override
    public Accumulator createNode(Map config, NodeRegistry registry) {
        if (base10 == null) {
            base10 = new Accumulator();
        }
        return base10;
    }

    @Override
    public void postInstanceRegistration(Map config, NodeRegistry registry, Accumulator instance) {
        LOG.info("postInstanceRegistration");
        if (!createdKeys) {
            createdKeys = true;
            int count = 10;
            for (int i = 0; i < count; i++) {
                config.put(KeyProcessorFactory.KEY_CHAR, (char) (i + '0'));
                config.put(KeyProcessorFactory.KEY_NOTIFY_ACCUM, "true");
                registry.findOrCreateNode(KeyProcessor.class, config, null);
            }
            for (char operation : operations) {
                config.put(KeyProcessorFactory.KEY_CHAR, operation);
                config.put(KeyProcessorFactory.KEY_NOTIFY_ACCUM, "true");
                registry.findOrCreateNode(KeyProcessor.class, config, null);
                
            }
        }

    }

}
