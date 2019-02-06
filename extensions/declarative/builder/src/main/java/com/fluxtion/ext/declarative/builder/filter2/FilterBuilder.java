package com.fluxtion.ext.declarative.builder.filter2;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.ext.declarative.api.Test;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.builder.factory.FunctionGeneratorHelper;
import com.fluxtion.ext.declarative.builder.factory.FunctionKeys;
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
import com.fluxtion.ext.declarative.builder.util.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.declarative.builder.util.LambdaReflection.SerializableSupplierNew;
import com.fluxtion.ext.declarative.builder.util.SourceInfo;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.velocity.VelocityContext;

/**
 *
 * @author V12 Technology Ltd.
 * @param <T> The test function applied to the filter subject
 * @param <F> The filter subject t be filtered
 */
public class FilterBuilder<T, F> {
    
    
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

    private FilterBuilder(Class<T> testFunctionClass) {
        this.testFunctionClass = testFunctionClass;
        notifyOnChange = false;
        isArray = false;
        Method[] methods = testFunctionClass.getDeclaredMethods();
        functionInfo = new FunctionInfo(methods[0], importMap);
        standardImports();
    }

    private FilterBuilder(T testInstance) {
        this.testFunctionClass = (Class<T>) testInstance.getClass();
        this.testFunction = testInstance;
        notifyOnChange = false;
        isArray = false;
        standardImports();
    }

    public static <T, R extends Boolean, S, F> FilterBuilder filter(F filter, Method filterMethod, S source, Method accessor, boolean cast) {
        GenerationContext.SINGLETON.addOrUseExistingNode(filter);
        FilterBuilder filterBuilder = new FilterBuilder(filter);
        filterBuilder.functionInfo = new FunctionInfo(filterMethod, filterBuilder.importMap);
        filterBuilder.filterSubject =  filter;
        SourceInfo sourceInfo = filterBuilder.addSource(source);
        filterBuilder.functionInfo.appendParamSource(accessor, sourceInfo, cast);
        return filterBuilder;
    }
    
    public static <T, R extends Boolean, S, F> FilterBuilder filter(F filter, Method filterMethod, S source, Method accessor) {
        return filter(filter, filterMethod, source, accessor, true);
    }
    
    public static <T, R extends Boolean, S> FilterBuilder filter(SerializableFunction<T, R> filter, S source, Method accessor) {
        return filter(filter.captured()[0], filter.method(), source, accessor);
    }

    public static <T, R extends Boolean> FilterBuilder filter(SerializableFunction<T, R> filter, SerializableSupplierNew<T> supplier) {
        return filter(filter.captured()[0], filter.method(), supplier.captured()[0], supplier.method());
    }
    
    public Wrapper<F> build() {
        if (isArray) {
//            return buildFilterArray();
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
                importMap.addImport(filterSubjectWrapper.eventClass());
                ctx.put(sourceClass.name(), filterSubjectWrapper.getClass().getSimpleName());
            } else {
                ctx.put(filterSubjectClass.name(), filterSubject.getClass().getSimpleName());
                ctx.put(sourceClass.name(), filterSubject.getClass().getSimpleName());
                importMap.addImport(filterSubject.getClass());
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
    
    
    
    public FilterBuilder<T, F> notifyOnChange(boolean notifyOnChange) {
        this.notifyOnChange = notifyOnChange;
        return this;
    }  
    
    private final void standardImports() {
        importMap.addImport(OnEvent.class);
        importMap.addImport(Wrapper.class);
        importMap.addImport(Initialise.class);
        importMap.addImport(NoEventReference.class);
        importMap.addImport(OnParentUpdate.class);
        importMap.addImport(Wrapper.class);
        importMap.addImport(Test.class);
    }

    private SourceInfo addSource(Object input) {

        return inst2SourceInfo.computeIfAbsent(input, (in) -> new SourceInfo(
                importMap.addImport(input.getClass()),
                "source_" + input.getClass().getSimpleName() + "_" + GenerationContext.nextId()));

    } 
}
