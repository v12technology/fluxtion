package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.ext.declarative.api.Wrapper;
import java.util.function.Function;

/**
 * A wrapper that can provide stream like 
 * @author V12 Technology Ltd.
 */
public class Wrapper2Stream<T> implements Wrapper<T>{

    private final T underlying;

    public Wrapper2Stream(T underlying) {
        this.underlying = underlying;
    }

    public Wrapper2Stream(Wrapper<T> underlying) {
        this.underlying = underlying.event();
    }
    
    public static <S> Wrapper2Stream<S> stream(S in){
        return new Wrapper2Stream<S>(in);
    }
    
    public <S> Wrapper2Stream<T> filter(Function<T, S> supplier, Function<T, S> filter){
        return new Wrapper2Stream(underlying);
    }
    
    @Override
    public T event() {
        return underlying;
    }

    @Override
    public Class<T> eventClass() {
        return (Class<T>) underlying.getClass();
    }
   
//    public static Wrapper2Stream(T t)
    
}
