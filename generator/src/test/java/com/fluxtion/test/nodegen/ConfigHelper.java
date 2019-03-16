/* 
 * Copyright (c) 2019, V12 Technology Ltd.
 * All rights reserved.
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
package com.fluxtion.test.nodegen;

import java.util.Map;

/**
 *
 * @author Greg Higgins
 */
public interface ConfigHelper {

    public static int intFromMap(Map<String, ?> configMap, String key, int defualtValue) {
        if (configMap.containsKey(key)) {
            try {
                String val = "" + configMap.get(key);
                defualtValue = Integer.parseInt(val);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defualtValue;
    }
}
