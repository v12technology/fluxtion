/*
 * Copyright 2014 Higher Frequency Trading
 *
 * http://www.higherfrequencytrading.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.openhft.compiler;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;
import javax.tools.*;
import javax.tools.JavaFileObject.Kind;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
class MyJavaFileManager implements JavaFileManager {

    /**
     * WARNING: Illegal reflective access by
     * net.openhft.compiler.MyJavaFileManager
     * (file:/C:/Users/gregp/development/projects/fluxtion/open-source/fluxtion/builder/target/classes/)
     * to method
     * com.sun.tools.javac.file.JavacFileManager.listLocationsForModules(javax.tools.JavaFileManager$Location)
     *
     */
    private final StandardJavaFileManager fileManager;

    // synchronizing due to ConcurrentModificationException
    private final Map<String, ByteArrayOutputStream> buffers = Collections.synchronizedMap(new LinkedHashMap<>());

    MyJavaFileManager(StandardJavaFileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public Iterable<Set<Location>> listLocationsForModules(final Location location) throws IOException {
        return fileManager.listLocationsForModules(location);
    }

    @Override
    public String inferModuleName(final Location location) throws IOException {
        return fileManager.inferModuleName(location);
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return fileManager.getClassLoader(location);
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException {
        return fileManager.list(location, packageName, kinds, recurse);
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        return fileManager.inferBinaryName(location, file);
    }

    @Override
    public boolean isSameFile(FileObject a, FileObject b) {
        return fileManager.isSameFile(a, b);
    }

    @Override
    public boolean handleOption(String current, Iterator<String> remaining) {
        return fileManager.handleOption(current, remaining);
    }

    @Override
    public boolean hasLocation(Location location) {
        return fileManager.hasLocation(location);
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind) throws IOException {
        log.trace("getJavaFileForInput location:{} className:{}", location, className);
        if (location == StandardLocation.CLASS_OUTPUT) {
            boolean success = false;
            final byte[] bytes;
            synchronized (buffers) {
                success = buffers.containsKey(className) && kind == Kind.CLASS;
                bytes = buffers.get(className).toByteArray();
            }
            if (success) {

                return new SimpleJavaFileObject(URI.create(className), kind) {
                    @NotNull
                    @Override
                    public InputStream openInputStream() {
                        return new ByteArrayInputStream(bytes);
                    }
                };
            }
        }
        log.trace("end getJavaFileForInput");
        final JavaFileObject javaFileForInput = fileManager.getJavaFileForInput(location, className, kind);
        return javaFileForInput;
    }

    @NotNull
    @Override
    public JavaFileObject getJavaFileForOutput(Location location, final String className, Kind kind, FileObject sibling) {
        log.trace("getJavaFileForOutput location:{} className:{}", location, className);
        final SimpleJavaFileObject simpleJavaFileObject = new SimpleJavaFileObject(URI.create(className), kind) {
            @NotNull
            @Override
            public OutputStream openOutputStream() {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                buffers.put(className, baos);
                return baos;
            }
        };
        log.trace("end getJavaFileForOutput");
        return simpleJavaFileObject;
    }

    @Override
    public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
        return fileManager.getFileForInput(location, packageName, relativeName);
    }

    @Override
    public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
        return fileManager.getFileForOutput(location, packageName, relativeName, sibling);
    }

    @Override
    public void flush() {
        // Do nothing
    }

    @Override
    public void close() throws IOException {
        fileManager.close();
    }

    @Override
    public int isSupportedOption(String option) {
        return fileManager.isSupportedOption(option);
    }

    public void clearBuffers() {
        buffers.clear();
    }

    @NotNull
    public Map<String, byte[]> getAllBuffers() {
        synchronized (buffers) {
            Map<String, byte[]> ret = new LinkedHashMap<>(buffers.size() * 2);
            for (Map.Entry<String, ByteArrayOutputStream> entry : buffers.entrySet()) {
                ret.put(entry.getKey(), entry.getValue().toByteArray());
            }
            return ret;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T invokeNamedMethodIfAvailable(final Location location, final String name) {
        final Method[] methods = fileManager.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(name) && method.getParameterTypes().length == 1
                    && method.getParameterTypes()[0] == Location.class) {
                try {
//                    unsafe.putBoolean(method, OVERRIDE_OFFSET, true);
                    return (T) method.invoke(fileManager, location);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new UnsupportedOperationException("Unable to invoke method " + name);
                }
            }
        }
        throw new UnsupportedOperationException("Unable to find method " + name);
    }
}
