/* 
 * Copyright (C) 2018 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.api.annotations.AfterEvent;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.api.annotations.PushReference;
import com.fluxtion.api.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.builder.generation.GenerationContext;
import static com.fluxtion.builder.generation.GenerationContext.SINGLETON;
import com.fluxtion.ext.declarative.api.Test;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.builder.factory.FunctionGeneratorHelper;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.filter;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.filterSubjectClass;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.functionClass;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.imports;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.input;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.newFunction;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.outputClass;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.sourceClass;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.sourceMappingList;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.targetClass;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.targetMethod;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.wrappedSubject;
import com.fluxtion.ext.declarative.builder.util.ArraySourceInfo;
import com.fluxtion.ext.declarative.builder.util.FunctionInfo;
import com.fluxtion.ext.declarative.builder.util.ImportMap;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.ext.declarative.api.Stateful;
import com.fluxtion.ext.declarative.api.stream.StreamOperator;
import com.fluxtion.ext.declarative.api.numeric.MutableNumber;
import com.fluxtion.ext.declarative.api.stream.AbstractFilterWrapper;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.stateful;
import com.fluxtion.ext.declarative.builder.util.FunctionArg;
import com.fluxtion.ext.declarative.builder.util.SourceInfo;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.velocity.VelocityContext;

/**
 * Applies filtering logic to a node in the execution graph. The filter invokes
 * a predicate that tests a node for a valid match. The outcome of the match has
 * the following effect:
 * <ul>
 * <li>Successful match allows the event wave to continue.
 * <li>Failed match the event wave stops at the node under test.
 * </ul>
 *
 * <p>
 * Filtering has the following charactersitics:
 * <ul>
 * <li>Filter functions are either instance or static methods.
 * <li>An instance filter is a node in the SEP and can receive inputs from any
 * other nodes.
 * <li>An instance filter is stateful, the same filter instance will be used
 * across multiple event processing cycles.
 * <li>An instance filter can attach to any SEP lifecyle method such as
 * {@link AfterEvent}
 * <li>A filter inspection target can be a method reference or a node in the
 * exeution graph.
 * <li>A ,target method reference can return either a primitive or reference
 * types.
 * <li>Fluxtion will cast all supplied values to the receiving type.
 * <li><b>Lmabdas cannot be used as filter predicates</b> use method references.
 * </ul>
 *
 * Below is an example creating a filter on a primitive double property. The
 * filter is accets an int parmter all casts are managed
 * <p>
 * <
 * pre><code>
 * {@code @SepBuilder(name = "FilterTest", packageName = "com.fluxtion.testfilter")}
 * public void buildFilter(SEPConfig cfg) { MyDataHandler dh1 = cfg.addNode(new
 * MyDataHandler("dh1")); filter(lt(34), dh1::getDoubleVal).build();
 * filter(positive(), dh1::getIntVal).build(); } ... public class
 * NumericValidator {
 *
 *     //method reference wraps instance test public static SerializableFunction
 * lt(int test) { return (SerializableFunction<Integer, Boolean>) new
 * NumericValidator(test)::lessThan; } //method reference wraps static test
 * public static SerializableFunction positive() { return
 * (SerializableFunction<Integer, Boolean>) NumericValidator::positiveInt; }
 * public int limit;
 *
 * public NumericValidator(int limit) { this.limit = limit; }
 *
 * public static boolean positiveInt(int d) { return d > 0; }
 *
 * public boolean greaterThan(int d) { return d > limit; }
 *
 * public boolean lessThan(int d) { return d < limit; } } </code></pre>
 *
 *
 * @author V12 Technology Ltd.
 * @param <T> The test function applied to the filter subject
 * @param <F> The filter subject under test
 */
public class FilterBuilder<T, F> {

    private static final String TEMPLATE = "template/FilterTemplate.vsl";
    private static final String MAPPER_TEMPLATE = "template/MapperTemplate.vsl";
    private static final String PUSH_TEMPLATE = "template/PushTemplate.vsl";
    private static final String MAPPER_PRIMITIVE_TEMPLATE = "template/MapperPrimitiveTemplate.vsl";
    private static final String MAPPER_BOOLEAN_TEMPLATE = "template/MapperBooleanTemplate.vsl";
    private static final String CONSUMER_TEMPLATE = "template/ConsumerTemplate.vsl";
    private static final String TEMPLATE_ARRAY = "template/TestArrayTemplate.vsl";
    private static final String INPUT_ARRAY_ELEMENT = "filterElementToTest";
    private static final String INPUT_ARRAY = "filterArray";
    private FunctionClassKey key;
    private String currentTemplate = TEMPLATE;
    private String genClassSuffix = "Filter_";

