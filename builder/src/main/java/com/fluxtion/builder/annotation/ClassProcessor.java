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
package com.fluxtion.builder.annotation;

import java.io.File;
import java.net.URL;
import java.util.ServiceLoader;

/**
 * A service that processes application classes. The classes on the supplied
 * {@link URL} are generated from the application source files after
 * compilation. No libraries from Fluxtion or other sources are on this path.
 * This gives the opportunity for {@link ClassProcessor}'s to scan the path
 * and generate Fluxtion artifacts without risk of duplication. For example a
 * service may scan for a specific annotation and generate a tailored solution
 * based on the meta-data discovered during scanning.
 *
 * Fluxtion loads services using the java platform provided
 * {@link ServiceLoader} specification. Please read the {@link ServiceLoader}
 * documentation describing service registration using META-INF/services in the
 * service implementation's jar file.
 *
 * @author V12 Technology Ltd.
 */
public interface ClassProcessor {
    
    /**
     * Directories for the current generation context
     * @param rootDir - root directory of the project
     * @param output - directory for generated source outputs
     * @param resourceDir - directory for generated resource outputs
     */
    default void outputDirectories(File rootDir, File output, File resourceDir){
        
    }

    void process(URL classPath);
}
