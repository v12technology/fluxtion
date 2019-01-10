/*
 * Copyright (C) 2019 V12 Technology Ltd.
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
package com.fluxtion.generator.compiler;

import com.fluxtion.builder.node.SEPConfig;
import java.util.function.Consumer;

/**
 * Generates and compiles a SEP in process for use by the caller in the same
 * process.
 *
 * @author V12 Technology Ltd.
 */
public class InprocessSepCompiler {

    public static void buildSep(Consumer<SEPConfig> cfgBuilder) {
        InProcessSepConfig cfgInProcess = new InProcessSepConfig(cfgBuilder);
        cfgInProcess.buildConfig();
        //clean context and build
    }

    private static class InProcessSepConfig extends SEPConfig {

        private Consumer<SEPConfig> cfg;

        public InProcessSepConfig(Consumer<SEPConfig> cfg) {
            this.cfg = cfg;
        }

        @Override
        public void buildConfig() {
            System.out.println("sending cfg");
            cfg.accept(this);
            System.out.println("receiving cfg");
        }

    }
}
