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
package com.fluxtion.extension.declarative.builder.test;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.extension.declarative.api.Test;
import com.fluxtion.api.generation.GenerationContext;
import com.fluxtion.extension.declarative.api.Wrapper;
import com.fluxtion.extension.declarative.builder.event.EventSelect;
import com.fluxtion.extension.declarative.api.EventWrapper;
import com.fluxtion.extension.declarative.builder.factory.FunctionGeneratorHelper;
import static com.fluxtion.extension.declarative.builder.factory.FunctionGeneratorHelper.methodFromLambda;
import com.fluxtion.extension.declarative.builder.factory.FunctionKeys;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.arraySize;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.filter;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.filterSubjectClass;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.filterSubjectClassFqn;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.functionClass;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.input;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.outputClass;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.sourceClass;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.sourceClassFqn;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.sourceMappingList;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.targetClass;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.targetMethod;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.wrappedSubject;
import com.fluxtion.extension.declarative.api.numeric.NumericValue;
import com.fluxtion.extension.declarative.api.util.StringWrapper;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.imports;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.newFunction;
import com.fluxtion.extension.declarative.builder.util.ArraySourceInfo;
import com.fluxtion.extension.declarative.builder.util.FunctionInfo;
import com.fluxtion.extension.declarative.builder.util.ImportMap;
import com.fluxtion.extension.declarative.builder.util.LambdaReflection.SerializableConsumer;
import com.fluxtion.extension.declarative.builder.util.LambdaReflection.SerializableSupplier;
import com.fluxtion.extension.declarative.builder.util.SourceInfo;
import com.fluxtion.runtime.event.Event;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.lang.ClassUtils;
import org.apache.velocity.VelocityContext;

/**
 *
 * @author Greg Higgins
 * @param <T>
 * @param <F>
 */
public final class TestBuilder<T, F> {

    private static final String TEMPLATE = "template/TestTemplate.vsl";
    private static final String TEMPLATE_ARRAY = "template/TestArrayTemplate.vsl";
    private static final String INPUT_ARRAY_ELEMENT = "filterElementToTest";
    private static final String INPUT_ARRAY = "filterArray";
    private final HashMap<Object, SourceInfo> inst2SourceInfo = new HashMap<>();
    private FunctionInfo functionInfo;
    private final Class<T> testFunctionClass;
    private boolean notifyOnChange;
    //only used for filtering functionality
    private F filterSubject;
    private Wrapper filterSubjectWrapper;
    //array
    private boolean isArray;
    private F[] filterSubjectArray;
    private Wrapper[] filterSubjectWrapperArray;
    private ArraySourceInfo arraySourceInfo;
    //To be used for rationalising imports
    private Set<Class> classSet;
    private final ImportMap importMap = ImportMap.newMap();
    private T testFunction;

    private TestBuilder(Class<T> testFunctionClass) {
        this.testFunctionClass = testFunctionClass;
        notifyOnChange = false;
        isArray = false;
        checkFunction();
        standardImports();
    }

    private TestBuilder(T testInstance) {
        this.testFunctionClass = (Class<T>) testInstance.getClass();
        this.testFunction = testInstance;
        notifyOnChange = false;
        isArray = false;
        standardImports();
    }

    private final void standardImports() {
        importMap.addImport(OnEvent.class);
        importMap.addImport(Wrapper.class);
        importMap.addImport(Initialise.class);
        importMap.addImport(NoEventReference.class);
    }

    //number scalar
    public static <T extends Test> TestBuilder<T, Number> buildTest(Class<T> testClass, Number n) {
        TestBuilder<T, Number> testBuilder = new TestBuilder(testClass);
        testBuilder.filterSubject = n;
        testBuilder.arg(n);
        return testBuilder;
    }

//  template to replace Class<T> with Function<T, R>    
//    public static <T extends Test> TestBuilder<T, Number> buildTest(Function<Number, ?> testClass, Number n) {
//        TestBuilder<T, Number> testBuilder = new TestBuilder(testClass);
//        testBuilder.filterSubject = n;
//        testBuilder.arg(n);
//        return testBuilder;
//    }

