package com.fluxtion.runtime.dataflow.groupby;

import com.fluxtion.runtime.partition.LambdaReflection;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Constructs a compound key for using on group by constructs in a data flow. The key is composed using method references
 * of the type to be grouped by.
 *
 * @param <T> The type of data flow to create a key for
 */
@ToString(of = {"key", "name"})
public class GroupByKey<T> {
    public final List<LambdaReflection.SerializableFunction<T, ?>> accessors;
    private final transient StringBuilder keyHolder = new StringBuilder();
    @Getter
    private final transient Class<T> valueClass;
    @Getter
    private transient String key;
    private transient final String name;

    public GroupByKey(List<LambdaReflection.SerializableFunction<T, ?>> accessorsToAdd) {
        this.accessors = new ArrayList<>();
        String tmpName = "";
        for (LambdaReflection.SerializableFunction<T, ?> element : accessorsToAdd) {
            if (!accessors.contains(element)) {
                accessors.add(element);
                tmpName += "_" + element.method().getName();
            }
        }
        valueClass = (Class<T>) accessors.get(0).method().getDeclaringClass();
        name = valueClass.getName() + tmpName;
    }

    public GroupByKey(LambdaReflection.SerializableFunction<T, ?> accessor) {
        this(Arrays.asList(accessor));
    }

    @SafeVarargs
    public GroupByKey(LambdaReflection.SerializableFunction<T, ?>... accessorList) {
        this(Arrays.asList(accessorList));
    }

    private GroupByKey(GroupByKey<T> toClone) {
        accessors = toClone.accessors;
        valueClass = toClone.getValueClass();
        name = toClone.name;
    }

    public static <T> LambdaReflection.SerializableFunction<T, GroupByKey<T>> build(LambdaReflection.SerializableFunction<T, ?> accessor) {
        return new GroupByKey<>(accessor)::toKey;
    }

    @SafeVarargs
    public static <T> LambdaReflection.SerializableFunction<T, GroupByKey<T>> build(
            LambdaReflection.SerializableFunction<T, ?> accessor,
            LambdaReflection.SerializableFunction<T, ?>... accessorList) {
        List<LambdaReflection.SerializableFunction<T, ?>> accessors = new ArrayList<>();
        accessors.add(accessor);
        accessors.addAll(Arrays.asList(accessorList));
        GroupByKey<T> accessorKey = new GroupByKey<>(accessors);
        return accessorKey::toKey;
    }


    public boolean keyPresent(LambdaReflection.SerializableFunction<T, ?> keyToCheck) {
        return accessors.contains(keyToCheck);
    }

    public GroupByKey<T> toKey(T input) {
        //TODO add object pooling
        GroupByKey<T> cloned = new GroupByKey<>(this);
        cloned.keyHolder.setLength(0);
        for (int i = 0, accessorsSize = accessors.size(); i < accessorsSize; i++) {
            LambdaReflection.SerializableFunction<T, ?> accessor = accessors.get(i);
            cloned.keyHolder.append(accessor.apply(input).toString());
            cloned.keyHolder.append("_");
        }
        cloned.key = cloned.keyHolder.toString();
        return cloned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupByKey<?> that = (GroupByKey<?>) o;

        if (!valueClass.equals(that.valueClass)) return false;
        if (!Objects.equals(key, that.key)) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = valueClass.hashCode();
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + name.hashCode();
        return result;
    }

}
