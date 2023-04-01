package com.fluxtion.compiler.generation.serialiser;

import com.fluxtion.compiler.EventProcessorConfig;
import com.fluxtion.compiler.generation.GenerationContext;
import com.fluxtion.compiler.generation.model.Field;
import com.fluxtion.compiler.generation.util.ClassUtils;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.function.MergeProperty;
import com.fluxtion.runtime.dataflow.helpers.GroupingFactory;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.partition.LambdaReflection.MethodReferenceReflection;
import org.apache.commons.lang3.StringEscapeUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
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
public class FieldSerializer {

    private final Map<Class<?>, Function<FieldContext, String>> classSerializerMap = new HashMap<>();
    private final List<FieldToSourceSerializer> namingStrategies;

    public FieldSerializer(EventProcessorConfig config) {
        if (config != null) {
            classSerializerMap.putAll(config.getClassSerializerMap());
        }
        ServiceLoader<FieldToSourceSerializer> loadServices;
        namingStrategies = new ArrayList<>();
        if (GenerationContext.SINGLETON != null && GenerationContext.SINGLETON.getClassLoader() != null) {
            loadServices = ServiceLoader.load(FieldToSourceSerializer.class, GenerationContext.SINGLETON.getClassLoader());
        } else {
            loadServices = ServiceLoader.load(FieldToSourceSerializer.class);
        }
        loadServices.forEach(namingStrategies::add);
    }

    private static boolean _nativeSerializerSupport(Class<?> type) {
        boolean zeroArg = false;
        try {
            type.getDeclaredConstructor();
            zeroArg = true;
        } catch (NoSuchMethodException | SecurityException ex) {
        }
        return _nativeTypeSupported(type) || zeroArg;
    }

    private static boolean _nativeTypeSupported(Class<?> type) {
        return type.isPrimitive()
                || type == String.class
                || type == Class.class
                || type.isEnum()
                || List.class.isAssignableFrom(type)
                || Set.class.isAssignableFrom(type)
                || type.isArray()
                || MethodReferenceReflection.class.isAssignableFrom(type)
                ;
    }

    public boolean typeSupported(Class<?> type) {
        return classSerializerMap.containsKey(type)
                || _nativeSerializerSupport(type)
                || namingStrategies.stream().anyMatch(s -> s.typeSupported(type));
    }

    public String mapToJavaSource(Object primitiveVal, List<Field> nodeFields, Set<Class<?>> importList) {
        Class<?> primitiveValClass = primitiveVal.getClass();
        Function<FieldContext, String> serializeFunction = classSerializerMap.get(primitiveValClass);
        FieldContext f = new FieldContext(primitiveVal, nodeFields, importList);
        if (serializeFunction != null) {
            return serializeFunction.apply(f);
        }
        if (_nativeTypeSupported(primitiveValClass)) {
            return _mapToJavaSource(primitiveVal, nodeFields, importList);
        }
        Optional<String> optionalSerialise = namingStrategies.stream()
                .filter(s -> s.typeSupported(primitiveValClass))
                .findFirst()
                .map(m -> m.mapToSource(f));
        if (optionalSerialise.isPresent()) {
            return optionalSerialise.get();
        }
        return _mapToJavaSource(primitiveVal, nodeFields, importList);
    }

    public boolean propertySupported(PropertyDescriptor property, Field field, List<Field> nodeFields) {
        return _propertySupported(property, field, nodeFields);
    }

    public String mapPropertyToJavaSource(PropertyDescriptor property, Field field, List<Field> nodeFields,
                                          Set<Class<?>> importList) {
        return _mapPropertyToJavaSource(property, field, nodeFields, importList);
    }