    private final HashMap<Object, SourceInfo> inst2SourceInfo = new HashMap<>();
    private FunctionInfo functionInfo;
    private final Class<T> testFunctionClass;
    //only used for filtering functionality
    private F filterSubject;
    private Wrapper filterSubjectWrapper;
    //for sources id's
    private int sourceCount;
    //array
    private boolean isArray;
    private F[] filterSubjectArray;
    private Wrapper[] filterSubjectWrapperArray;
    private ArraySourceInfo arraySourceInfo;
    //To be used for rationalising imports
    private final ImportMap importMap = ImportMap.newMap();
    private T testFunction;

    private FilterBuilder(Class<T> testFunctionClass) {
        this.testFunctionClass = testFunctionClass;
        isArray = false;
        Method[] methods = testFunctionClass.getDeclaredMethods();
        functionInfo = new FunctionInfo(methods[0], importMap);
        standardImports();
    }

    private FilterBuilder(T testInstance) {
        this.testFunctionClass = (Class<T>) testInstance.getClass();
        this.testFunction = GenerationContext.SINGLETON.addOrUseExistingNode(testInstance);
        isArray = false;
        standardImports();
    }

    /**
     * static filter generation method
     *
     * @param <T>
     * @param <R>
     * @param <S>
     * @param <F>
     * @param filterMethod
     * @param source
     * @param accessor
     * @param cast
     * @return
     */
    public static <T, R extends Boolean, S, F> FilterBuilder filter(Method filterMethod, S source, Method accessor, boolean cast) {
        FilterBuilder filterBuilder = new FilterBuilder(filterMethod.getDeclaringClass());
        String sourceString = (accessor == null ? source.getClass().getSimpleName() : accessor.getName());
        filterBuilder.functionInfo = new FunctionInfo(filterMethod, filterBuilder.importMap);
        filterBuilder.filterSubject = source;
        SourceInfo sourceInfo = filterBuilder.addSource(source);
        filterBuilder.key = new FunctionClassKey(null, filterMethod, source, accessor, cast, "filter");
        if (source instanceof Wrapper) {
            filterBuilder.filterSubjectWrapper = (Wrapper) source;
            sourceString = accessor == null ? filterBuilder.filterSubjectWrapper.eventClass().getSimpleName() : accessor.getName();
            if (accessor == null) {
                filterBuilder.functionInfo.appendParamLocal("filterSubject", (Wrapper) source, cast);
            } else {
                filterBuilder.functionInfo.appendParamSource(accessor, sourceInfo, (Wrapper) source, cast);
            }
        } else {
            if (accessor == null) {
                filterBuilder.functionInfo.appendParamValue("filterSubject", cast, true);
            } else {
                filterBuilder.functionInfo.appendParamSource(accessor, sourceInfo, cast);
            }
        }
        filterBuilder.genClassSuffix = "Filter_" + sourceString + "_By_" + filterMethod.getName();
        return filterBuilder;
    }
    
    public static <T, R extends Boolean, S, F> FilterBuilder mapSet(F mapper, FunctionArg... args) {
        //call map then add sources
        //update the key
        //filterBuilder.key = new FunctionClassKey(mapper, mappingMethod, source, accessor, cast, "mapper");
        return null;
    }
    
