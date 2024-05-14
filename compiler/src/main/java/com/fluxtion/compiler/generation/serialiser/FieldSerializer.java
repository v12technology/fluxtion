package com.fluxtion.compiler.generation.serialiser;

import com.fluxtion.compiler.EventProcessorConfig;
import com.fluxtion.compiler.generation.GenerationContext;
import com.fluxtion.compiler.generation.model.Field;
import com.fluxtion.compiler.generation.util.ClassUtils;
import com.fluxtion.runtime.dataflow.groupby.GroupByKey;
import com.fluxtion.runtime.dataflow.groupby.MultiJoin;
import com.fluxtion.runtime.dataflow.helpers.GroupingFactory;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Loads  FieldToSourceSerializer using
 * <ul>
 *     <li>The {@link ServiceLoader} support provided by Java platform. New factories can be added to Fluxtion using the
 *     extension mechanism described in {@link ServiceLoader} documentation.</li>
 *     <li>Registered programmatically with {@link EventProcessorConfig#addClassSerializer(Class, Function)}</li>
 * </ul>
 */
public class FieldSerializer implements MapFieldToJavaSource {

    private final Map<Class<?>, Function<FieldContext, String>> classSerializerMap = new HashMap<>();
    private final List<FieldToSourceSerializer> serviceLoadedFieldSerializerList;

    public FieldSerializer(EventProcessorConfig config) {
        if (config != null) {
            classSerializerMap.putAll(config.getClassSerializerMap());
        }
        ServiceLoader<FieldToSourceSerializer> loadServices;
        serviceLoadedFieldSerializerList = new ArrayList<>();
        if (GenerationContext.SINGLETON != null && GenerationContext.SINGLETON.getClassLoader() != null) {
            loadServices = ServiceLoader.load(FieldToSourceSerializer.class, GenerationContext.SINGLETON.getClassLoader());
        } else {
            loadServices = ServiceLoader.load(FieldToSourceSerializer.class);
        }
        loadServices.forEach(serviceLoadedFieldSerializerList::add);
    }

    private static boolean _nativeTypeSupported(Class<?> type) {
        return type.isEnum() || type.isArray();
    }

    public boolean typeSupported(Class<?> type) {
        return classSerializerMap.containsKey(type)
                || _nativeTypeSupported(type)
                || Arrays.stream(type.getDeclaredConstructors()).anyMatch(c -> c.getParameterCount() == 0)
                || classSerializerMap.keySet().stream().anyMatch(c -> c.isAssignableFrom(type))
                || serviceLoadedFieldSerializerList.stream().anyMatch(s -> s.typeSupported(type));
    }

    @Override
    public String mapToJavaSource(Object primitiveVal, List<Field> nodeFields, Set<Class<?>> importList) {
        Class<?> primitiveValClass = primitiveVal.getClass();
        FieldContext f = new FieldContext(primitiveVal, nodeFields, importList, this);
        Function<FieldContext, String> serializeFunction = classSerializerMap.get(primitiveValClass);
        if (serializeFunction != null) {
            return serializeFunction.apply(f);
        }
        Optional<Class<?>> matchingClass = classSerializerMap.keySet().stream().filter(clazz -> clazz.isAssignableFrom(primitiveValClass)).findFirst();
        if (matchingClass.isPresent()) {
            return classSerializerMap.get(matchingClass.get()).apply(f);
        }
        if (_nativeTypeSupported(primitiveValClass)) {
            return _mapToJavaSource(primitiveVal, nodeFields, importList);
        }
        Optional<String> optionalSerialise = serviceLoadedFieldSerializerList.stream().filter(s -> s.typeSupported(primitiveValClass)).findFirst().map(m -> m.mapToSource(f));
        if (optionalSerialise.isPresent()) {
            return optionalSerialise.get();
        }
        return _mapToJavaSource(primitiveVal, nodeFields, importList);
    }

    public boolean propertySupported(PropertyDescriptor property, Field field, List<Field> nodeFields) {
        return _propertySupported(property, field, nodeFields);
    }

    public String mapPropertyToJavaSource(PropertyDescriptor property, Field field, List<Field> nodeFields, Set<Class<?>> importList) {
        return _mapPropertyToJavaSource(property, field, nodeFields, importList);
    }

    private String _mapToJavaSource(Object primitiveVal, List<Field> nodeFields, Set<Class<?>> importList) {
        Class<?> clazz = primitiveVal.getClass();
        Object original = primitiveVal;
        boolean foundMatch = false;
        if (clazz.isArray()) {
            primitiveVal = serializeArray(primitiveVal, nodeFields, importList, clazz);
        } else if (clazz.isEnum()) {
            primitiveVal = clazz.getSimpleName() + "." + ((Enum<?>) primitiveVal).name();
            importList.add(clazz);
        }
        for (Field nodeField : nodeFields) {
            if (nodeField.instance == primitiveVal) {
                primitiveVal = nodeField.name;
                foundMatch = true;
                break;
            }
        }
        if (!foundMatch && original == primitiveVal && clazz.getCanonicalName() != null) {
            importList.add(clazz);
            primitiveVal = "new " + (clazz).getSimpleName() + "()";
        }
        return primitiveVal.toString();
    }

    @NotNull
    private Object serializeArray(Object primitiveVal, List<Field> nodeFields, Set<Class<?>> importList, Class<?> clazz) {
        Class<?> arrayType = clazz.getComponentType();
        importList.add(arrayType);
        ArrayList<String> strings = new ArrayList<>();
        int length = Array.getLength(primitiveVal);
        for (int i = 0; i < length; i++) {
            Object arrayElement = Array.get(primitiveVal, i);
            for (Field nodeField : nodeFields) {
                if (nodeField.instance.equals(arrayElement)) {
                    arrayElement = (nodeField.instance);
                    break;
                }
            }
            strings.add(mapToJavaSource(arrayElement, nodeFields, importList));
        }
        primitiveVal = strings.stream().collect(Collectors.joining(", ", "new " + arrayType.getSimpleName() + "[]{", "}"));
        return primitiveVal;
    }

    private String _mapPropertyToJavaSource(PropertyDescriptor property, Field field, List<Field> nodeFields, Set<Class<?>> importList) {
        String ret = null;
        if (!ClassUtils.isPropertyTransient(property, field)) {
            try {
                Object value = property.getReadMethod().invoke(field.instance);
                String mappedValue = mapToJavaSource(value, nodeFields, importList);
                String writeMethod = property.getWriteMethod().getName();
                for (Field nodeField : nodeFields) {
                    if (nodeField.instance == value) {
                        mappedValue = nodeField.name;
                        break;
                    }
                }
                ret = writeMethod + "(" + mappedValue + ")";
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//            LOGGER.warn("cannot introspect bean property", e);
            }
        }
        return ret;
    }

    private boolean _propertySupported(PropertyDescriptor property, Field field, List<Field> nodeFields) {
        try {
            boolean isTransient = ClassUtils.isPropertyTransient(property, field);
            final boolean writeMethod = property.getWriteMethod() != null;
            final boolean hasValue = property.getReadMethod() != null && property.getReadMethod().invoke(field.instance) != null;
            boolean isNode = false;
            if (hasValue) {
                for (Field nodeField : nodeFields) {
                    if (nodeField.instance == property.getReadMethod().invoke(field.instance)) {
                        isNode = true;
                        break;
                    }
                }
            }
            return !isTransient && writeMethod && hasValue && (typeSupported(property.getPropertyType()) || isNode);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
//            LOGGER.warn("cannot introspect bean property", e);
        }
        return false;
    }

    //bit of a hack to get generic declarations working
    public String buildTypeDeclaration(Field field, Function<Class<?>, String> classNameConverter) {
        Object instance = field.instance;
        if (instance instanceof GroupingFactory) {
            GroupingFactory groupByKeyFactory = (GroupingFactory) instance;
            Method method = groupByKeyFactory.getKeyFunction().method();
            String returnType = classNameConverter.apply(method.getReturnType());
            String inputClass = classNameConverter.apply(method.getDeclaringClass());
            if (method.getParameterTypes().length == 1) {
                inputClass = classNameConverter.apply(method.getParameterTypes()[0]);
            }
            String genericDeclaration = "<" + inputClass + ", " + returnType + ", ?, ?>";
            return genericDeclaration;
        }
        if (instance instanceof GroupByKey) {
            GroupByKey groupByKey = (GroupByKey) instance;
            return "<" + classNameConverter.apply(groupByKey.getValueClass()) + ">";
        }
        if (instance instanceof MultiJoin.LegMapper<?>) {
            MultiJoin.LegMapper<?> multiLegJoin = (MultiJoin.LegMapper<?>) instance;
            return "<" + classNameConverter.apply(multiLegJoin.targetClass()) + ">";
        }
        return "";
    }


}