    private String _mapToJavaSource(Object primitiveVal, List<Field> nodeFields, Set<Class<?>> importList) {
        Class clazz = primitiveVal.getClass();
        String primitiveSuffix = "";
        String primitivePrefix = "";
        Object original = primitiveVal;
        if (List.class.isAssignableFrom(clazz)) {
            importList.add(Arrays.class);
            List values = (List) primitiveVal;
            List newList = new ArrayList();
            values.stream().forEach(item -> {
                boolean foundMatch = false;
                for (Field nodeField : nodeFields) {
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
            primitiveVal = newList.stream().map(f -> _mapToJavaSource(f, nodeFields, importList)).collect(Collectors.joining(", ", "Arrays.asList(", ")"));
        }
        if (Set.class.isAssignableFrom(clazz)) {
            importList.add(Arrays.class);
            importList.add(HashSet.class);
            Set values = (Set) primitiveVal;
            Set newList = new HashSet();
            values.stream().forEach(item -> {
                boolean foundMatch = false;
                for (Field nodeField : nodeFields) {
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
            primitiveVal = newList.stream()
                    .map(f -> _mapToJavaSource(f, nodeFields, importList)).
                    collect(Collectors.joining(", ", "new HashSet<>(Arrays.asList(", "))"));
        }
        if (clazz.isArray()) {
            Class arrayType = clazz.getComponentType();
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
                strings.add(_mapToJavaSource(arrayElement, nodeFields, importList));
            }
//            Object[] values = (Object[]) primitiveVal;
            primitiveVal = strings.stream().collect(Collectors.joining(", ", "new " + arrayType.getSimpleName() + "[]{", "}"));
        }
        if (clazz.isEnum()) {
            primitiveVal = clazz.getSimpleName() + "." + ((Enum) primitiveVal).name();
            importList.add(clazz);
        }
        if (clazz == Float.class || clazz == float.class) {
            primitiveSuffix = "f";
        }
        if (clazz == Double.class || clazz == double.class) {
            if (Double.isNaN((double) primitiveVal)) {
                primitiveVal = "Double.NaN";
            }
        }
        if (clazz == Byte.class || clazz == byte.class) {
            primitivePrefix = "(byte)";
        }
        if (clazz == Short.class || clazz == short.class) {
            primitivePrefix = "(short)";
        }
        if (clazz == Long.class || clazz == long.class) {
            primitiveSuffix = "L";
        }
        if (clazz == Character.class || clazz == char.class) {
            primitivePrefix = "'";
            primitiveSuffix = "'";
            primitiveVal = StringEscapeUtils.escapeJava(primitiveVal.toString());
        }
        if (clazz == String.class) {
            primitivePrefix = "\"";
            primitiveSuffix = "\"";
            primitiveVal = StringEscapeUtils.escapeJava(primitiveVal.toString());
        }
        if (clazz == Class.class) {
            importList.add((Class) primitiveVal);
            primitiveVal = ((Class) primitiveVal).getSimpleName() + ".class";
        }
        boolean foundMatch = false;
        if (MergeProperty.class.isAssignableFrom(clazz)) {
            importList.add(MergeProperty.class);
            MergeProperty<?, ?> mergeProperty = (MergeProperty<?, ?>) primitiveVal;
            LambdaReflection.SerializableBiConsumer<?, ?> setValue = mergeProperty.getSetValue();
            String containingClass = setValue.getContainingClass().getSimpleName();
            String methodName = setValue.method().getName();
            String lambda = containingClass + "::" + methodName;
            String triggerName = "null";
            //
            FlowFunction<?> trigger = mergeProperty.getTrigger();
            for (Field nodeField : nodeFields) {
                if (nodeField.instance == trigger) {
                    triggerName = nodeField.name;
                    break;
                }
            }
            primitiveVal = "new MergeProperty<>("
                    + triggerName + ", " + lambda + "," + mergeProperty.isTriggering() + "," + mergeProperty.isMandatory() + ")";
        }
        if (MethodReferenceReflection.class.isAssignableFrom(clazz)) {
            MethodReferenceReflection ref = (MethodReferenceReflection) primitiveVal;
            importList.add(ref.getContainingClass());

            if (ref.isDefaultConstructor()) {
                primitiveVal = ref.getContainingClass().getSimpleName() + "::new";
            } else if (ref.captured().length > 0) {
                //see if we can find the reference and set the instance
                Object functionInstance = ref.captured()[0];
                for (Field nodeField : nodeFields) {
                    if (nodeField.instance == functionInstance) {
                        primitiveVal = nodeField.name + "::" + ref.method().getName();
                        foundMatch = true;
                        break;
                    }
                }
                if (!foundMatch) {
                    primitiveVal = "new " + ref.getContainingClass().getSimpleName() + "()::" + ref.method().getName();
                }
            } else {
                if (ref.getContainingClass().getTypeParameters().length > 0) {
                    String typeParam = "<Object";
                    for (int i = 1; i < ref.getContainingClass().getTypeParameters().length; i++) {
                        typeParam += ", Object";
                    }
                    typeParam += ">";
                    primitiveVal = ref.getContainingClass().getSimpleName() + typeParam + "::" + ref.method().getName();
                } else {
                    primitiveVal = ref.getContainingClass().getSimpleName() + "::" + ref.method().getName();
                }
            }
        }
        for (Field nodeField : nodeFields) {
            if (nodeField.instance == primitiveVal) {
                primitiveVal = nodeField.name;
                foundMatch = true;
                break;
            }
        }

        if (!foundMatch && original == primitiveVal
                && !org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper(clazz)
                && !String.class.isAssignableFrom(clazz)
                && clazz.getCanonicalName() != null
        ) {
            importList.add(clazz);
            primitiveVal = "new " + (clazz).getSimpleName() + "()";
        }
        return primitivePrefix + primitiveVal.toString() + primitiveSuffix;
    }

    private String _mapPropertyToJavaSource(PropertyDescriptor property, Field field, List<Field> nodeFields,
                                            Set<Class<?>> importList) {
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
        return "";
    }


}
