package net.openhft.compiler;

import java.net.URL;
import java.net.URLClassLoader;
import static org.junit.Assert.fail;
import org.junit.Test;

public class RuntimeCompileTest {
    static String code = "package net.openhft.compiler;\n" +
            "public class Test implements IntConsumer {\n" +
            "    public void accept(int num) {\n" +
            "        if ((byte) num != num)\n" +
            "            throw new IllegalArgumentException();\n" +
            "    }\n" +
            "}\n";

    @Test
    public void outOfBounds() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ClassLoader cl = new URLClassLoader(new URL[0]);
        Class aClass = CompilerUtils.CACHED_COMPILER.
                loadFromJava(cl, "net.openhft.compiler.Test", code);
        IntConsumer consumer = (IntConsumer) aClass.newInstance();
        consumer.accept(1); // ok
        try {
            consumer.accept(128); // no ok
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }
}