    public static <T extends Test, C extends Number> TestBuilder<T, Number> buildTest(SerializableConsumer<C> rule, Number n) {
        Object handler = rule.captured()[0];
        GenerationContext.SINGLETON.addOrUseExistingNode(handler);
        TestBuilder<T, Number> testBuilder = new TestBuilder(handler);
        testBuilder.filterSubject = n;
        testBuilder.arg(n);
        return testBuilder;
    }

    public static <T extends Test, S extends NumericValue> TestBuilder<T, S> buildTest(Class<T> testClass, S n) {
        TestBuilder<T, S> testBuilder = new TestBuilder(testClass);
        testBuilder.filterSubject = n;
        testBuilder.arg(n);
        return testBuilder;
    }

    public static <T extends Test, S extends NumericValue> TestBuilder<T, S> buildTest(SerializableConsumer<S> rule, S n) {
        Object handler = rule.captured()[0];
        GenerationContext.SINGLETON.addOrUseExistingNode(handler);
        TestBuilder<T, S> testBuilder = new TestBuilder(handler);
        testBuilder.filterSubject = n;
        testBuilder.arg(n);
        return testBuilder;
    }

    //event subscription
    public static <S extends Event, V, T extends Test> TestBuilder<T, S> buildTest(
            Class<T> testClass, Class<S> eventClass, Function<S, ?> accessor) {
        return buildTest(testClass, EventSelect.select(eventClass), accessor);
    }

    public static <S extends Event, V, R, T extends Test> TestBuilder<T, S> buildTest(
            SerializableConsumer<? extends R> rule, Class<S> eventClass, Function<S, R> accessor) {
        return buildTest(rule, EventSelect.select(eventClass), accessor);
    }

    public static <S, V, T extends Test> TestBuilder<T, S> buildTest(
            Class<T> testClass, Wrapper<S> handler, Function<S, ?> accessor) {
        TestBuilder<T, S> testBuilder = new TestBuilder(testClass);
        testBuilder.filterSubjectWrapper = handler;
        testBuilder.arg(handler, accessor);
        return testBuilder;
    }

    public static <T, R> TestBuilder buildTest(SerializableConsumer<? extends R> rule, Wrapper<T> instance, Function<T, R> supplier) {
        Object handler = rule.captured()[0];
        GenerationContext.SINGLETON.addOrUseExistingNode(handler);
        TestBuilder<T, ?> testBuilder = new TestBuilder(handler);
        testBuilder.functionInfo = new FunctionInfo(rule.method(), testBuilder.importMap);
        testBuilder.arg(instance, supplier);
        return testBuilder;
    }

    public static <S, V, T extends Test> TestBuilder<T, S> buildTest(
            Class<T> testClass, S supplier, SerializableSupplier<S, V> accessor) {
        TestBuilder<T, S> testBuilder = new TestBuilder(testClass);
        testBuilder.filterSubject = supplier;
        testBuilder.arg(accessor);
        return testBuilder;
    }

    public static <T, R> TestBuilder buildTest(SerializableConsumer<? extends R> rule, SerializableSupplier<T, R> supplier) {
        Object handler = rule.captured()[0];
        GenerationContext.SINGLETON.addOrUseExistingNode(handler);
        TestBuilder<T, ?> testBuilder = new TestBuilder(handler);
        testBuilder.functionInfo = new FunctionInfo(rule.method(), testBuilder.importMap);
        testBuilder.arg(supplier);
        return testBuilder;
    }

    //ARRAY SUPPORT
    public static <T extends Test, N extends Number> TestBuilder<T, N> buildTest(Class<T> testClass, N[] n) {
        TestBuilder<T, N> testBuilder = new TestBuilder(testClass);
        testBuilder.isArray = true;
        testBuilder.filterSubjectArray = n;
        testBuilder.arg(n);
        return testBuilder;
    }

    public static <T extends Test, N extends Number> TestBuilder<T, N> buildTest(SerializableConsumer<N> rule, N[] n) {
        Object handler = rule.captured()[0];
        GenerationContext.SINGLETON.addOrUseExistingNode(handler);
        TestBuilder<T, N> testBuilder = new TestBuilder(handler);
        testBuilder.isArray = true;
        testBuilder.filterSubjectArray = n;
        testBuilder.arg(n);
        return testBuilder;
    }

