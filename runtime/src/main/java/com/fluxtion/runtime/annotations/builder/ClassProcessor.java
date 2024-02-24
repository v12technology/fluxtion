/*
 * Copyright (C) 2019 2024 gregory higgins.
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
package com.fluxtion.runtime.annotations.builder;

import java.io.File;
import java.net.URL;
import java.util.ServiceLoader;

/**
 * A ClassProcessor service can inspect and process application classes after
 * they are compiled. The callback {@link #process(java.net.URL) } points to the
 * compiled application classes. No external libraries are on the process URL,
 * solely the output of compiling application source files.<p>
 * <p>
 * This gives the opportunity for {@link ClassProcessor}'s to scan the path and
 * generate artifacts without risk of confusing library and application classes.
 * For example a service may scan for a specific annotation and generate a
 * tailored solution based on the meta-data discovered during scanning.<p>
 *
 * <h2>Registering ClassProcessor</h2>
 * Fluxtion employs the {@link ServiceLoader} pattern to register user
 * implemented NodeFactories. Please read the java documentation describing the
 * meta-data a factory implementor must provide to register a factory using the
 * {@link ServiceLoader} pattern.
 *
 * @author 2024 gregory higgins.
 */
public interface ClassProcessor {

    /**
     * Directories for the current generation context
     *
     * @param rootDir     - root directory of the project
     * @param output      - directory for generated source outputs
     * @param resourceDir - directory for generated resource outputs
     */
    default void outputDirectories(File rootDir, File output, File resourceDir) {

    }

    /**
     * The URL of compiled application classes
     *
     * @param classPath application classes location
     */
    void process(URL classPath);
}
