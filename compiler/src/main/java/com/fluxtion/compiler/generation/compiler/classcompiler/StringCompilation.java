package com.fluxtion.compiler.generation.compiler.classcompiler;

import javax.tools.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

public interface StringCompilation {


    /**
     *
     * @param source java source to compile
     * @param <T> The compiled class type
     * @return Compiled class
     * @throws URISyntaxException if fails to compile
     * @throws IOException if fails to compile
     * @throws ClassNotFoundException if fails to compile
     */
    @SuppressWarnings({"unchecked"})
    static<T>  Class<T> compile(String className, String source) throws URISyntaxException, IOException, ClassNotFoundException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        final JavaByteObject byteObject = new JavaByteObject(className);
        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(diagnostics, null, null);
        JavaFileManager fileManager = createFileManager(standardFileManager, byteObject);
        JavaCompiler.CompilationTask task = compiler.getTask(
                null, fileManager, diagnostics, null, null, Arrays.asList(new JavaStringObject(className, source))
        );

        if (!task.call()) {
            diagnostics.getDiagnostics().forEach(System.out::println);
            throw new RuntimeException("unable to compile source file to class:'" + className + "'");
        }
        fileManager.close();
        final ClassLoader inMemoryClassLoader = createClassLoader(byteObject);
        return (Class<T>) inMemoryClassLoader.loadClass(className);
    }

    static JavaFileManager createFileManager(StandardJavaFileManager fileManager, JavaByteObject byteObject) {
        return new ForwardingJavaFileManager(fileManager) {
            @Override
            public JavaFileObject getJavaFileForOutput(Location location,
                                                       String className, JavaFileObject.Kind kind,
                                                       FileObject sibling) throws IOException {
                return byteObject;
            }
        };
    }

    static ClassLoader createClassLoader(final JavaByteObject byteObject) {
        return new ClassLoader() {
            @Override
            public Class<?> findClass(String name) throws ClassNotFoundException {
                byte[] bytes = byteObject.getBytes();
                return defineClass(name, bytes, 0, bytes.length);
            }
        };
    }

}