    public static <T extends Test, N extends NumericValue> TestBuilder<T, N> buildTest(Class<T> testClass, N[] n) {
        TestBuilder<T, N> testBuilder = new TestBuilder(testClass);
        testBuilder.isArray = true;
        testBuilder.filterSubjectArray = n;
        testBuilder.arg(n);
        return testBuilder;
    }

    public static <T extends Test, N extends NumericValue> TestBuilder<T, N> buildTest(SerializableConsumer<N> rule, N[] n) {
        Object handler = rule.captured()[0];
        GenerationContext.SINGLETON.addOrUseExistingNode(handler);
        TestBuilder<T, N> testBuilder = new TestBuilder(handler);
        testBuilder.isArray = true;
        testBuilder.filterSubjectArray = n;
        testBuilder.arg(n);
        return testBuilder;
    }

    public static <S, V, T extends Test> TestBuilder<T, S> buildTest(
            Class<T> testClass, S[] supplier, Function<S, ?> accessor) {
        TestBuilder<T, S> testBuilder = new TestBuilder(testClass);
        testBuilder.isArray = true;
        testBuilder.filterSubjectArray = supplier;
        testBuilder.arg(supplier, accessor);
        return testBuilder;
    }

    public static <S, R, V, T extends Test> TestBuilder<T, S> buildTest(
            SerializableConsumer<? extends R> rule, S[] supplier, Function<S, R> accessor) {
        Object handler = rule.captured()[0];
        GenerationContext.SINGLETON.addOrUseExistingNode(handler);
        TestBuilder<T, S> testBuilder = new TestBuilder(handler);
        testBuilder.isArray = true;
        testBuilder.filterSubjectArray = supplier;
        testBuilder.arg(supplier, accessor);
        return testBuilder;
    }

    public static <S extends Event, V, T extends Test> TestBuilder<T, S> buildTest(
            Class<T> testClass, Class<S> eventClass, Function<S, ?> accessor, String... filters) {
        return buildTest(testClass, EventSelect.select(eventClass, filters), accessor);
    }
    
    public static <S extends Event, C, V, T extends Test> TestBuilder<T, S> buildTest(
            SerializableConsumer<? extends C> rule, Class<S> eventClass, Function<S, C> accessor, String... filters) {
        return buildTest(rule, EventSelect.select(eventClass, filters), accessor);
    }

    public static <S extends Event, V, T extends Test> TestBuilder<T, S> buildTest(
            Class<T> testClass, Class<S> eventClass, Function<S, ?> accessor, int... filters) {
        return buildTest(testClass, EventSelect.select(eventClass, filters), accessor);
    }

    public static <S extends Event, C, V, T extends Test> TestBuilder<T, S> buildTest(
            SerializableConsumer<? extends C> rule, Class<S> eventClass, Function<S, C> accessor, int... filters) {
        return buildTest(rule, EventSelect.select(eventClass, filters), accessor);
    }

    public static <S extends Event, V, T extends Test> TestBuilder<T, S> buildTest(
            Class<T> testClass, EventWrapper<S>[] handler, Function<S, ?> accessor) {
        TestBuilder<T, S> testBuilder = new TestBuilder(testClass);
        testBuilder.isArray = true;
        testBuilder.filterSubjectWrapperArray = handler;
        testBuilder.filterSubjectWrapper = handler[0];
        testBuilder.arg(handler, accessor);
        return testBuilder;
    }

    public static <S, V, T extends Test> TestBuilder<T, S> buildTest(
            Class<T> testClass, Wrapper<S>[] handler, Function<S, ?> accessor) {
        TestBuilder<T, S> testBuilder = new TestBuilder(testClass);
        testBuilder.isArray = true;
        testBuilder.filterSubjectWrapperArray = handler;
        testBuilder.filterSubjectWrapper = handler[0];
        testBuilder.arg(handler, accessor);
        return testBuilder;
    }

    public static <S extends Event, T, C> TestBuilder<T, S> buildTest(
            SerializableConsumer<? extends C> rule,
            Wrapper<S>[] eventHandlers,
            Function<S, C> accessor
    ) {
        Object handler = rule.captured()[0];
        TestBuilder<T, S> testBuilder = new TestBuilder(handler);
        testBuilder.isArray = true;
        testBuilder.filterSubjectWrapperArray = eventHandlers;
        testBuilder.filterSubjectWrapper = eventHandlers[0];
        testBuilder.arg(eventHandlers, accessor);
        return testBuilder;
    }

