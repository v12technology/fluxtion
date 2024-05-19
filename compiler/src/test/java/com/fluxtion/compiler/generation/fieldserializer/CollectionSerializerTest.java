package com.fluxtion.compiler.generation.fieldserializer;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.serializer.MapBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class CollectionSerializerTest extends MultipleSepTargetInProcessTest {
    public CollectionSerializerTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void collectionTest() {
        CollectionHolder collectionHolder = CollectionHolder.builder()
                .string2IdMap(MapBuilder.builder()
                        .put("id1", 1)
                        .put("id2", 2)
                        .build()
                )
                .string2IdMapFinal(MapBuilder.builder()
                        .put("idXXX", 34)
                        .put("idYYY", 112)
                        .build())
                .build();

        sep(c -> c.addNode(collectionHolder, "collectionHolder"));
        Assert.assertEquals(collectionHolder, getField("collectionHolder"));
    }

    @Data
    @Builder
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class CollectionHolder {
        private Map<String, Integer> string2IdMap;
        private final Map<String, Integer> string2IdMapFinal;
    }
}