    public static <T, R extends Boolean, S, F> FilterBuilder map(F mapper, Method mappingMethod, S source, Method accessor, boolean cast) {
        FilterBuilder filterBuilder;
        boolean staticMeth = Modifier.isStatic(mappingMethod.getModifiers());
        if (mapper == null && staticMeth ) {
            filterBuilder = new FilterBuilder(mappingMethod.getDeclaringClass());
        } else if(mapper == null && !staticMeth) {
            try {
                mapper = (F) mappingMethod.getDeclaringClass().newInstance();
            } catch (IllegalAccessException | InstantiationException ex) {
                throw new RuntimeException("unable to create mapper instance must have public default consturctor", ex);
            }
            filterBuilder = new FilterBuilder(mapper);
        } else {
            filterBuilder = new FilterBuilder(mapper);
        }
        String sourceString = (accessor == null ? source.getClass().getSimpleName() : accessor.getName());
        filterBuilder.currentTemplate = MAPPER_TEMPLATE;
        filterBuilder.functionInfo = new FunctionInfo(mappingMethod, filterBuilder.importMap);
        filterBuilder.key = new FunctionClassKey(mapper, mappingMethod, source, accessor, cast, "mapper");
        if (mappingMethod.getReturnType().isPrimitive() && mappingMethod.getReturnType() != boolean.class) {
            filterBuilder.currentTemplate = MAPPER_PRIMITIVE_TEMPLATE;
            filterBuilder.importMap.addImport(Number.class);
            filterBuilder.importMap.addImport(MutableNumber.class);
        }else if( mappingMethod.getReturnType() == boolean.class){
            filterBuilder.currentTemplate = MAPPER_BOOLEAN_TEMPLATE;
            filterBuilder.importMap.addImport(Boolean.class);
        }
        filterBuilder.filterSubject = source;
        if (source instanceof Wrapper) {
            filterBuilder.filterSubjectWrapper = (Wrapper) source;
            sourceString = accessor == null ? filterBuilder.filterSubjectWrapper.eventClass().getSimpleName() : accessor.getName();
            if (accessor == null) {
                filterBuilder.functionInfo.appendParamLocal("filterSubject", (Wrapper) source, cast);
            } else {
                filterBuilder.functionInfo.appendParamLocal(accessor, "filterSubject", (Wrapper) source, cast);
            }
        } else {
            if (accessor == null) {
                filterBuilder.functionInfo.appendParamValue("filterSubject", cast, true);
            } else {
                filterBuilder.functionInfo.appendParamLocal(accessor, "filterSubject", cast);
            }
        }
        filterBuilder.genClassSuffix = "Map_" + sourceString + "_By_" + mappingMethod.getName();
        return filterBuilder;
    }
    
    public static <T, R extends Boolean, S, F> FilterBuilder push(F mapper, Method mappingMethod, S source, Method accessor, boolean cast) {
        FilterBuilder filterBuilder;
        boolean staticMeth = Modifier.isStatic(mappingMethod.getModifiers());
        if (mapper == null && staticMeth ) {
            filterBuilder = new FilterBuilder(mappingMethod.getDeclaringClass());
        } else if(mapper == null && !staticMeth) {
            try {
                mapper = (F) mappingMethod.getDeclaringClass().newInstance();
            } catch (IllegalAccessException | InstantiationException ex) {
                throw new RuntimeException("unable to create mapper instance must have public default consturctor", ex);
            }
            filterBuilder = new FilterBuilder(mapper);
        } else {
            filterBuilder = new FilterBuilder(mapper);
        }
        String sourceString = (accessor == null ? source.getClass().getSimpleName() : accessor.getName());
        filterBuilder.currentTemplate = PUSH_TEMPLATE;
        filterBuilder.importMap.addImport(PushReference.class);
        filterBuilder.functionInfo = new FunctionInfo(mappingMethod, filterBuilder.importMap);
        filterBuilder.key = new FunctionClassKey(mapper, mappingMethod, source, accessor, cast, "mapper");
        filterBuilder.filterSubject = source;
        if (source instanceof Wrapper) {
            filterBuilder.filterSubjectWrapper = (Wrapper) source;
            sourceString = accessor == null ? filterBuilder.filterSubjectWrapper.eventClass().getSimpleName() : accessor.getName();
            if (accessor == null) {
                filterBuilder.functionInfo.appendParamLocal("filterSubject", (Wrapper) source, cast);
            } else {
                filterBuilder.functionInfo.appendParamLocal(accessor, "filterSubject", (Wrapper) source, cast);
            }
        } else {
            if (accessor == null) {
                filterBuilder.functionInfo.appendParamValue("filterSubject", cast, true);
            } else {
                filterBuilder.functionInfo.appendParamLocal(accessor, "filterSubject", cast);
            }
        }
        filterBuilder.genClassSuffix = "Push_" + sourceString + "_To_" + mappingMethod.getName();
        return filterBuilder;
    }

