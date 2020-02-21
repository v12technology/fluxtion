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
package net.openhft.compiler;

/**
 * A ClassLoader that exposes the defineClass method from ClassLoader base class
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class MyClassLoader extends ClassLoader{

    private static int count;
    private String myName;

    public MyClassLoader(ClassLoader parent) {
        super(parent);
        myName = "Fluxtion-classloader" + count;
    }
  
    Class<?> defineClassOverride(String className, byte[] b, int off, int len) throws ClassFormatError
    {
        return defineClass(className, b, off, len, null);
    }

    public String getName() {
        return myName;
    }

}
