package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.ext.declarative.api.Wrapper;
import java.util.function.Function;

/**
 * A wrapper that can provide stream like 
 * @author V12 Technology Ltd.
 */
public class Stream<T> implements Wrapper<T>{

    private final T underlying;

    public Stream(T underlying) {
        this.underlying = underlying;
    }

    public Stream(Wrapper<T> underlying) {
        this.underlying = underlying.event();
    }
    
    public static <S> Stream<S> stream(S in){
        return new Stream<S>(in);
    }
    
    public <S> Stream<T> filter(Function<T, S> supplier, Function<T, S> filter){
        return new Stream(underlying);
    }
    
    @Override
    public T event() {
        return underlying;
    }

    @Override
    public Class<T> eventClass() {
        return (Class<T>) underlying.getClass();
    }
   
//    public static Stream(T t)
    
}
