package com.fluxtion.compiler.generation.compiler.classcompiler;

import com.fluxtion.compiler.generation.annotationprocessor.ValidateEventhandlerAnnotations;
import com.fluxtion.compiler.generation.annotationprocessor.ValidateLifecycleAnnotations;

import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface StringCompilation {

    static <T> Class<T> compile(String className, String source, String... options) throws URISyntaxException, IOException, ClassNotFoundException {
        List<String> optionList = new ArrayList<>();
        Collections.addAll(optionList, options);
        return compile(className, source, optionList);
    }

    /**
     * @param source java source to compile
     * @param <T>    The compiled class type
     * @return Compiled class
     * @throws URISyntaxException     if fails to compile
     * @throws IOException            if fails to compile
     * @throws ClassNotFoundException if fails to compile
     */
    @SuppressWarnings({"unchecked"})
    static <T> Class<T> compile(String className, String source, List<String> optionList) throws URISyntaxException, IOException, ClassNotFoundException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        final JavaByteObject byteObject = new JavaByteObject(className);
        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(diagnostics, null, null);
        JavaFileManager fileManager = createFileManager(standardFileManager, byteObject);
        JavaCompiler.CompilationTask task = compiler.getTask(
                null, fileManager, diagnostics, optionList, null, Collections.singletonList(new JavaStringObject(className, source))
        );
        task.setProcessors(Arrays.asList(new ValidateEventhandlerAnnotations(), new ValidateLifecycleAnnotations()));
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