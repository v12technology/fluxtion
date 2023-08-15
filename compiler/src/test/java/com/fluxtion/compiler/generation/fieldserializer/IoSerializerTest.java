package com.fluxtion.compiler.generation.fieldserializer;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import lombok.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;

public class IoSerializerTest extends MultipleSepTargetInProcessTest {
    public IoSerializerTest(SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    @SneakyThrows
    public void testIoSerializersConstructor() {
//        writeSourceFile = true;
        IoHolder ioData = IoHolder.builder()
                .file(new File("c:\\my_made_up\\path"))
                .url(new URL("http://www.example.com/docs/resource1.html"))
                .uri(new URI("http://www.example.com/docs/resource2.html"))
                .inetSocketAddress(InetSocketAddress.createUnresolved("localhost", 2020)).build();
        sep(c -> c.addNode(ioData, "ioData"));
        Assert.assertEquals(ioData, getField("ioData"));
    }

    @Test
    @SneakyThrows
    public void testIoSerializersProperty() {
        IoHolderProperty ioData = IoHolderProperty.builder()
                .file(new File("c:\\my_made_up\\path"))
                .url(new URL("http://www.example.com/docs/resource1.html"))
                .uri(new URI("http://www.example.com/docs/resource2.html"))
                .inetSocketAddress(InetSocketAddress.createUnresolved("localhost", 2020)).build();
        sep(c -> c.addNode(ioData, "ioData"));
        Assert.assertEquals(ioData, getField("ioData"));
    }

    @Test
    @SneakyThrows
    public void serializeFieldTest() {
        IoHolderFieldProperty ioData = new IoHolderFieldProperty();
        ioData.setFile(new File("c:\\my_made_up\\path"));
        ioData.setUrl(new URL("http://www.example.com/docs/resource1.html"));
        sep(c -> c.addNode(ioData, "ioData"));
        Assert.assertEquals(ioData, getField("ioData"));
    }


    @Builder
    @AllArgsConstructor
    @Value
    public static class IoHolder {
        File file;
        URL url;
        URI uri;
        InetSocketAddress inetSocketAddress;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class IoHolderProperty {
        File file;
        URL url;
        URI uri;
        InetSocketAddress inetSocketAddress;
    }


    @Data
    public static class IoHolderFieldProperty {
        File file;
        URL url;
        URI uri;
        InetSocketAddress inetSocketAddress;
    }


}