    public static <S, C> FilterBuilder consume(C consumer, Method mappingMethod, S source) {
        if (consumer == System.out) {
            consumer = null;
            mappingMethod = ((SerializableConsumer) StreamOperator::standardOut).method();
        }
        FilterBuilder filterBuilder;
        if (consumer == null) {
            filterBuilder = new FilterBuilder(mappingMethod.getDeclaringClass());
        } else {
            filterBuilder = new FilterBuilder(consumer);
        }
        String sourceString = source.getClass().getSimpleName();
        filterBuilder.currentTemplate = CONSUMER_TEMPLATE;
        filterBuilder.functionInfo = new FunctionInfo(mappingMethod, filterBuilder.importMap);
        filterBuilder.functionInfo.returnTypeClass = source.getClass();
        filterBuilder.functionInfo.returnType = source.getClass().getName();
        filterBuilder.key = new FunctionClassKey(consumer, mappingMethod, source, null, false, "consumer");
        filterBuilder.filterSubject = source;
        if (source instanceof Wrapper) {
            filterBuilder.filterSubjectWrapper = (Wrapper) source;
            sourceString = filterBuilder.filterSubjectWrapper.eventClass().getSimpleName();
            filterBuilder.functionInfo.appendParamLocal("filterSubject", (Wrapper) source, false);
        } else {
            filterBuilder.functionInfo.appendParamValue("filterSubject", false, false);
        }
        filterBuilder.genClassSuffix = "Consume_" + sourceString;
//        + "_By_" + mappingMethod.getName();
        return filterBuilder;
    }

    /**
     * instance filter generation method
     *
     * @param <T>
     * @param <R>
     * @param <S>
     * @param <F>
     * @param filter
     * @param filterMethod
     * @param source
     * @param accessor
     * @param cast
     * @return
     */
    public static <T, R extends Boolean, S, F> FilterBuilder filter(F filter, Method filterMethod, S source, Method accessor, boolean cast) {
        GenerationContext.SINGLETON.addOrUseExistingNode(filter);
        FilterBuilder filterBuilder = new FilterBuilder(filter);
        String sourceString = accessor == null ? source.getClass().getSimpleName() : accessor.getName();
        filterBuilder.functionInfo = new FunctionInfo(filterMethod, filterBuilder.importMap);
        filterBuilder.filterSubject = source;
        SourceInfo sourceInfo = filterBuilder.addSource(source);
        filterBuilder.key = new FunctionClassKey(filter, filterMethod, source, accessor, cast, "filter");
        if (source instanceof Wrapper) {
            filterBuilder.filterSubjectWrapper = (Wrapper) source;
            sourceString = accessor == null ? filterBuilder.filterSubjectWrapper.eventClass().getSimpleName() : accessor.getName();
            if (accessor == null) {
                filterBuilder.functionInfo.appendParamLocal("filterSubject", (Wrapper) source, cast);
            } else {
                filterBuilder.functionInfo.appendParamSource(accessor, sourceInfo, (Wrapper) source, cast);
            }
        } else {
            if (accessor == null) {
                filterBuilder.functionInfo.appendParamValue("filterSubject", cast, true);
            } else {
                filterBuilder.functionInfo.appendParamSource(accessor, sourceInfo, cast);
            }
        }
        filterBuilder.genClassSuffix = "Filter_" + sourceString + "_By_" + filterMethod.getName();
        return filterBuilder;
    }

    public static <T, R extends Boolean, S, F> FilterBuilder filter(F filter, Method filterMethod, S source, Method accessor) {
        return filter(filter, filterMethod, source, accessor, true);
    }

    public static <T, R extends Boolean, S, F> FilterBuilder filter(F filter, Method filterMethod, S source) {
        return filter(filter, filterMethod, source, null, true);
    }

    public static <T, R extends Boolean, S, F> FilterBuilder filter(Method filterMethod, S source) {
        return filter(filterMethod, source, null, true);
    }

    public static <T, R extends Boolean, S> FilterBuilder filter(SerializableFunction<T, R> filter, S source, Method accessor) {
        if (Modifier.isStatic(filter.method(SINGLETON.getClassLoader()).getModifiers())) {
            return filter(filter.method(SINGLETON.getClassLoader()), source, accessor, true);
        }
        return filter(filter.captured()[0], filter.method(SINGLETON.getClassLoader()), source, accessor);
    }

    public static <T, R extends Boolean, S> FilterBuilder filter(SerializableFunction<T, R> filter, S source) {
        if (Modifier.isStatic(filter.method(SINGLETON.getClassLoader()).getModifiers())) {
            return filter(filter.method(SINGLETON.getClassLoader()), source, null, true);
        }
        return filter(filter.captured()[0], filter.method(SINGLETON.getClassLoader()), source, null);
    }

    public static <T, R extends Boolean> FilterBuilder filter(SerializableFunction<T, R> filter, SerializableSupplier<T> supplier) {
        if (Modifier.isStatic(filter.method(SINGLETON.getClassLoader()).getModifiers())) {
            return filter(filter.method(SINGLETON.getClassLoader()), supplier.captured()[0], supplier.method(SINGLETON.getClassLoader()), true);
        }
        return filter(filter.captured()[0], filter.method(SINGLETON.getClassLoader()), supplier.captured()[0], supplier.method(SINGLETON.getClassLoader()));
    }

