package com.fluxtion.compiler.annotationprocessor;

import com.fluxtion.compiler.builder.imperative.DoNothingPrintStream;
import com.fluxtion.compiler.generation.compiler.classcompiler.StringCompilation;
import lombok.SneakyThrows;
import org.junit.Test;

public class ValidatingAnnotationProcessorTest {

    @SneakyThrows
    @Test(expected = RuntimeException.class)
    public void eventHandler_failCompileString_notPubicMethod() {
        String source = "    " +
                "import com.fluxtion.runtime.annotations.OnEventHandler;\n" +
                "\n" +
                "public class MyStringHandler {\n" +
                "    String in;\n" +
                "\n" +
                "    @OnEventHandler\n" +
                "    boolean stringUpdated() {\n" +
                "        this.in = in;\n" +
                "        return true;\n" +
                "    }\n" +
                "}";

        System.setErr(new DoNothingPrintStream());
        System.setOut(new DoNothingPrintStream());
        StringCompilation.compile("MyStringHandler", source);
    }

    @SneakyThrows
    @Test(expected = RuntimeException.class)
    public void eventHandler_failCompileString_missingBooleanReturn() {
        String source = "    " +
                "import com.fluxtion.runtime.annotations.OnEventHandler;\n" +
                "\n" +
                "public class MyStringHandler {\n" +
                "    String in;\n" +
                "\n" +
                "    @OnEventHandler\n" +
                "    public void stringUpdated() {\n" +
                "        this.in = in;\n" +
                "    }\n" +
                "}";

        System.setErr(new DoNothingPrintStream());
        System.setOut(new DoNothingPrintStream());
        StringCompilation.compile("MyStringHandler", source);
    }

    @SneakyThrows
    @Test(expected = RuntimeException.class)
    public void eventHandler_failCompileString_badReturnType() {
        String source = "    " +
                "import com.fluxtion.runtime.annotations.OnEventHandler;\n" +
                "\n" +
                "public class MyStringHandler {\n" +
                "    String in;\n" +
                "\n" +
                "    @OnEventHandler\n" +
                "    public boolean stringUpdated() {\n" +
                "        this.in = in;\n" +
                "        return \"test\";\n" +
                "    }\n" +
                "}";

        System.setErr(new DoNothingPrintStream());
        System.setOut(new DoNothingPrintStream());
        StringCompilation.compile("MyStringHandler", source);
    }

    @SneakyThrows
    @Test(expected = RuntimeException.class)
    public void onTrigger_failCompileString_NotPublicMethod() {
        String source = "    " +
                "import com.fluxtion.runtime.annotations.OnEventHandler;\n" +
                "import com.fluxtion.runtime.annotations.OnTrigger;\n" +
                "\n" +
                "public class MyStringHandler {\n" +
                "    String in;\n" +
                "\n" +
                "    @OnTrigger\n" +
                "    boolean stringUpdated() {\n" +
                "        this.in = in;\n" +
                "        return true;\n" +
                "    }\n" +
                "}";
        System.setErr(new DoNothingPrintStream());
        System.setOut(new DoNothingPrintStream());
        StringCompilation.compile("MyStringHandler", source);
    }

    @SneakyThrows
    @Test(expected = RuntimeException.class)
    public void onTrigger_failCompileString_missingBooleanReturn() {
        String source = "    " +
                "import com.fluxtion.runtime.annotations.OnEventHandler;\n" +
                "import com.fluxtion.runtime.annotations.OnTrigger;\n" +
                "\n" +
                "public class MyStringHandler {\n" +
                "    String in;\n" +
                "\n" +
                "    @OnTrigger\n" +
                "    public void stringUpdated() {\n" +
                "        this.in = in;\n" +
                "    }\n" +
                "}";
        System.setErr(new DoNothingPrintStream());
        System.setOut(new DoNothingPrintStream());
        StringCompilation.compile("MyStringHandler", source);
    }

    @SneakyThrows
    @Test(expected = RuntimeException.class)
    public void onTrigger_failCompileString_nonBooleanReturn() {
        String source = "    " +
                "import com.fluxtion.runtime.annotations.OnEventHandler;\n" +
                "import com.fluxtion.runtime.annotations.OnTrigger;\n" +
                "\n" +
                "public class MyStringHandler {\n" +
                "    String in;\n" +
                "\n" +
                "    @OnTrigger\n" +
                "    public String stringUpdated() {\n" +
                "        this.in = in;\n" +
                "        return \"fail\";\n" +
                "    }\n" +
                "}";
        System.setErr(new DoNothingPrintStream());
        System.setOut(new DoNothingPrintStream());
        StringCompilation.compile("MyStringHandler", source);
    }

    @SneakyThrows
    @Test(expected = RuntimeException.class)
    public void noFailCompileString_OverrideGuardBooleanReturn() {
        String source = "    " +
                "import com.fluxtion.runtime.annotations.OnEventHandler;\n" +
                "import com.fluxtion.runtime.annotations.OnTrigger;\n" +
                "\n" +
                "public class MyStringHandler {\n" +
                "    String in;\n" +
                "\n" +
                "    @OnTrigger(failBuildIfNotGuarded = false)\n" +
                "    public void stringUpdated() {\n" +
                "        this.in = in;\n" +
                "    }\n" +
                "}";
        StringCompilation.compile("MyStringHandler", source);
    }


    @SneakyThrows
    @Test(expected = RuntimeException.class)
    public void failCompileString_ExportFunction() {
        String source = "    " +
                "import com.fluxtion.runtime.annotations.OnEventHandler;\n" +
                "import com.fluxtion.runtime.annotations.ExportFunction;\n" +
                "import com.fluxtion.runtime.callback.ExportFunctionNode;\n" +
                "\n" +
                "public class MyStringHandler {\n" +
                "    String in;\n" +
                "\n" +
                "    @ExportFunction\n" +
                "    public boolean stringUpdated(String in) {\n" +
                "        this.in = in;\n" +
                "        return true;\n" +
                "    }\n" +
                "}";
        StringCompilation.compile("MyStringHandler", source);
    }

    @SneakyThrows
    @Test(expected = RuntimeException.class)
    public void failCompileString_ExportFunction_NoBooleanOrVoidReturn() {
        String source = "    " +
                "import com.fluxtion.runtime.annotations.OnEventHandler;\n" +
                "import com.fluxtion.runtime.annotations.ExportFunction;\n" +
                "import com.fluxtion.runtime.callback.ExportFunctionNode;\n" +
                "\n" +
                "public class MyStringHandler extends ExportFunctionNode{\n" +
                "    String in;\n" +
                "\n" +
                "    @ExportFunction\n" +
                "    public int stringUpdated(String in) {\n" +
                "        this.in = in;\n" +
                "        return 0;\n" +
                "    }\n" +
                "}";
        StringCompilation.compile("MyStringHandler", source);
    }

    @SneakyThrows
    public void success_ExportFunction_VoidReturn() {
        String source = "    " +
                "import com.fluxtion.runtime.annotations.OnEventHandler;\n" +
                "import com.fluxtion.runtime.annotations.ExportFunction;\n" +
                "import com.fluxtion.runtime.callback.ExportFunctionNode;\n" +
                "\n" +
                "public class MyStringHandler extends ExportFunctionNode{\n" +
                "    String in;\n" +
                "\n" +
                "    @ExportFunction\n" +
                "    public void stringUpdated(String in) {\n" +
                "        this.in = in;\n" +
                "    }\n" +
                "}";
        StringCompilation.compile("MyStringHandler", source);
    }
}
