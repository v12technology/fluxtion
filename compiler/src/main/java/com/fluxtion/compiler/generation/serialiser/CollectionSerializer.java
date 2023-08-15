package com.fluxtion.compiler.generation.serialiser;

import com.fluxtion.compiler.generation.model.Field;
import com.fluxtion.runtime.serializer.MapBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public interface CollectionSerializer {

    static String mapToSource(FieldContext<Map<?, ?>> fieldContext) {
        fieldContext.getImportList().add(MapBuilder.class);
        Map<?, ?> values = fieldContext.getInstanceToMap();
        Map<Object, Object> newMap = new HashMap<>();
        values.entrySet().stream().forEach(item -> {
            Object key = item.getKey();
            Object value = item.getValue();
            for (Field nodeField : fieldContext.getNodeFields()) {
                if (nodeField.instance.equals(key)) {
                    key = nodeField.instance;
                    break;
                }
            }
            for (Field nodeField : fieldContext.getNodeFields()) {
                if (nodeField.instance.equals(value)) {
                    value = nodeField.instance;
                    break;
                }
            }
            newMap.put(key, value);
        });
        return newMap.entrySet().stream().sorted(Comparator.comparing(e -> e.getKey().toString())).map(
                entry -> {
                    String keyString = fieldContext.mapToJavaSource(entry.getKey());
                    String valueString = fieldContext.mapToJavaSource(entry.getValue());
                    return "(" + keyString + ", " + valueString + ")";
                }
        ).collect(Collectors.joining(".put", "MapBuilder.builder().put", ".build()"));
    }

    @NotNull
    static String setToSource(FieldContext<Set<?>> fieldContext) {
        fieldContext.getImportList().add(Arrays.class);
        fieldContext.getImportList().add(HashSet.class);
        Set<?> values = fieldContext.getInstanceToMap();
        Set<Object> newList = new HashSet<>();
        values.stream().forEach(item -> {
            boolean foundMatch = false;
            for (Field nodeField : fieldContext.getNodeFields()) {
                if (nodeField.instance.equals(item)) {
                    newList.add(nodeField.instance);
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch) {
                newList.add(item);
            }
        });
        return newList.stream().map(f -> fieldContext.mapToJavaSource(f)).collect(Collectors.joining(", ", "new HashSet<>(Arrays.asList(", "))"));
    }

    @NotNull
    static String listToSource(FieldContext<List<?>> fieldContext) {
        fieldContext.getImportList().add(Arrays.class);
        List<?> values = fieldContext.getInstanceToMap();
        List<Object> newList = new ArrayList<>();
        values.stream().forEach(item -> {
            boolean foundMatch = false;
            for (Field nodeField : fieldContext.getNodeFields()) {
                if (nodeField.instance.equals(item)) {
                    newList.add(nodeField.instance);
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch) {
                newList.add(item);
            }
        });
        return newList.stream().map(f -> fieldContext.mapToJavaSource(f)).collect(Collectors.joining(", ", "Arrays.asList(", ")"));
    }
}
