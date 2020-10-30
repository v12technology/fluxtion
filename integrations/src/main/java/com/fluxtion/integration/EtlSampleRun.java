/*
 * Copyright (C) 2020 V12 Technology Ltd.
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
package com.fluxtion.integration;

import lombok.Data;

/**
 *
 * @author V12 Technology Ltd.
 */
@Data
public class EtlSampleRun {

    private String pipelineId;
    private String inputData;
    private String result;
    private String result_csv;
    private String errorLog;
    private boolean processed = false;

    public String id() {
        return pipelineId.substring(0, pipelineId.indexOf(","));
    }
}