    /**
     * Add a parameter to the test supplied by a source and method reference.
     * The value is read at runtime, using the method reference to determine the
     * accessor.
     *
     *
     * @param <S> The source instance of the argument
     * @param <V> Access method in the source
     * @param source source
     * @param accessor accessor method
     * @return Updated TestBuilder
     * @deprecated to be replace with {@linkplain  #arg(com.fluxtion.extension.declarative.builder.util.LambdaReflection.SerializableSupplier) arg(SerializableSupplier&lt;S, V>
     * accessor)}
     */
    public <S, V> TestBuilder<T, F> arg(S source, SerializableSupplier<S, V> accessor) {
        Method accessorMethod = accessor.method();
        SourceInfo sourceInfo = addSource(source);
        functionInfo.appendParamSource(accessorMethod, sourceInfo, true);
        return this;
    }

    private <S, V> TestBuilder<T, F> arg(S[] source, Function<S, ?> accessor) {
        Method accessorMethod = methodFromLambda((Class<S>) source.getClass().getComponentType(), accessor);
        Class componentClass = source.getClass().getComponentType();
        arraySourceInfo = new ArraySourceInfo(
                componentClass,
                accessorMethod,
                true
        );
        arraySourceInfo.addInstances(source);
        functionInfo.appendParamSource(arraySourceInfo, INPUT_ARRAY_ELEMENT, true);
        return this;
    }

    /**
     * Add a NumericValue to the test
     *
     * @param input
     * @return
     */
    public TestBuilder<T, F> arg(NumericValue input) {
        SourceInfo sourceInfo = addSource(input);
        functionInfo.appendParamNumeric(input, sourceInfo);
        return this;
    }

    public TestBuilder<T, F> arg(NumericValue[] input) {
        Method accessorMethod = methodFromLambda(input[0], NumericValue::doubleValue);
        arraySourceInfo = new ArraySourceInfo(
                NumericValue.class,
                accessorMethod,
                true
        );
        arraySourceInfo.addInstances((Object[]) input);
        //TODO add the source as this is not a primitive source
        functionInfo.appendParamNumber(INPUT_ARRAY_ELEMENT);
        return this;
    }

    public TestBuilder<T, F> arg(CharSequence input) {
        StringWrapper str = new StringWrapper(input.toString());
        GenerationContext.SINGLETON.getNodeList().add(str);
        return arg(str::event);
    }

    /**
     * Add a numeric parameter to the test. If the Number is either a primitive,
     * or a primitive wrapper then the value is used. If the supplied input
     * extends Number, the relevant accessor is used at runtime.
     *
     * @param input
     * @return
     */
    public TestBuilder<T, F> arg(Number input) {
        boolean isWrapped
                = null != ClassUtils.wrapperToPrimitive(input.getClass());
        if (input.getClass().isPrimitive() || isWrapped) {
            functionInfo.appendParamLocal(input.toString(), true);
        } else {
            //TODO add the source as this is not a primitive source
            SourceInfo sourceInfo = addSource(input);
            functionInfo.appendParamNumber(input, sourceInfo);
        }
        return this;
    }

    public TestBuilder<T, F> arg(Number[] input) {
        boolean isAutoBoxed
                = null != ClassUtils.wrapperToPrimitive(input.getClass());
        if (isAutoBoxed) {
        } else {
            Method accessorMethod = methodFromLambda(Number.class, Number::doubleValue);
            arraySourceInfo = new ArraySourceInfo(
                    Number.class,
                    accessorMethod,
                    true
            );

            arraySourceInfo.addInstances((Object[]) input);
            //TODO add the source as this is not a primitive source
            functionInfo.appendParamNumber(INPUT_ARRAY_ELEMENT);
        }
        return this;
    }