    public Wrapper<F> build() {
        if (isArray) {
//            return buildFilterArray();
        }
        try {
            VelocityContext ctx = new VelocityContext();
            String genClassName = genClassSuffix + "_" + GenerationContext.nextId();
            ctx.put(functionClass.name(), genClassName);
            ctx.put(outputClass.name(), functionInfo.returnType);
            ctx.put(targetClass.name(), functionInfo.calculateClass);
            ctx.put(targetMethod.name(), functionInfo.calculateMethod);
            ctx.put(input.name(), functionInfo.paramString);
            ctx.put(filter.name(), true);
            ctx.put(stateful.name(), isStateful(functionInfo.getFunctionMethod()));
            if (filterSubjectWrapper != null) {
                ctx.put(wrappedSubject.name(), true);
                ctx.put(filterSubjectClass.name(), importMap.addImport(filterSubjectWrapper.eventClass()));//.getSimpleName());
                importMap.addImport(filterSubjectWrapper.eventClass());
                ctx.put(sourceClass.name(), importMap.addImport(filterSubjectWrapper.getClass()));//.getSimpleName());
            } else {
                ctx.put(filterSubjectClass.name(), importMap.addImport(filterSubject.getClass()));//.getSimpleName());
                ctx.put(sourceClass.name(), importMap.addImport(filterSubject.getClass()));//.getSimpleName());
                importMap.addImport(filterSubject.getClass());
            }
            ctx.put(sourceMappingList.name(), new ArrayList(inst2SourceInfo.values()));
            ctx.put(imports.name(), importMap.asString());
            ctx.put(newFunction.name(), testFunction == null);
//            Class<Wrapper<F>> aggClass = FunctionGeneratorHelper.generateAndCompile(null, currentTemplate, GenerationContext.SINGLETON, ctx);
            Class<Wrapper<F>> aggClass = compileIfAbsent(ctx);
            Wrapper<F> result = aggClass.newInstance();
            //set function instance
            if (testFunction != null) {
                aggClass.getField("f").set(result, testFunction);
            }
            //set sources via reflection
            Set<Map.Entry<Object, SourceInfo>> entrySet = inst2SourceInfo.entrySet();
            for (Map.Entry<Object, SourceInfo> entry : entrySet) {
                Object source = entry.getKey();
                String fieldName = entry.getValue().id;
                aggClass.getField(fieldName).set(result, source);
            }
            if (filterSubjectWrapper != null) {
                aggClass.getField("filterSubject").set(result, filterSubjectWrapper);
            } else {
                aggClass.getField("filterSubject").set(result, filterSubject);
            }
            GenerationContext.SINGLETON.getNodeList().add(result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("could not buuld function " + toString(), e);
        }
    }

    private Class<Wrapper<F>> compileIfAbsent(VelocityContext ctx) throws Exception {
        Map<FunctionClassKey, Class<Wrapper<F>>> cache = GenerationContext.SINGLETON.getCache(FilterBuilder.class);
        Class<Wrapper<F>> clazz = cache.get(key);
        if (clazz == null) {
            clazz = FunctionGeneratorHelper.generateAndCompile(null, currentTemplate, GenerationContext.SINGLETON, ctx);
            cache.put(key, clazz);
        }
        return clazz;
    }

    private boolean isStateful(Method method) {
        if (!Modifier.isStatic(method.getModifiers()) && Stateful.class.isAssignableFrom(method.getDeclaringClass())) {
            importMap.addImport(Stateful.class);
            return true;
        }
        return false;
    }

//    private static Class getClassForInstance(Object o) {
//        if (o == null) {
//            return null;
//        }
//        return o.getClass();
//    }

    private void standardImports() {
        importMap.addImport(OnEvent.class);
        importMap.addImport(Wrapper.class);
        importMap.addImport(Initialise.class);
        importMap.addImport(NoEventReference.class);
        importMap.addImport(OnParentUpdate.class);
        importMap.addImport(Wrapper.class);
        importMap.addImport(Test.class);
        importMap.addImport(AbstractFilterWrapper.class);
        importMap.addImport(AfterEvent.class);
    }

    private SourceInfo addSource(Object input) {

        return inst2SourceInfo.computeIfAbsent(input, (in) -> new SourceInfo(
                importMap.addImport(input.getClass()),
                "source_" + sourceCount++));

    }
}
