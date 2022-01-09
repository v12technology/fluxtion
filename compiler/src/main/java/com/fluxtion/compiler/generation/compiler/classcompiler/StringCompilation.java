package com.fluxtion.compiler.generation.compiler.classcompiler;

import javax.tools.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

public class StringCompilation {

    /**
     * compile a string to a java class
     * @param className fully qualified class name
     * @throws Exception throws {@link RuntimeException} if fails to compile
     */
    @SuppressWarnings({"unchecked"})
    public static<T>  Class<T> compile(String className, String source) throws URISyntaxException, IOException, ClassNotFoundException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        final JavaByteObject byteObject = new JavaByteObject(className);
        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(diagnostics, null, null);
        JavaFileManager fileManager = createFileManager(standardFileManager, byteObject);
//        List<String> optionList = new ArrayList<>();
//        optionList.add("-classpath");
//        optionList.add(System.getProperty("java.class.path"));
        JavaCompiler.CompilationTask task = compiler.getTask(
                null, fileManager, diagnostics, null, null, Arrays.asList(new JavaStringObject(className, source))
        );

        if (!task.call()) {
            diagnostics.getDiagnostics().forEach(System.out::println);
            throw new RuntimeException("unable to compile source file to class:'" + className + "'");
        }
        fileManager.close();

        //loading and using our compiled class
        final ClassLoader inMemoryClassLoader = createClassLoader(byteObject);
        return (Class<T>) inMemoryClassLoader.loadClass(className);
//        EventProcessor processor = test.getDeclaredConstructor().newInstance();
//        processor.onEvent("hello world");
    }

    private static JavaFileManager createFileManager(StandardJavaFileManager fileManager, JavaByteObject byteObject) {
        return new ForwardingJavaFileManager(fileManager) {
            @Override
            public JavaFileObject getJavaFileForOutput(Location location,
                                                       String className, JavaFileObject.Kind kind,
                                                       FileObject sibling) throws IOException {
                return byteObject;
            }
        };
    }

    private static ClassLoader createClassLoader(final JavaByteObject byteObject) {
        return new ClassLoader() {
            @Override
            public Class<?> findClass(String name) throws ClassNotFoundException {
                //no need to search class path, we already have byte code.
                byte[] bytes = byteObject.getBytes();
                return defineClass(name, bytes, 0, bytes.length);
            }
        };
    }

}