    /**
     * Add a parameter to the test supplied by a source and method reference for
     * an Event. An event handler for the event type is created and the
     * generated test will subscribe to the handler for updates and then process
     * the wrapped event as defined in arg(EventWrapper<S> handler,
     * Function<S, ?> accessor)
     *
     * @param <S>
     * @param eventClass
     * @param sourceFunction
     * @return
     */
    public < S extends Event> TestBuilder<T, F> arg(Class<S> eventClass,
            Function<S, ?> sourceFunction) {
        return arg(EventSelect.select(eventClass), sourceFunction);
    }

    /**
     * Add a parameter to the test supplied by a source and method reference.
     * The value is read at runtime from the wrapped instance, using the method
     * reference to determine the accessor.
     *
     * @param <S>
     * @param <V>
     * @param handler
     * @param accessor
     * @return
     */
    public <S, V> TestBuilder<T, F> arg(Wrapper<S> handler, Function<S, ?> accessor) {
        Method accessorMethod = methodFromLambda((Class<S>) handler.eventClass(), accessor);
        SourceInfo sourceInfo = addSource(handler);
        functionInfo.appendParamSource(accessorMethod, sourceInfo, handler, false);
//        functionInfo.appendParamSource(accessorMethod, sourceInfo, handler, true);
        return this;
    }

    private <S, V> TestBuilder<T, F> arg(Wrapper<S>[] source, Function<S, ?> accessor) {
        Method accessorMethod = methodFromLambda((Class<S>) source[0].eventClass(), accessor);
        Class componentClass = source.getClass().getComponentType();
        arraySourceInfo = new ArraySourceInfo(
                componentClass,
                accessorMethod,
                true
        );
        arraySourceInfo.addInstances((Object[]) source);
        functionInfo.appendParamSource(arraySourceInfo, INPUT_ARRAY_ELEMENT, false);
//        functionInfo.appendParamSource(arraySourceInfo, INPUT_ARRAY_ELEMENT, true);
        return this;
    }

    /**
     * Add a parameter to the test supplied by a method reference. The value is
     * read at runtime, using the method reference to determine the access
     * method.
     *
     * @param <S>
     * @param <V>
     * @param accessor
     * @return
     */
    public <S, V> TestBuilder<T, F> arg(SerializableSupplier<S, V> accessor) {
        S source = (S) accessor.captured()[0];
        Method accessorMethod = accessor.method();
        SourceInfo sourceInfo = addSource(source);
        functionInfo.appendParamSource(accessorMethod, sourceInfo, false);
//        functionInfo.appendParamSource(accessorMethod, sourceInfo, true);
        return this;
    }

    public TestBuilder<T, F> notifyOnChange(boolean notifyOnChange) {
        this.notifyOnChange = notifyOnChange;
        return this;
    }

