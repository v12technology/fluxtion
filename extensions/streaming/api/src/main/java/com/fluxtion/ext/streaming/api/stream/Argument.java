package com.fluxtion.ext.streaming.api.stream;

import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.numeric.ConstantNumber;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Representation of a an argument
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 * @param <T>
 */
public class Argument<T> {

    public Object source;
    public Method accessor;
    public boolean cast;

    public static <T extends Number> Argument<T> arg(Double d) {
        LambdaReflection.SerializableFunction<Number, Double> s = Number::doubleValue;
        return new Argument(new ConstantNumber(d), s.method(), true);
    }

    public static <T extends Number> Argument<T> arg(int d) {
        LambdaReflection.SerializableFunction<Number, Integer> s = Number::intValue;
        return new Argument(new ConstantNumber(d), s.method(), true);
    }

    public static <T extends Number> Argument<T> arg(long d) {
        LambdaReflection.SerializableFunction<Number, Long> s = Number::longValue;
        return new Argument(new ConstantNumber(d), s.method(), true);
    }

    public static <T extends Number> Argument<Double> arg(Wrapper<T> wrapper) {
        return arg(wrapper, Number::doubleValue);
    }

    public static <T, S> Argument<S> arg(Wrapper<T> wrapper, SerializableFunction<T, S> supplier) {
        return new Argument(wrapper, supplier.method(), true);
    }

    public static <T> Argument<T> arg(LambdaReflection.SerializableSupplier<T> supplier) {
        return new Argument(supplier.captured()[0], supplier.method(), true);
    }

    public static Argument arg(Object supplier) {
        return new Argument(supplier, null, true);
    }

    public Argument(Object source, Method accessor, boolean cast) {
        this.source = source;
        this.accessor = accessor;
        this.cast = cast;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Method getAccessor() {
        return accessor;
    }

    public void setAccessor(Method accessor) {
        this.accessor = accessor;
    }

    public boolean isCast() {
        return cast;
    }

    public void setCast(boolean cast) {
        this.cast = cast;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.source);
        hash = 67 * hash + Objects.hashCode(this.accessor);
        hash = 67 * hash + (this.cast ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Argument<?> other = (Argument<?>) obj;
        if (this.cast != other.cast) {
            return false;
        }
        if (!Objects.equals(this.source, other.source)) {
            return false;
        }
        if (!Objects.equals(this.accessor, other.accessor)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Argument{" + "source=" + source + ", accessor=" + accessor + ", cast=" + cast + '}';
    }
}
