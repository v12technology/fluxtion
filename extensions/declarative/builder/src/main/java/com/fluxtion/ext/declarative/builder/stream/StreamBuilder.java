package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.ext.declarative.api.StreamOperator;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.builder.filter2.FilterBuilder;
import com.google.auto.service.AutoService;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 * @author V12 Technology Ltd.
 */
@AutoService(StreamOperator.class)
public class StreamBuilder implements StreamOperator{

    @Override
    public <S, T> Wrapper<T> filter(LambdaReflection.SerializableFunction<S, Boolean> filter, Wrapper<T> source, Method accessor, boolean cast) {
//        System.out.println("cool this is working");
        Method filterMethod = filter.method();
        FilterBuilder builder = null;
        if(Modifier.isStatic(filterMethod.getModifiers())){
            builder = FilterBuilder.filter(filterMethod, source, accessor, cast);
        }else{
            builder = FilterBuilder.filter(filter.captured()[0], filterMethod, source, accessor, cast);
        }
        return builder.build();
//        return StreamOperator.super.filter(filter, source, accessor, cast); 
    }

    @Override
    public <T> Wrapper<T> filter(LambdaReflection.SerializableFunction<T, Boolean> filter, Wrapper<T> source, boolean cast) {
//        System.out.println("cool this is working");
        Method filterMethod = filter.method();
        FilterBuilder builder = null;
        if(Modifier.isStatic(filterMethod.getModifiers())){
            builder = FilterBuilder.filter(filterMethod, source);
        }else{
            builder = FilterBuilder.filter(filter.captured()[0], filterMethod, source);
        }
        return builder.build();
    }
    
    
    
}
