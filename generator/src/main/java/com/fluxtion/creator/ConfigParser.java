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
package com.fluxtion.creator;

import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author gregp
 */
public class ConfigParser {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ConfigParser.class);

    public CreatorConfig parse(String s) {
        LOG.debug("parsing creator yaml document");
        Yaml beanLoader = new Yaml();
        CreatorConfig cfg = beanLoader.loadAs(s, CreatorConfig.class);
        LOG.debug("parsed creator yaml document, now validating");
        cfg.validateConfig();
        LOG.debug("validated successfully");
        if (LOG.isDebugEnabled()) {
            LOG.debug(beanLoader.dumpAsMap(cfg));
        }
        return cfg;
    }
}
