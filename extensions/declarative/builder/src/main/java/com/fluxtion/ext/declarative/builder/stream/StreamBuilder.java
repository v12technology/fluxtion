package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.ext.declarative.api.StreamOperator;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.google.auto.service.AutoService;
import java.lang.reflect.Method;

/**
 *
 * @author V12 Technology Ltd.
 */
@AutoService(StreamOperator.class)
public class StreamBuilder implements StreamOperator{

    @Override
    public <T> Wrapper<T> filter(LambdaReflection.SerializableFunction<T, Boolean> filter, Wrapper<T> source, Method accessor, boolean cast) {
        System.out.println("cool this is working");
        return StreamOperator.super.filter(filter, source, accessor, cast); //To change body of generated methods, choose Tools | Templates.
    }
    
}
