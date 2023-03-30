package com.fluxtion.compiler.generation.customfieldserializer;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.junit.Test;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class IoSerializerTest extends MultipleSepTargetInProcessTest {
    public IoSerializerTest(SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void testIoSerializersConstructor() {
        sep(c -> {
            try {
                c.addNode(IoHolder.builder()
                        .file(new File("c:\\my_made_up\\path"))
                        .url(new URL("http://www.example.com/docs/resource1.html"))
                        .uri(new URI("http://www.example.com/docs/resource2.html"))
                        .inetSocketAddress(InetSocketAddress.createUnresolved("localhost", 2020)).build());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void testIoSerializersProperty() {
        sep(c -> {

            try {
                c.addNode(IoHolderProperty.builder()
                        .file(new File("c:\\my_made_up\\path"))
                        .url(new URL("http://www.example.com/docs/resource1.html"))
                        .uri(new URI("http://www.example.com/docs/resource2.html"))
                        .inetSocketAddress(InetSocketAddress.createUnresolved("localhost", 2020)).build());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
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


}