    public Wrapper<F> buildFilter() {
        if (isArray) {
            return buildFilterArray();
        }
        try {
            VelocityContext ctx = new VelocityContext();
            String genClassName = testFunctionClass.getSimpleName() + "Decorator_" + GenerationContext.nextId();
            ctx.put(functionClass.name(), genClassName);
            ctx.put(outputClass.name(), functionInfo.returnType);
            ctx.put(targetClass.name(), functionInfo.calculateClass);
            ctx.put(targetMethod.name(), functionInfo.calculateMethod);
            ctx.put(input.name(), functionInfo.paramString);
            ctx.put(filter.name(), true);
            if (filterSubjectWrapper != null) {
                ctx.put(wrappedSubject.name(), true);
                ctx.put(filterSubjectClass.name(), filterSubjectWrapper.eventClass().getSimpleName());
                ctx.put(filterSubjectClassFqn.name(), filterSubjectWrapper.eventClass().getCanonicalName());
                ctx.put(sourceClass.name(), filterSubjectWrapper.getClass().getSimpleName());
                ctx.put(sourceClassFqn.name(), filterSubjectWrapper.getClass().getCanonicalName());
            } else {
                ctx.put(filterSubjectClass.name(), filterSubject.getClass().getSimpleName());
                ctx.put(sourceClass.name(), filterSubject.getClass().getSimpleName());
                ctx.put(filterSubjectClassFqn.name(), filterSubject.getClass().getCanonicalName());
                ctx.put(sourceClassFqn.name(), filterSubject.getClass().getCanonicalName());
            }
            if (notifyOnChange) {
                ctx.put(FunctionKeys.changetNotifier.name(), notifyOnChange);
            }
            ctx.put(sourceMappingList.name(), new ArrayList(inst2SourceInfo.values()));
            ctx.put(imports.name(), importMap.asString());
            ctx.put(newFunction.name(), testFunction == null);
            Class<Wrapper<F>> aggClass = FunctionGeneratorHelper.generateAndCompile(null, TEMPLATE, GenerationContext.SINGLETON, ctx);
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
//            if (resetNotifier != null) {
//                aggClass.getField("resetNotifier").set(result, resetNotifier);
//            }
            GenerationContext.SINGLETON.getNodeList().add(result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("could not buuld function " + toString(), e);
        }
    }

    private Wrapper<F> buildFilterArray() {
        try {
            VelocityContext ctx = new VelocityContext();
            String genClassName = testFunctionClass.getSimpleName() + "Decorator_" + GenerationContext.nextId();
            ctx.put(functionClass.name(), genClassName);
            ctx.put(outputClass.name(), functionInfo.returnType);
            ctx.put(targetClass.name(), functionInfo.calculateClass);
            ctx.put(targetMethod.name(), functionInfo.calculateMethod);
            ctx.put(input.name(), functionInfo.paramString);
            ctx.put(filter.name(), true);
            ctx.put(FunctionKeys.arrayElement.name(), INPUT_ARRAY_ELEMENT);
//            ctx.put(filterSubjectClassFqn.name(), filterSubjectWrapperArray[0].eventClass().getName());
            ctx.put(arraySize.name(), arraySourceInfo.count);
            if (filterSubjectWrapper != null) {
                ctx.put(wrappedSubject.name(), true);
                ctx.put(filterSubjectClass.name(), filterSubjectWrapper.eventClass().getSimpleName());
                ctx.put(filterSubjectClassFqn.name(), filterSubjectWrapper.eventClass().getCanonicalName());
                ctx.put(sourceClass.name(), filterSubjectWrapper.getClass().getSimpleName());
                ctx.put(sourceClassFqn.name(), filterSubjectWrapper.getClass().getCanonicalName());
            } else {
//                String simpleName = filterSubject.getClass().getSimpleName();
//                String fqnName = filterSubject.getClass().getCanonicalName();
                String simpleName = filterSubjectArray.getClass().getComponentType().getSimpleName();
                String fqnName = filterSubjectArray.getClass().getComponentType().getCanonicalName();
                ctx.put(filterSubjectClass.name(), simpleName);
                ctx.put(sourceClass.name(), simpleName);
                ctx.put(filterSubjectClassFqn.name(), fqnName);
                ctx.put(sourceClassFqn.name(), fqnName);
            }
            if (notifyOnChange) {
                ctx.put(FunctionKeys.changetNotifier.name(), notifyOnChange);
            }
            ctx.put(sourceMappingList.name(), new ArrayList(inst2SourceInfo.values()));
            ctx.put(imports.name(), importMap.asString());
            ctx.put(newFunction.name(), testFunction == null);
            Class<Wrapper<F>> aggClass = FunctionGeneratorHelper.generateAndCompile(null, TEMPLATE_ARRAY, GenerationContext.SINGLETON, ctx);
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
//            if(filterSubjectWrapper!=null){
//                aggClass.getField("filterSubject").set(result, filterSubjectWrapper);
//            }else{
//                aggClass.getField("filterSubject").set(result, filterSubject);
//            }
            ArrayList list = arraySourceInfo.sourceInstances;
            Object[] arrayField = (Object[]) aggClass.getField(INPUT_ARRAY).get(result);
            for (int i = 0; i < arrayField.length; i++) {
                arrayField[i] = list.get(i);
            }
//            if (resetNotifier != null) {
//                aggClass.getField("resetNotifier").set(result, resetNotifier);
//            }
            GenerationContext.SINGLETON.getNodeList().add(result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("could not buuld function " + toString(), e);
        }
    }

    /**
     * builds and registers a test in the SEP.
     *
     * @return
     */
    public Test build() {
        if (isArray) {
            return buildArray();
        }
        try {
            VelocityContext ctx = new VelocityContext();
            String genClassName = testFunctionClass.getSimpleName() + "Decorator_" + GenerationContext.nextId();
            ctx.put(functionClass.name(), genClassName);
            ctx.put(outputClass.name(), functionInfo.returnType);
//            ctx.put(targetClass.name(), functionInfo.calculateClass);
            ctx.put(targetClass.name(), importMap.addImport(functionInfo.calculateClazz));
            ctx.put(targetMethod.name(), functionInfo.calculateMethod);
            ctx.put(input.name(), functionInfo.paramString);
            if (notifyOnChange) {
                ctx.put(FunctionKeys.changetNotifier.name(), notifyOnChange);
            }
            ctx.put(sourceMappingList.name(), new ArrayList(inst2SourceInfo.values()));
            ctx.put(imports.name(), importMap.asString());
            ctx.put(newFunction.name(), testFunction == null);
            Class<Test> aggClass = FunctionGeneratorHelper.generateAndCompile(null, TEMPLATE, GenerationContext.SINGLETON, ctx);
            Test result = aggClass.newInstance();
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
            //set function instance
            if (testFunction != null) {
                aggClass.getField("f").set(result, testFunction);
            }
            GenerationContext.SINGLETON.getNodeList().add(result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("could not buuld function " + e.getMessage(), e);
        }
    }

    private Test buildArray() {
        try {
            VelocityContext ctx = new VelocityContext();
            String genClassName = testFunctionClass.getSimpleName() + "Decorator_" + GenerationContext.nextId();
            ctx.put(functionClass.name(), genClassName);
            ctx.put(outputClass.name(), functionInfo.returnType);
            ctx.put(targetClass.name(), functionInfo.calculateClass);
            ctx.put(targetMethod.name(), functionInfo.calculateMethod);
            ctx.put(input.name(), functionInfo.paramString);
            ctx.put(FunctionKeys.arrayElement.name(), INPUT_ARRAY_ELEMENT);
            ctx.put(sourceClass.name(), filterSubjectArray.getClass().getComponentType().getSimpleName());
            ctx.put(sourceClassFqn.name(), filterSubjectArray.getClass().getComponentType().getCanonicalName());
            ctx.put(filterSubjectClassFqn.name(), filterSubjectArray.getClass().getComponentType().getCanonicalName());
            ctx.put(arraySize.name(), arraySourceInfo.count);
            if (notifyOnChange) {
                ctx.put(FunctionKeys.changetNotifier.name(), notifyOnChange);
            }
            ctx.put(sourceMappingList.name(), new ArrayList(inst2SourceInfo.values()));
            ctx.put(imports.name(), importMap.asString());
            ctx.put(newFunction.name(), testFunction == null);
            Class<Test> aggClass = FunctionGeneratorHelper.generateAndCompile(null, TEMPLATE_ARRAY, GenerationContext.SINGLETON, ctx);
            Test result = aggClass.newInstance();
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
            ArrayList list = arraySourceInfo.sourceInstances;
            Object[] arrayField = (Object[]) aggClass.getField(INPUT_ARRAY).get(result);
            for (int i = 0; i < arrayField.length; i++) {
                arrayField[i] = list.get(i);
            }
            GenerationContext.SINGLETON.getNodeList().add(result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("could not buuld function " + e.getMessage(), e);
        }
    }

    private void checkFunction() {
        Method[] methods = testFunctionClass.getDeclaredMethods();
        //Arrays.stream(methods).filter((m) -> m.getModifiers() & Modifiers)
        if (methods.length != 1) {
            throw new RuntimeException("Cannot generate numeric function from "
                    + "supplied function class must have only 1 public "
                    + "method.");
        }
        functionInfo = new FunctionInfo(methods[0], importMap);
    }

    private SourceInfo addSource(Object input) {

        return inst2SourceInfo.computeIfAbsent(input, (in) -> new SourceInfo(
                importMap.addImport(input.getClass()),
                "source_" + input.getClass().getSimpleName() + "_" + GenerationContext.nextId()));

    }

    @Override
    public String toString() {
        return "TestBuilder{" + "inst2SourceInfo=" + inst2SourceInfo + ", functionInfo=" + functionInfo + ", testFunctionClass=" + testFunctionClass + '}';
    }

}
