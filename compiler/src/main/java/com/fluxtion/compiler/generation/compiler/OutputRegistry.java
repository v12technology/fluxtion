/*
 * Copyright (c) 2020, V12 Technology Ltd.
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
package com.fluxtion.compiler.generation.compiler;

import lombok.extern.java.Log;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;

/**
 * A registry that stores the output destinations for use by the
 * generation/loading processes.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log
public class OutputRegistry {

    public static final OutputRegistry INSTANCE = new OutputRegistry();
    public static final String JAVA_GEN_DIR = "target/generated-sources/fluxtion/";
    public static final String JAVA_SRC_DIR = "src/main/java/";
    public static final String JAVA_TESTGEN_DIR = "target/generated-test-sources/fluxtion/";
    public static final String JAVA_TEST_SRC_DIR = "src/test/java/";
    public static final String RESOURCE_DIR = "src/main/resources/";
    public static final String RESOURCE_TEST_DIR = "target/generated-test-sources/resources/";
    private ClassLoader classLoader;

    private DirOptions dirOptions;
    private String genDir;
    private String resDir;
    private String classesDir;

    private OutputRegistry() {
        dirOptions = DirOptions.JAVA_GENDIR_OUTPUT;
        update();
    }

    public final void update() {
        String dir = System.getProperty("fluxtion.cacheDirectory");
        log.log(Level.INFO, "upate fluxtion.cacheDirectory:'"+ dir + "'");
        genDir = OutputRegistry.JAVA_GEN_DIR;
        resDir = OutputRegistry.RESOURCE_DIR;
        classLoader = ClassLoader.getSystemClassLoader();
        classesDir = null;
        System.getProperties().remove("fluxtion.build.outputdirectory");
        if (dir != null) {
            dirOptions = null;
            genDir = dir + "/source/";
            resDir = dir + "/resources/";
            classesDir = dir + "/classes/";
            System.setProperty("fluxtion.build.outputdirectory", classesDir);
            try {
                final URL classesDir = getClassesDirFile().toURI().toURL();
                final URL reosurcesDir = getResDirFile().toURI().toURL();
                //TODO valiate if we need a parent class loder or not
                classLoader = new URLClassLoader( new URL[]{classesDir, reosurcesDir});
//                classLoader = new URLClassLoader("fluxtionCompilerClassloader", new URL[]{classesDir, reosurcesDir}, null);
            } catch (Exception e) {
                log.log(Level.WARNING, "could not build classloader from cache directories", e);
            }
        } else {
            if (dirOptions == null) {
                dirOptions = DirOptions.JAVA_GENDIR_OUTPUT;
            }
            switch (dirOptions) {
                case JAVA_SRCDIR_OUTPUT:
                    genDir = OutputRegistry.JAVA_SRC_DIR;
                    resDir = OutputRegistry.RESOURCE_DIR;
                    break;
                case TEST_DIR_OUTPUT:
                    genDir = OutputRegistry.JAVA_TESTGEN_DIR;
                    resDir = OutputRegistry.RESOURCE_TEST_DIR;
            }
        }

        try {
            FileUtils.forceMkdir(getGenDirFile());
        } catch (IOException ex) {
            log.log(Level.SEVERE, "could not make generated output directory {0}", getGenDirFile());
        }
        try {
            if (getClassesDirFile() != null) {
                FileUtils.forceMkdir(getClassesDirFile());
            }
        } catch (IOException ex) {
            log.log(Level.SEVERE, "could not make classes output directory {0}", getClassesDirFile());
        }
        try {
            FileUtils.forceMkdir(getResDirFile());
        } catch (IOException ex) {
            log.log(Level.SEVERE, "could not make recources output directory {0}", getResDirFile());
        }

        log.log(Level.INFO, "updated resources registry : {0}", toString());
    }

    public DirOptions getDirOptions() {
        return dirOptions;
    }

    public void setDirOptions(DirOptions dirOptions) {
        this.dirOptions = dirOptions;
        update();
    }

    public String getGenDir() {
        return genDir;
    }

    public String getResDir() {
        return resDir;
    }

    public String getClassesDir() {
        return classesDir;
    }

    public File getGenDirFile() {
        return new File(genDir);
    }

    public File getResDirFile() {
        return new File(resDir);
    }

    public File getClassesDirFile() {
        return classesDir == null ? null : new File(classesDir);
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public String toString() {
        try {
            return "OutputRegistry{"
                    + "fluxtion.cacheDirectory=" + System.getProperty("fluxtion.cacheDirectory")
                    + ", dirOptions=" + dirOptions
                    + ", genDir=" + getGenDirFile().getCanonicalPath()
                    + ", resDir=" + getResDirFile().getCanonicalPath()
                    + ", classesDir=" + getClassesDirFile().getCanonicalPath()
                    + ", classLoader=" + classLoader
                    + '}';
        } catch (Exception ex) {
            return "OutputRegistry{"
                    + "fluxtion.cacheDirectory=" + System.getProperty("fluxtion.cacheDirectory")
                    + ", dirOptions=" + dirOptions
                    + ", genDir=" + genDir
                    + ", resDir=" + resDir
                    + ", classesDir=" + classesDir
                    + ", classLoader=" + classLoader
                    + '}';
        }
    }

}
